package android.camant.com.elibraryandroid.adapters;

import android.camant.com.elibraryandroid.R;
import android.camant.com.elibraryandroid.holders.BookViewHolder;
import android.camant.com.elibraryandroid.holders.CategoryViewHolder;
import android.camant.com.elibraryandroid.models.BaseModel;
import android.camant.com.elibraryandroid.models.Book;
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

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {
    private List<Book> books = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public BookAdapter(List<Book> books) {
        this.books = books;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View child = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item_view, parent, false);
        return new BookViewHolder(child);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        holder.applyData(books.get(position), position, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
