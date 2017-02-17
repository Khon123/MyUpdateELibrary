package android.camant.com.elibraryandroid.adapters;

import android.camant.com.elibraryandroid.R;
import android.camant.com.elibraryandroid.fragments.BaseFragment;
import android.camant.com.elibraryandroid.fragments.BooksListFragment;
import android.camant.com.elibraryandroid.fragments.CategoriesFragment;
import android.camant.com.elibraryandroid.fragments.MaterialFragment;
import android.camant.com.elibraryandroid.fragments.MoreFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by sreng on 2/5/2017.
 */

public class LibraryFragmentPagerAdapter extends FragmentPagerAdapter {
    public LibraryFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = new MaterialFragment();
        Bundle bundle = new Bundle();
        switch (position){
            case 1:
                fragment = new BooksListFragment();
                bundle.putBoolean("downloaded", true);
                bundle.putInt("title", R.string.downloaded);
                break;
            case 2:fragment = new MoreFragment();
                bundle.putInt("title", R.string.more);
            default:
                bundle.putInt("title", R.string.library);
                break;
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }
}
