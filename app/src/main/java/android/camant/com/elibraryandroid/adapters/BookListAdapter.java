package android.camant.com.elibraryandroid.adapters;

import android.camant.com.elibraryandroid.R;
import android.camant.com.elibraryandroid.db.BaseDbHelper;
import android.camant.com.elibraryandroid.models.Book;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Institute on 1/26/2017.
 */

public class BookListAdapter extends ArrayAdapter<Book> {
    private final List<Book> books;

    public BookListAdapter(Context context, List<Book> books) {
        super(context, android.R.layout.simple_list_item_1);
        this.books = books;
    }

    @Nullable
    @Override
    public Book getItem(int position) {
        return books.get(position);
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_suggestion_item_view, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imgThumb);
        TextView textView = (TextView) convertView.findViewById(R.id.txtTitle);
        Book book = books.get(position);
        if(book != null) {
            if (imageView != null) {
                Glide.with(convertView.getContext()).load(BaseDbHelper.BOOK_FILES_PATH + book.getThumbnail_local()).into(imageView);
            }
            if(textView != null){
                textView.setText(book.getTitle());
            }
        }
        return convertView;
    }

}
