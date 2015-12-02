package com.alonseg.iprotest.Activities;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.alonseg.iprotest.Consts;
import com.alonseg.iprotest.Objects.Log;
import com.alonseg.iprotest.R;

public class SplashScreen extends Activity {

    private static final String TAG = "SPLASH";
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Consts.appFont = Typeface.createFromAsset(getAssets(), "fonts/appFont.ttf");

        Intent intent = getIntent();
        Uri data;
        if (intent != null) {
            Log.d(TAG, "started with intent");
            data = intent.getData();
            if (data != null){
                String[] dataParts = data.toString().split("/", -1);
                if (dataParts == null || dataParts.length < Consts.ID_INDEX + 1){
                    Log.d(TAG, "uri is in wrong format");
                    startApp(SPLASH_TIME_OUT, null);
                }else {
                    Intent startIntent = getIntent(dataParts);
                    startApp(SPLASH_TIME_OUT, startIntent);
                    return;

                }

            }else {
                Log.d(TAG, "started without intent");
                startApp(SPLASH_TIME_OUT, null);
            }
        }
        else {
            Log.d(TAG, "started without intent");
            startApp(SPLASH_TIME_OUT, null);
        }


    }

    @NonNull
    private Intent getIntent(String[] dataParts) {
        Intent startIntent = new Intent(SplashScreen.this, ProtestActivity.class);
        if (dataParts[Consts.TYPE_INDEX].equals(Consts.PROTEST)) {
            startIntent.putExtra(Consts.P_ID, dataParts[Consts.ID_INDEX]);
        } else {
            startIntent.putExtra(Consts.TYPE, dataParts[Consts.TYPE_INDEX]);
            startIntent.putExtra(Consts.ID, dataParts[Consts.ID_INDEX]);
        }
        return startIntent;
    }

    private void startApp(int timeOut, final Intent intent) {
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, FollowActivity.class);
                startActivity(intent == null ? i : intent);

                // close this activity
                finish();
            }
        }, timeOut);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
