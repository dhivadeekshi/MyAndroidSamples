package com.dhivakar.mysamples.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Switch;

import com.dhivakar.mysamples.BaseAppCompatActivity;
import com.dhivakar.mysamples.R;


public class WebViewActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent = getIntent();
        if(intent != null)
        {
            Bundle bundle = intent.getExtras();
            if(bundle != null)
            {
                String htmlPage = bundle.getString("htmlPage");
                if(htmlPage != null && !htmlPage.isEmpty()) {
                    WebView webView = (WebView) findViewById(R.id.webviewActivity);
                    webView.loadData(htmlPage, "text/html", "UTF-8");
                }
            }
        }
    }

    public void onClick(View view) {
        super.onClick(view);
        switch(view.getId()) {
            case R.id.closeButton: finish(); break;
        }
    }
}
