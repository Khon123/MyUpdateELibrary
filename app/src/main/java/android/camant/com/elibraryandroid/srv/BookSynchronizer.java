package android.camant.com.elibraryandroid.srv;

import android.camant.com.elibraryandroid.db.BaseDbHelper;
import android.camant.com.elibraryandroid.db.BookDbHelper;
import android.camant.com.elibraryandroid.utils.MainUtil;
import android.camant.com.elibraryandroid.wrappers.BookRelatedWrapper;
import android.camant.com.elibraryandroid.wrappers.BookWrapper;
import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Institute on 1/23/2017.
 */

public class BookSynchronizer {
    private Context context;
    public BookSynchronizer(Context context){
        this.context = context;
    }
    public void sync(String baseUrl, int start) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ELibraryServer server = retrofit.create(ELibraryServer.class);
        Call<BookWrapper> bookWrapperCall = server.listBooks(start, 20);
        Response<BookWrapper> response = bookWrapperCall.execute();
        if(response != null){
            BookWrapper bookWrapper = response.body();
            int count = insertOrUpdateBooksInDB(bookWrapper);
            if(count >= 20){
                sync(baseUrl, count);
            }
        }
    }
    public void async(final String baseUrl, int start) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ELibraryServer server = retrofit.create(ELibraryServer.class);
        Call<BookWrapper> bookWrapperCall = server.listBooks(start, 20);
        bookWrapperCall.enqueue(new Callback<BookWrapper>() {
            @Override
            public void onResponse(Call<BookWrapper> call, Response<BookWrapper> response) {
                if(response != null){
                    BookWrapper bookWrapper = response.body();
                    int count = insertOrUpdateBooksInDB(bookWrapper);
                    if(count >= 20){
                        async(baseUrl, count);
                    }
                }
            }

            @Override
            public void onFailure(Call<BookWrapper> call, Throwable t) {

            }
        });

    }
    private int insertOrUpdateBooksInDB(final BookWrapper bookWrapper){
        if(bookWrapper != null && bookWrapper.getDatas() != null && bookWrapper.getDatas().size() > 0){
            BookDbHelper bookDbHelper = new BookDbHelper(context);
            File file = new File(BaseDbHelper.DATABASE_FILE_PATH);
            file = new File(file, "book_files");
            if(!file.exists()) file.mkdir();
            Uri image_uri;
            if(file.exists()) {
                String bookFileLocal, thumb;
                for (BookRelatedWrapper wrapper : bookWrapper.getDatas()) {
                    // we don't download book file now, we download when user open it
                    //image_uri = Uri.parse(wrapper.getBook().getBook_file());
                    //bookFileLocal = MainUtil.download(context, image_uri, "book_files");
                    //wrapper.getBook().setBook_file_local(bookFileLocal);
                    image_uri = Uri.parse(wrapper.getBook().getThumbnail());
                    thumb = MainUtil.download(context, image_uri, "book_files");
                    wrapper.getBook().setThumbnail_local(thumb);
                    bookDbHelper.insertOrUpdateExcludeDownloadStat(wrapper.getBook());
                }
                if (bookWrapper.getDatas().size() >= 20) {
                    return bookDbHelper.getCount();
                }
            }
        }
        return -1;
    }
}
