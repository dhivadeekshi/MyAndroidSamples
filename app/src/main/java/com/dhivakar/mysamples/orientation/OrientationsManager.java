package com.dhivakar.mysamples.orientation;

import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.dhivakar.mysamples.BaseAppCompatActivity;
import com.dhivakar.mysamples.R;
import com.dhivakar.mysamples.utils.LogUtils;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER;

public class OrientationsManager extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientations_manager);

        SetButtonClickListener(R.id.buttonForcePortrait, this);
        SetButtonClickListener(R.id.buttonForceLandscape, this);
        SetButtonClickListener(R.id.buttonUserOrientation, this);
        SetButtonClickListener(R.id.buttonPortraitOrientation, this);
        SetButtonClickListener(R.id.buttonLandscapeOrientation, this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch(v.getId())
        {
            case R.id.buttonForcePortrait: ForceDeviceOrientationTo(SCREEN_ORIENTATION_PORTRAIT); break;
            case R.id.buttonForceLandscape: ForceDeviceOrientationTo(SCREEN_ORIENTATION_LANDSCAPE); break;
            case R.id.buttonUserOrientation: SetAppOrientationTo(SCREEN_ORIENTATION_USER); break;
            case R.id.buttonPortraitOrientation: SetAppOrientationTo(SCREEN_ORIENTATION_PORTRAIT); break;
            case R.id.buttonLandscapeOrientation: SetAppOrientationTo(SCREEN_ORIENTATION_LANDSCAPE); break;
        }
    }

    private void GetPermissionToChangeDeviceSettings()
    {
        DisplayInfo(R.id.orientation, R.string.message_no_settings_permission, R.string.action_open_settings, Snackbar.LENGTH_SHORT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoSettingsPage();
            }
        });
    }

    private void ForceDeviceOrientationTo(int orientation)
    {
        LogUtils.i(this, "ForceDeviceOrientationTo "+orientation);
        if(CheckIfWeCanChangeDeviceSettings())
            Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, orientation);
        else
            GetPermissionToChangeDeviceSettings();
    }

    private void SetAppOrientationTo(int orientation)
    {
        LogUtils.i(this, "SetAppOrientationTo "+orientation);
    }
}
