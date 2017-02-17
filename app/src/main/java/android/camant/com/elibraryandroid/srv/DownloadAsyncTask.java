package android.camant.com.elibraryandroid.srv;

import android.app.DownloadManager;
import android.camant.com.elibraryandroid.Constants;
import android.camant.com.elibraryandroid.db.BookDbHelper;
import android.camant.com.elibraryandroid.models.Book;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.ProgressBar;

/**
 * Created by Institute on 2/2/2017.
 */

public class DownloadAsyncTask extends AsyncTask<Long, Integer, Boolean> {
    private Context context;
    private long downloadManagerId;
    private ProgressBar progressBar;
    private Book book;
    private String destinationFile;
    private int progress, oldProgress = 0;
    public DownloadAsyncTask(Context context, Book book,  ProgressBar progressBar){
        this.context = context;
        this.progressBar = progressBar;
        this.book = book;
    }

    @Override
    protected Boolean doInBackground(Long... longs) {
        this.downloadManagerId = longs[0]==null ? 0 : longs[0];

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        while (true) {

            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(downloadManagerId); //filter by id which you have receieved when reqesting download from download manager
            Cursor cursor = manager.query(q);
            cursor.moveToFirst();
            int bytes_downloaded = cursor.getInt(cursor
                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                cursor.close();
                return true;
            }else if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED) {
                cursor.close();
                break;
            }
            cursor.close();
            progress = (int) ((bytes_downloaded * 100L) / bytes_total);
            if(progress>oldProgress) {
                publishProgress(progress);
                oldProgress = progress;
            }
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(progressBar != null){
            progressBar.setProgress(values[0]);
        }else {
            Intent intent = new Intent(Constants.ACTION_DOWNLOAD_UPDATE);
            intent.putExtra("downloadManagerId", downloadManagerId);
            intent.putExtra("progress", values[0]);
            intent.putExtra("bookId", book.getId());
            context.sendBroadcast(intent);
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        BookDbHelper bookDbHelper = new BookDbHelper(context);
        Intent intent = new Intent(Constants.ACTION_DOWNLOAD_COMPLETE);
        intent.putExtra("downloadManagerId", downloadManagerId);
        intent.putExtra("progress", aBoolean);
        intent.putExtra("bookId", book.getId());
        book.setDownload_id(downloadManagerId);
        if(aBoolean){
            book.setProgress(100);
            intent.putExtra("book_file_local", destinationFile);
        }
        bookDbHelper.insertOrUpdate(book);
        context.sendBroadcast(intent);
    }

    public long getDownloadManagerId() {
        return downloadManagerId;
    }

    public Book getBook() {
        return book;
    }

    public void setDownloadManagerId(long downloadManagerId) {
        this.downloadManagerId = downloadManagerId;
    }

    public String getDestinationFile() {
        return destinationFile;
    }

    public void setDestinationFile(String destinationFile) {
        this.destinationFile = destinationFile;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
