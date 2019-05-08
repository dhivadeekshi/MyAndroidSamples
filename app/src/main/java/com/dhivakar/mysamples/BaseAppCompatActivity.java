package com.dhivakar.mysamples;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SignalStrength;
import android.view.View;
import android.widget.Toast;
import android.support.design.widget.Snackbar;
import android.widget.Toolbar;

import com.crashlytics.android.Crashlytics;
import com.dhivakar.mysamples.utils.LogUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

public class BaseAppCompatActivity extends AppCompatActivity implements View.OnClickListener{

    protected String ClassName = "";
    protected String TAG = "BaseAppCompatActivity";
    protected static FirebaseAnalytics mFirebaseAnalytics;
    private static void CreateFireBaseAnalytics(Context context)
    {
        // Obtain the FirebaseAnalytics instance.
        if(mFirebaseAnalytics == null)
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        ClassName = this.getClass().getSimpleName();
        TAG = this.getClass().getSimpleName();
        CreateFireBaseAnalytics(this);
        LogActivityLaunch();
        Crashlytics.log("Activity Loaded : "+ClassName);
        Crashlytics.setString("MenuLoaded",ClassName);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.i(this, "onNewIntent");
    }

    @Override
    protected void onPause() {
        LogUtils.i(this, "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i(this, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.i(this, "onRestart");
    }

    @Override
    protected void onDestroy() {
        LogUtils.i(this, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        LogUtils.i(this, "onButtonClick "+v.getId());
    }

    protected void SetButtonClickListener(int buttonId, final Context context, final Class<?> activityToLaunch)
    {
        if(context != null && activityToLaunch != null) {
            SetButtonClickListener(buttonId, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, activityToLaunch);
                    context.startActivity(intent);
                }
            });
        }
    }

    protected void SetButtonClickListener(int buttonId, View.OnClickListener onClick)
    {
        if(findViewById(buttonId) != null)
        {
            findViewById(buttonId).setOnClickListener(onClick);
        }
    }

    protected void UpdateActivityHeader(String header)
    {
        UpdateActivityHeader(header, "");
    }

    protected void UpdateActivityHeader(String header, String subTitle)
    {
        try {
            getSupportActionBar().setTitle(header);
            getSupportActionBar().setSubtitle(subTitle);
        }catch (NullPointerException e){
            Crashlytics.log("Exception in UpdateActivityHeader:"+e.getMessage());
            Crashlytics.logException(e);
        }
    }

    protected void UpdateActivityHeader(String header, String subTitle, Color color)
    {
        try {
            getSupportActionBar().setTitle(header);
            getSupportActionBar().setSubtitle(subTitle);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.image_android_versions));
        }catch (NullPointerException e){
            Crashlytics.log("Exception in UpdateActivityHeader:"+e.getMessage());
            Crashlytics.logException(e);
        }
    }

    protected void DisplayInfo(int viewId, int messageId, int actionId, int duration, View.OnClickListener onClick)
    {
        Snackbar mySnackbar = Snackbar.make(findViewById(viewId), messageId, duration);
        mySnackbar.setAction(actionId, onClick);
        mySnackbar.show();
    }

    protected void DisplayToast(String message, int duration)
    {
        Toast.makeText(getApplicationContext(), message, duration).show();
    }

    protected void DisplayShortToast(String message)
    {
        DisplayToast(message, Toast.LENGTH_SHORT);
    }

    protected void DisplayLongToast(String message)
    {
        DisplayToast(message, Toast.LENGTH_LONG);
    }

    protected void DisplayToast(int messageId, int duration)
    {
        Toast.makeText(getApplicationContext(), messageId, duration).show();
    }

    protected void DisplayShortToast(int messageId)
    {
        DisplayToast(messageId, Toast.LENGTH_SHORT);
    }

    protected void DisplayLongToast(int messageId)
    {
        DisplayToast(messageId, Toast.LENGTH_LONG);
    }

    protected boolean CheckIfWeCanChangeDeviceSettings()
    {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.M || Settings.System.canWrite(this);
    }

    protected void GotoSettingsPage()
    {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    protected void LaunchURL(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }


    // Firebase Analytics
    private void LogActivityLaunch()
    {
        Bundle bundle = new Bundle();
        bundle.putString("activity_name",this.getClass().getSimpleName());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
    }

    private void  LogActivityCreated()
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
    }
    // -----------------------------
}
