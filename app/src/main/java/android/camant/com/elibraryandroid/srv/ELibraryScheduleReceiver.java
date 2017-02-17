package android.camant.com.elibraryandroid.srv;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.camant.com.elibraryandroid.utils.MainUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ELibraryScheduleReceiver extends BroadcastReceiver {
    public static final String ACTION_SYNC = "com.camant.android.elibrary.action.SYNC";
    public static final String ACTION_SYNC_NOW = "com.camant.android.elibrary.action.SYNC_NOW";
    public static final String ACTION_SYNC_COMPLETE = "com.camant.android.elibrary.action.SYNC_COMPLETE";
    //public static final String ACTION_SYNC = "com.camant.android.ACTION_SYNC";
    private static final String TAG = ELibraryScheduleReceiver.class.getSimpleName();
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private ELibraryServer service;
    private static final int SYNC_INTERVAL = 10;
    private static final String BASE_URL = "http://camant.com/elibrary/api/";
    private long startTime;
    public ELibraryScheduleReceiver() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ELibraryServer.class);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        //Log.d("ELibraryScheduleReceiver", ""+intent.getAction());
        if(intent.getAction().equals(ACTION_SYNC_NOW)){
            setAlarm(context);
        }
        //if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        Message msg = mServiceHandler.obtainMessage();
        msg.obj = context;
        mServiceHandler.sendMessage(msg);

        wl.release();
        //}

    }

    public static void setAlarm(Context context)
    {
        // cancel previous alarm if exists
        cancelAlarm(context);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, ELibraryScheduleReceiver.class);
        i.setAction(ACTION_SYNC);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * SYNC_INTERVAL, pi); // Millisec * Second * Minute
    }

    public static void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, ELibraryScheduleReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            Context context = (Context)msg.obj;
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (MainUtil.isConnected(context)) {
                    try {
                        // Put here YOUR code.
                        syncBooks(context);
                        syncCategories(context);
                        Log.d(TAG, "Alarm !!!!!!!!!!"); // For example
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // Restore interrupt status.
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        public void syncBooks(Context context){
            BookSynchronizer bookSynchronizer = new BookSynchronizer(context);
            try {
                bookSynchronizer.sync(BASE_URL, 0);
            } catch(SocketTimeoutException e){
                Log.e(TAG, Log.getStackTraceString(e));
            } catch (SocketException e){
                Log.e(TAG, Log.getStackTraceString(e));
            } catch (IOException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        public void syncCategories(Context context){
            CategorySynchronizer categorySynchronizer = new CategorySynchronizer(context);
            try {
                categorySynchronizer.sync(BASE_URL, 0);
                context.sendBroadcast(new Intent(ACTION_SYNC_COMPLETE));
            }catch(SocketTimeoutException e){
                Log.e(TAG, Log.getStackTraceString(e));
            } catch (SocketException e){
                Log.e(TAG, Log.getStackTraceString(e));
            } catch (IOException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
    }

    private long downloadImage (Context context, Uri uri, String subFolderName) {

        long downloadReference;
        String ext = ".png";
        if(uri.toString().lastIndexOf('.')>0){
            ext = uri.toString().substring(uri.toString().lastIndexOf('.'));
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
        File f = Environment.getExternalStoragePublicDirectory("ParkingKD");
        f = new File(f, subFolderName);
        if(!f.exists()) f.mkdir();
        if(!f.exists()) return -1;
        File imageFile = new File(f, md5Name+ext);
        if(imageFile.exists()) return -2;
        request.setDestinationUri(Uri.parse("file://" + f.getAbsolutePath() + "/" + md5Name + ext));

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

}
