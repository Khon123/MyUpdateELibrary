package android.camant.com.elibraryandroid.fragments;


import android.app.Activity;
import android.camant.com.elibraryandroid.activities.BaseActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.Toolbar;
import android.text.BoringLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.camant.com.elibraryandroid.R;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment implements Toolbar.OnMenuItemClickListener {
    protected View view;
    protected Toolbar toolbar;
    protected TextView activityBack, activityNext, activityTitle;
    protected Button buttonBack;
    protected IFragmentInteraction fragmentInteraction;

    public BaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_base, container, false);
        if(getActivity() instanceof BaseActivity){
            BaseActivity b = (BaseActivity)getActivity();
            activityBack = b.activityBack;
            activityNext = b.activityNext;
            activityTitle = b.activityTitle;
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        if(toolbar != null){
            onCreateOptionsMenu(toolbar.getMenu());
            onPrepareOptionsMenu(toolbar.getMenu());
            toolbar.setOnMenuItemClickListener(this);
            toolbar.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MenuItem item = new ActionMenuItem(getContext(),0, R.id.home,0,0,"");
                    onOptionsItemSelected(item);
                }
            });
            toolbar.addView(getLayoutInflater().inflate(R.layout.action_bar, null), new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL));
            activityTitle = (TextView)toolbar.findViewById(R.id.activity_title);
            activityBack = (TextView)toolbar.findViewById(R.id.activity_back);
            activityNext = (TextView)toolbar.findViewById(R.id.activity_next);
            buttonBack = (Button)toolbar.findViewById(R.id.home);
            activityTitle.setText(getTitle());
        }
    }
    protected String getTitle(){
        return getString(R.string.app_name);
    }

    protected View findViewById(int resId){
        if(view != null) return view.findViewById(resId);
        return null;
    }
    public Context getContext(){
        if(getActivity() != null) return getActivity();
        if(view != null) return view.getContext();
        return null;
    }
    public boolean sendBroadcast(Intent intent){
        if(getActivity() != null){
            getActivity().sendBroadcast(intent);
            return true;
        }
        return false;
    }
    protected void registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter){
        if(getActivity() != null){
            getActivity().registerReceiver(broadcastReceiver, intentFilter);
        }
    }
    protected void unregisterReceiver(BroadcastReceiver broadcastReceiver){
        if(getActivity() != null){
            getActivity().unregisterReceiver(broadcastReceiver);
        }
    }
    protected boolean onCreateOptionsMenu(Menu menu){
        return false;
    }
    protected MenuInflater getMenuInflater(){
        return getActivity().getMenuInflater();
    }
    protected LayoutInflater getLayoutInflater(){
        if(getActivity()!= null) return LayoutInflater.from(getActivity());
        return null;
    }
    protected void runOnUiThread(Runnable runnable){
        if(getActivity() != null){
            getActivity().runOnUiThread(runnable);
        }
    }
    protected void setContentView(int resId, ViewGroup container){
        view = getLayoutInflater().inflate(resId, container, false);
    }
    protected Intent getIntent(){
        if(getArguments() == null) return null;
        Intent intent = new Intent();
        intent.putExtras(getArguments());
        return intent;
    }
    protected Toolbar getSupportActionBar(){
        return toolbar;
    }
    protected void setDisplayHomeAsUpEnabled(boolean enabled){
        if(enabled){
            toolbar.findViewById(R.id.home).setVisibility(View.VISIBLE);
        }else{
            toolbar.findViewById(R.id.home).setVisibility(View.GONE);
        }
    }

    public void setFragmentInteraction(IFragmentInteraction fragmentInteraction) {
        this.fragmentInteraction = fragmentInteraction;
    }
    public void startFragment(Class<? extends Fragment> clazz){
        startFragment(clazz, null);
    }
    public void startFragment(Class<? extends Fragment> clazz, String backStackLabel){
        if(fragmentInteraction!=null){
            try {
                fragmentInteraction.replaceFragment(clazz.newInstance(), backStackLabel);
            } catch (java.lang.InstantiationException e) {
                Log.e("BaseFragment", Log.getStackTraceString(e));
            } catch (IllegalAccessException e) {
                Log.e("BaseFragment", Log.getStackTraceString(e));
            }
        }
    }
    public void startFragment(Fragment fragment){
        startFragment(fragment, null);
    }
    public void startFragment(Fragment fragment, String backStackLabel){
        if(fragmentInteraction != null){
            fragmentInteraction.replaceFragment(fragment, backStackLabel);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return onOptionsItemSelected(item);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        return false;
    }
    protected void finish(){
        getFragmentManager().popBackStack();
    }
}
