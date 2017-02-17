package android.camant.com.elibraryandroid;

import android.camant.com.elibraryandroid.activities.CategoriesActivity;
import android.camant.com.elibraryandroid.activities.CategoriesFragmentActivity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent intent = new Intent();
                //intent.setAction(Constants.ACTIVITY_CATEGORIES);
                Intent intent = new Intent(SplashActivity.this, CategoriesFragmentActivity.class);
                startActivity(intent);
                finish();
            }
        }, 500);
    }
}
