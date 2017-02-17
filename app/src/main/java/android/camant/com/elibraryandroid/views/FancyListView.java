package android.camant.com.elibraryandroid.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Institute on 1/26/2017.
 */

public class FancyListView extends ListView implements AbsListView.OnScrollListener {
    private int currentFirstVisibleItem;
    private int currentVisibleItemCount;
    private int currentScrollState;
    private OnScrollBottomReachedListener onScrollBottomReachedListener;
    public FancyListView(Context context) {
        super(context);
    }

    public FancyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FancyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.currentFirstVisibleItem = firstVisibleItem;
        this.currentVisibleItemCount = visibleItemCount;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.currentScrollState = scrollState;
        this.isScrollCompleted();
    }

    private void isScrollCompleted() {
        if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {
            /*** In this way I detect if there's been a scroll which has completed ***/
            /*** do the work! ***/
            if(onScrollBottomReachedListener != null) onScrollBottomReachedListener.onScrollBottomReached();
        }
    }

    public void setOnScrollBottomReachedListener(OnScrollBottomReachedListener onScrollBottomReachedListener) {
        this.onScrollBottomReachedListener = onScrollBottomReachedListener;
    }

    public interface OnScrollBottomReachedListener{
        void onScrollBottomReached();
    }
}
