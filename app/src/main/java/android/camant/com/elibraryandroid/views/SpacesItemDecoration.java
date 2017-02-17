package android.camant.com.elibraryandroid.views;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by sreng on 1/25/2017.
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int columns = 0;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        //outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if(columns <= 0) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                columns = ((GridLayoutManager) layoutManager).getSpanCount();
            }else{
                columns = 1;
            }
        }
        if (parent.getChildLayoutPosition(view) < columns) {
            outRect.top = 0;
        } else {
            outRect.top = space;
        }
        if(columns > 1){
            if(parent.getChildLayoutPosition(view) % columns == 0){
                outRect.right = 0;
            }else{
                outRect.right = space;
            }
        }else{
            outRect.right = space;
        }
    }
}
