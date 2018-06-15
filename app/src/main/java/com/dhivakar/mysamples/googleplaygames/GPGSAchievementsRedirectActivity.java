package com.dhivakar.mysamples.googleplaygames;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dhivakar.mysamples.BaseAppCompatActivity;
import com.dhivakar.mysamples.utils.LogUtils;

public class GPGSAchievementsRedirectActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        HandleIntent(intent,"onCreate");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        HandleIntent(intent, "onNewIntent");
    }

    private void HandleIntent(Intent intent, String source) {
        if (intent != null) {
            LogUtils.i(this, "Received Intent data "+source);
            Bundle extras = intent.getExtras();
            if(extras != null) {
                String authCode = extras.getString("code");
                if (authCode != null)
                    LogUtils.i(this, "AuthCode Received : " + authCode);
            }
        } else {
            LogUtils.i(this, "Not Received Intent data "+source);
        }

        finish();
    }
}
