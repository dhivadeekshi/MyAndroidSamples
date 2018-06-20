package com.dhivakar.mysamples;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.dhivakar.mysamples.googleplaygames.GPGSAchievements;
import com.dhivakar.mysamples.notifications.NotificationsManager;
import com.dhivakar.mysamples.orientation.OrientationsManager;
import com.dhivakar.mysamples.externalapps.ExternalAppsManager;
import com.dhivakar.mysamples.utils.ListenToOrientaionChanges;
import com.dhivakar.mysamples.utils.LogUtils;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Locale;

public class MainActivity extends BaseAppCompatActivity implements ListenToOrientaionChanges.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetButtonClickListener(R.id.BtnLaunchNotifications, this, NotificationsManager.class);
        SetButtonClickListener(R.id.BtnLaunchOrientations, this, OrientationsManager.class);
        SetButtonClickListener(R.id.BtnLaunchExternalApps, this, ExternalAppsManager.class);
        SetButtonClickListener(R.id.BtnLaunchGPGSAchievements, this, GPGSAchievements.class);

        UpdateOrientationText(getResources().getConfiguration().orientation);
        ListenToOrientaionChanges.StartListening(this, this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch(v.getId())
        {
            case R.id.buttonTestCrash: CrashForTesting(); break;
        }
    }

    @Override
    protected void onPause() {
        LogUtils.i(this, "onPause");
        ListenToOrientaionChanges.get_intent().disable();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i(this, "onResume");
        ListenToOrientaionChanges.get_intent().disable();
    }

    @Override
    protected void onDestroy() {
        LogUtils.i(this, "onDestroy");
        // Being called on orientation change ??
        //ListenToOrientaionChanges.StopListening();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtils.i(this,"onConfigurationChanged newConfig:"+newConfig);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        LogUtils.i(this, "onOrientationChanged orientation:" + orientation);
        UpdateOrientationText(orientation);
    }

    private void UpdateOrientationText(final int orientation) {
        LogUtils.i(this, "UpdateOrientationText orientation:" + orientation);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView orientationText = (TextView) findViewById(R.id.orientation_description);
                orientationText.setText(String.format(Locale.US, "orientation:%d", orientation));
            }
        });
    }

    private void CrashForTesting()
    {
        FirebaseCrash.log("App Crashed for Testing");
        Crashlytics.getInstance().crash(); /* Force a crash */
    }

}
