package android.camant.com.elibraryandroid.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by sreng on 2/5/2017.
 */

public interface IFragmentInteraction {
    void replaceFragment(Fragment fragment);
    void replaceFragment(Fragment fragment, @Nullable String backStackLabel);
    void replaceFragmentNoBackTrack(Fragment fragment);
}
