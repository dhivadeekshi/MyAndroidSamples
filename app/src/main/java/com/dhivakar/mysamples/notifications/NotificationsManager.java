package com.dhivakar.mysamples.notifications;

import android.os.Bundle;
import android.util.Log;

import com.dhivakar.mysamples.BaseAppCompatActivity;
import com.dhivakar.mysamples.R;

public class NotificationsManager extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_manager);
    }

    public static void CreateNotificationChannel(String channelId)
    {

    }

    @Override
    public void onBackPressed() {
        Log.i(getLocalClassName(), "onBackPressed");
        super.onBackPressed();
    }
}
