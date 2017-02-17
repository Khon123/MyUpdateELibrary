package android.camant.com.elibraryandroid.activities;

import android.Manifest;
import android.camant.com.elibraryandroid.R;
import android.camant.com.elibraryandroid.adapters.CategoryAdapter;
import android.camant.com.elibraryandroid.adapters.LibraryFragmentPagerAdapter;
import android.camant.com.elibraryandroid.adapters.OnItemClickListener;
import android.camant.com.elibraryandroid.db.CategoryDbHelper;
import android.camant.com.elibraryandroid.fragments.BaseFragment;
import android.camant.com.elibraryandroid.fragments.CategoriesFragment;
import android.camant.com.elibraryandroid.fragments.IFragmentInteraction;
import android.camant.com.elibraryandroid.models.BaseModel;
import android.camant.com.elibraryandroid.models.Category;
import android.camant.com.elibraryandroid.srv.ELibraryScheduleReceiver;
import android.camant.com.elibraryandroid.utils.MainUtil;
import android.camant.com.elibraryandroid.views.FancyRecyclerView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragmentActivity extends BaseActivity implements IFragmentInteraction, ViewPager.OnPageChangeListener, View.OnClickListener {
    private static final int REQUEST_CODE = 5;
    private LibraryFragmentPagerAdapter libraryFragmentPagerAdapter;
    private ViewPager mViewPager;
    private RadioGroup tabs;
    private Fragment mActivityDirectFragment;
    private int oldRadio = -1;
    private static final int[] selected_tab_icons = {R.drawable.ic_globe_blue, R.drawable.ic_downloaded_blue, R.drawable.ic_more_apps_blue};
    private static final int[] tab_icons = {R.drawable.ic_globe, R.drawable.ic_download, R.drawable.ic_more_apps};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_categories);
        libraryFragmentPagerAdapter =
                new LibraryFragmentPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(3);
        tabs = (RadioGroup)findViewById(R.id.tabs);
        tabs.check(tabs.getChildAt(0).getId());
        selectRadio(0);
        mActivityDirectFragment = libraryFragmentPagerAdapter.getItem(0);
        for(int i=0;i<tabs.getChildCount();i++){
            //((RadioButton)tabs.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(0, tab_icons[i], 0, 0);
            tabs.getChildAt(i).setOnClickListener(this);
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }else {
            sendBroadcast(new Intent(ELibraryScheduleReceiver.ACTION_SYNC_NOW));
            mViewPager.setAdapter(libraryFragmentPagerAdapter);
        }
    }
    private void selectRadio(int radioButton){
        Drawable drawable = null;
        if(oldRadio>=0){
            ((RadioButton)tabs.getChildAt(oldRadio)).setTextColor(getResources().getColor(R.color.colorBlack));
            drawable = getResources().getDrawable(tab_icons[oldRadio]);
            drawable.setBounds(new Rect(0,0,64,64));
            ((RadioButton)tabs.getChildAt(oldRadio)).setCompoundDrawablesWithIntrinsicBounds(0, tab_icons[oldRadio], 0, 0);
            ((RadioButton)tabs.getChildAt(oldRadio)).setCompoundDrawables(null, drawable, null, null);
        }
        drawable = getResources().getDrawable(selected_tab_icons[radioButton]);
        drawable.setBounds(new Rect(0,0,64,64));
        ((RadioButton)tabs.getChildAt(radioButton)).setTextColor(getResources().getColor(R.color.colorBlue));
        ((RadioButton)tabs.getChildAt(radioButton)).setCompoundDrawablesWithIntrinsicBounds(0, tab_icons[radioButton], 0, 0);
        ((RadioButton)tabs.getChildAt(radioButton)).setCompoundDrawables(null, drawable, null, null);
        oldRadio = radioButton;
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, null);
    }

    @Override
    public void replaceFragment(Fragment fragment, @Nullable String backStackLabel) {
        //getSupportFragmentManager().beginTransaction().addToBackStack(backStackLabel).replace(R.id.container, fragment, backStackLabel).commit();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tabs.check(tabs.getChildAt(position).getId());
        selectRadio(position);
        mActivityDirectFragment = libraryFragmentPagerAdapter.getItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void replaceFragmentNoBackTrack(Fragment fragment) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                sendBroadcast(new Intent(ELibraryScheduleReceiver.ACTION_SYNC_NOW));
                mViewPager.setAdapter(libraryFragmentPagerAdapter);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int index = tabs.indexOfChild(view);
        if(index >= 0 && mViewPager.getCurrentItem() != index) {
            mViewPager.setCurrentItem(index);
        }
    }
    @Override
    public void onBackPressed() {
        Log.e(getClass().getSimpleName(), ""+mActivityDirectFragment);
        if(mActivityDirectFragment != null){
            Log.e(getClass().getSimpleName(), "BackStack:"+mActivityDirectFragment.getChildFragmentManager().getBackStackEntryCount());

        }
        // If the fragment exists and has some back-stack entry
        if (mActivityDirectFragment != null && mActivityDirectFragment.getChildFragmentManager().getBackStackEntryCount() > 0){
            // Get the fragment fragment manager - and pop the backstack
            mActivityDirectFragment.getChildFragmentManager().popBackStack();
        }
        // Else, nothing in the direct fragment back stack
        else{
            // Let super handle the back press
            super.onBackPressed();
        }
    }
}
