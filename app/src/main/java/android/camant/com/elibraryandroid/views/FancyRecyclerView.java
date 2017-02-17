package android.camant.com.elibraryandroid.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Institute on 1/25/2017.
 */

public class FancyRecyclerView extends RecyclerView {
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LayoutManager mLayoutManager;
    private OnScrollBottomReachedListener onScrollBottomReachedListener;

    public FancyRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public FancyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    if(mLayoutManager == null){
                        mLayoutManager = getLayoutManager();
                    }
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    if(mLayoutManager instanceof LinearLayoutManager) {
                        pastVisiblesItems = ((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition();
                    }else if(mLayoutManager instanceof GridLayoutManager) {
                        pastVisiblesItems = ((GridLayoutManager)mLayoutManager).findFirstVisibleItemPosition();
                    }else{
                        //not supported
                        return;
                    }
                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            if(onScrollBottomReachedListener != null) onScrollBottomReachedListener.onScrollBottomReached();
                        }
                    }
                }
            }
        });
    }

    public void setOnScrollBottomReachedListener(OnScrollBottomReachedListener onScrollBottomReachedListener) {
        this.onScrollBottomReachedListener = onScrollBottomReachedListener;
    }

    public interface OnScrollBottomReachedListener{
        void onScrollBottomReached();
    }

}
