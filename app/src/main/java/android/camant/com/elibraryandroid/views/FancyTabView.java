package android.camant.com.elibraryandroid.views;

import android.camant.com.elibraryandroid.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.RadioGroup;

/**
 * Created by sreng on 2/5/2017.
 */

public class FancyTabView extends RadioGroup {
    public FancyTabView(Context context) {
        super(context);
        init(context, null);
    }

    public FancyTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){

        //LayoutInflater.from(context).inflate(R.layout.fancy_tab_view, this, false);
    }

}
