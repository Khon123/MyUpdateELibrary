package android.camant.com.elibraryandroid.holders;

import android.camant.com.elibraryandroid.R;
import android.camant.com.elibraryandroid.adapters.OnItemClickListener;
import android.camant.com.elibraryandroid.db.BaseDbHelper;
import android.camant.com.elibraryandroid.models.BaseModel;
import android.camant.com.elibraryandroid.models.Book;
import android.camant.com.elibraryandroid.models.Category;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by Institute on 1/25/2017.
 */

public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView textViewTitle;
    private ImageView imgThumb;
    private Book book;
    private int position;
    private OnItemClickListener onItemClickListener;
    private ProgressBar progressBar;
    public BookViewHolder(View itemView) {
        super(itemView);
        textViewTitle = (TextView)itemView.findViewById(R.id.txtTitle);
        imgThumb = (ImageView)itemView.findViewById(R.id.imgThumb);
        progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
        this.itemView.setOnClickListener(this);
    }
    public void applyData(final Book book, int position, OnItemClickListener onItemClickListener){
        this.book = book;
        this.position = position;
        this.onItemClickListener = onItemClickListener;
        if(textViewTitle != null){
            textViewTitle.setText(book.getTitle());
            //textViewTitle.setBackgroundColor(Color.RED);
        }
        if(imgThumb != null){
            String thumb;
            if(book.getThumbnail_local() != null){
                thumb = BaseDbHelper.DATABASE_FILE_PATH+"/book_files/"+book.getThumbnail_local();
            }else{
                thumb = book.getThumbnail();
            }
            Glide.with(itemView.getContext()).load(thumb).into(imgThumb);
        }
        if(progressBar!=null){
            progressBar.setProgress(book.getProgress());
        }
    }

    @Override
    public void onClick(View view) {
        if(onItemClickListener != null){
            onItemClickListener.onItemClick(book, position);
        }
    }
}
