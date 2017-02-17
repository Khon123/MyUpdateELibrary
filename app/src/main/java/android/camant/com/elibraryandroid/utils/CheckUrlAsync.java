package android.camant.com.elibraryandroid.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Institute on 10/19/2016.
 */

public class CheckUrlAsync extends AsyncTask<String,Void,Boolean> {
    private final OnResultCallback onResultCallback;

    public CheckUrlAsync(OnResultCallback onResultCallback){
        this.onResultCallback = onResultCallback;
    }
    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            String URLName = strings[0];
            if(URLName == null) return false;
            Log.d("CheckUrlAsync", ""+URLName);
            if(URLName.startsWith("file")|| URLName.startsWith("/")){
                File f = new File(URLName);
                return f.exists();
            }
            Log.d("CheckUrlAsync","success1:"+URLName);
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            con.setReadTimeout(5000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            con.setConnectTimeout(5000);
            // For this use case, set HTTP method to GET.
            con.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            con.setDoInput(true);
            // Open communications link (network traffic occurs here).
            con.connect();
            boolean ret = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            Log.d("CheckUrlAsync","success:"+URLName);
            con.disconnect();
            return ret;
        }
        catch (Exception e) {
            Log.d("CheckUrlAsync","failed:"+Log.getStackTraceString(e));
            if(new File(strings[0]).exists()) return true;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if(onResultCallback != null){
            onResultCallback.onResult(aBoolean);
        }
    }

    public interface OnResultCallback{
        void onResult(boolean result);
    }
}
