package android.camant.com.elibraryandroid.adapters;

import android.camant.com.elibraryandroid.R;
import android.camant.com.elibraryandroid.db.BaseDbHelper;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by Institute on 1/26/2017.
 */

public class BookCursorAdapter extends SimpleCursorAdapter {
    private ImageView imageView;
    private TextView textView;

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, android.R.layout.simple_list_item_1, c, new String[]{"title"}, new int[]{R.id.txtTitle});
    }

    public BookCursorAdapter(Context context, Cursor c, int flags) {
        super(context, android.R.layout.simple_list_item_1, c, new String[]{"title"}, new int[]{R.id.txtTitle}, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_suggestion_item_view, parent, false);
        imageView = (ImageView)view.findViewById(R.id.imgThumb);
        textView = (TextView)view.findViewById(R.id.txtTitle);
        if(imageView != null){
            Glide.with(context).load(BaseDbHelper.DATABASE_FILE_PATH+"/book_files/"+cursor.getString(cursor.getColumnIndex("thumbnail_local")));
        }
        if(textView != null){
            textView.setText(cursor.getString(cursor.getColumnIndex("title")));
        }
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        imageView = (ImageView)view.findViewById(R.id.imgThumb);
        textView = (TextView)view.findViewById(R.id.txtTitle);
        if(imageView != null){
            Glide.with(context).load(BaseDbHelper.DATABASE_FILE_PATH+"/book_files/"+cursor.getString(cursor.getColumnIndex("thumbnail_local")));
        }
        if(textView != null){
            textView.setText(cursor.getString(cursor.getColumnIndex("title")));
        }
    }
}
