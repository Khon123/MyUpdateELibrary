package android.camant.com.elibraryandroid.holders;

import android.camant.com.elibraryandroid.R;
import android.camant.com.elibraryandroid.adapters.OnItemClickListener;
import android.camant.com.elibraryandroid.models.Category;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Institute on 1/25/2017.
 */

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView textViewName;
    private TextView textViewBooksCount;
    private OnItemClickListener onItemClickListener;
    private Category category;
    private int position;
    public CategoryViewHolder(View itemView) {
        super(itemView);
        textViewName = (TextView)itemView.findViewById(R.id.txtName);
        textViewBooksCount = (TextView)itemView.findViewById(R.id.txtBooksCount);
        this.itemView.setOnClickListener(this);
    }
    public void applyData(final Category category, int position, OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
        this.category = category;
        this.position = position;
        if(textViewName != null){
            textViewName.setText(category.getName());
        }
        if(textViewBooksCount != null){
            textViewBooksCount.setText(""+category.getBooks_count());
        }
    }

    @Override
    public void onClick(View view) {
        if(onItemClickListener != null){
            onItemClickListener.onItemClick(category, position);
        }
    }
}
