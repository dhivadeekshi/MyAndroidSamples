package com.dhivakar.mysamples;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dhivakar.mysamples.notifications.NotificationsManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetButtonClickListener(R.id.BtnLaunchNotifications, this, NotificationsManager.class);

    }

    private void SetButtonClickListener(int buttonId, final Context context, final Class<?> activityToLaunch)
    {
        if(findViewById(buttonId) != null && context != null && activityToLaunch != null)
        {
            findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, activityToLaunch);
                    context.startActivity(intent);
                }
            });
        }
    }

}
