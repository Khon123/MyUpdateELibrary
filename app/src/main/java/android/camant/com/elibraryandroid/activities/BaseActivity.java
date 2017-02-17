package android.camant.com.elibraryandroid.activities;

import android.camant.com.elibraryandroid.R;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BaseActivity extends AppCompatActivity implements MenuItemCompat.OnActionExpandListener {
    public TextView activityBack, activityNext, activityTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(getLayoutInflater().inflate(R.layout.action_bar, null), new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL));
            activityTitle = (TextView)actionBar.getCustomView().findViewById(R.id.activity_title);
            activityBack = (TextView)actionBar.getCustomView().findViewById(R.id.activity_back);
            activityNext = (TextView)actionBar.getCustomView().findViewById(R.id.activity_next);
            activityTitle.setText(getTitle());

        }
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        activityTitle.setVisibility(View.GONE);
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        activityTitle.setVisibility(View.VISIBLE);
        return false;
    }
}
