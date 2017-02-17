package android.camant.com.elibraryandroid.utils;

import android.app.DownloadManager;
import android.camant.com.elibraryandroid.db.BaseDbHelper;
import android.camant.com.elibraryandroid.models.Book;
import android.camant.com.elibraryandroid.srv.DownloadAsyncTask;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ProgressBar;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Institute on 1/14/2017.
 */

public abstract class MainUtil {
    /**
     * Take the string prefix if not empty|null, plus s, plus suffix if not empty|null.<br/>
     * If s is null, will return empty string not null without prefix and suffix.
     * @param s input string or null
     * @param prefix adding ahead of s, if not null
     * @param suffix adding tail of s, if not null
     * @return empty string if s is null or empty. Otherwise, prefix + s + suffix
     */
    public static String stringNullEmpty(String s, String prefix, String suffix){
        if(s == null || s.trim().isEmpty()) return "";
        String ret = s;
        if(prefix != null) ret = prefix + ret;
        if(suffix != null) ret = ret + suffix;
        return ret;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return s;
    }
    public static DownloadAsyncTask asyncDownload (Context context, Book book, ProgressBar progressBar) {
        DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(context, book, progressBar);
        Uri uri = Uri.parse(book.getBook_file());
        String subFolderName = "book_files";
        String ext = "";
        long downloadReference;
        if(uri.toString().lastIndexOf('.')>0){
            ext = uri.toString().substring(uri.toString().lastIndexOf('.'));
        }else if(ext == null) {
            ext = "";
        }
        String md5Name = MainUtil.md5(uri.toString());
        downloadAsyncTask.setDestinationFile(md5Name+ext);

        // Create request for android download manager
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle(book.getTitle());

        //Setting description of request
        request.setDescription("Downloading needed book file.");

        //Set the local destination for the downloaded file to a path within the application's external files directory
        File f = new File(BaseDbHelper.DATABASE_FILE_PATH);
        f = new File(f, subFolderName);
        if(!f.exists()) f.mkdir();
        if(!f.exists()) return null;
        File imageFile = new File(f, md5Name+ext);
        if(imageFile.exists()){
            downloadAsyncTask.setDownloadManagerId(-1L);
            downloadAsyncTask.setProgress(100);
            return downloadAsyncTask;
        }
        request.setDestinationUri(Uri.parse("file://" + f.getAbsolutePath() + "/" + md5Name + ext));

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);
        downloadAsyncTask.execute(downloadReference);
        return downloadAsyncTask;
    }
    public static String download (Context context, Uri uri, String subFolderName) {
        return download(context, uri, subFolderName, null);
    }
    public static String download (Context context, Uri uri, String subFolderName, String ext) {

        long downloadReference;
        if(uri.toString().lastIndexOf('.')>0){
            ext = uri.toString().substring(uri.toString().lastIndexOf('.'));
        }else if(ext == null) {
            ext = "";
        }
        String md5Name = MainUtil.md5(uri.toString());

        // Create request for android download manager
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Download Staff Image");

        //Setting description of request
        request.setDescription("Staff avatar are persisted.");

        //Set the local destination for the downloaded file to a path within the application's external files directory
        File f = new File(BaseDbHelper.DATABASE_FILE_PATH);
        f = new File(f, subFolderName);
        if(!f.exists()) f.mkdir();
        if(!f.exists()) return null;
        File imageFile = new File(f, md5Name+ext);
        if(imageFile.exists()) return md5Name+ext;
        request.setDestinationUri(Uri.parse("file://" + f.getAbsolutePath() + "/" + md5Name + ext));

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        return md5Name + ext;
    }

    public static int dp2px(Context context, int dp){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    public static String getBookFileLocalFullPath(String fileName){
        if(fileName == null || fileName.isEmpty()) return null;
        File f = new File(BaseDbHelper.BOOK_FILES_PATH+fileName);
        if(!f.exists()) return null;
        return f.getAbsolutePath();
    }
    public static void exists(String URLName, CheckUrlAsync.OnResultCallback onResultCallback){
        CheckUrlAsync checkUrlAsync = new CheckUrlAsync(onResultCallback);
        checkUrlAsync.execute(URLName);
    }
}
