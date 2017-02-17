package android.camant.com.elibraryandroid.adapters;

import android.camant.com.elibraryandroid.R;
import android.camant.com.elibraryandroid.holders.CategoryViewHolder;
import android.camant.com.elibraryandroid.models.Category;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Institute on 1/25/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    private List<Category> categories = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View child = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_view, parent, false);

        return new CategoryViewHolder(child);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        holder.applyData(categories.get(position), position, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
