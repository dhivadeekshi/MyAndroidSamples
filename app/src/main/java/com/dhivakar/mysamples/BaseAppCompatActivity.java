package com.dhivakar.mysamples;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import android.support.design.widget.Snackbar;

import com.dhivakar.mysamples.utils.LogUtils;

public class BaseAppCompatActivity extends AppCompatActivity implements View.OnClickListener{

    protected String ClassName = "";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        ClassName = this.getClass().getSimpleName();
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
}
