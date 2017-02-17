package android.camant.com.elibraryandroid.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.camant.com.elibraryandroid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MaterialFragment extends BaseFragment implements IFragmentInteraction {


    public MaterialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view=inflater.inflate(R.layout.fragment_material, container, false);
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        if(getArguments() != null){
            categoriesFragment.setArguments(getArguments());
        }
        categoriesFragment.setFragmentInteraction(this);
        replaceFragmentNoBackTrack(categoriesFragment);
        return view;
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, null);
    }

    @Override
    public void replaceFragment(Fragment fragment, @Nullable String backStackLabel) {
        getChildFragmentManager().beginTransaction().addToBackStack(backStackLabel)
                .replace(R.id.childContainer, fragment, backStackLabel).commit();
    }

    @Override
    public void replaceFragmentNoBackTrack(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.childContainer, fragment).commit();
    }

}
