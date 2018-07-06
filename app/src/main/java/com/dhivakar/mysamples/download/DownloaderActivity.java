package com.dhivakar.mysamples.download;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dhivakar.mysamples.BaseAppCompatActivity;
import com.dhivakar.mysamples.R;
import com.dhivakar.mysamples.utils.LogUtils;
import com.google.android.gms.auth.api.phone.SmsRetriever;

import java.io.File;
import java.util.ArrayList;

public class DownloaderActivity extends BaseAppCompatActivity {

    public static Activity instance = null;
    private int downloadingCount= 0;
    private int downloadCompletedCount= 0;
    private int downloadFailedCount = 0;

    private class DownloaderInfo {
        public String fileUrl;
        public String fileName;
        public String destination;
        public long fileReference = -1;

        private String serverBaseURL = "";
        private String defaultDestination = "/storage/emulated/0/Android/data/com.dhivakar.mysamples/files/Assets/";
        public boolean downaloadComplete;
        public boolean downloadSuccess;

        public DownloaderInfo(String fileUrl, String fileName, String destination)
        {
            this.fileUrl = !fileUrl.isEmpty() ? fileUrl : serverBaseURL + fileName;
            this.fileName = fileName;
            this.destination = destination;
            this.downaloadComplete = false;
            this.downloadSuccess = false;
        }

        public DownloaderInfo(String fileName)
        {
            this.fileName = fileName;
            this.fileUrl = serverBaseURL + fileName;
            this.destination = defaultDestination;
            this.downaloadComplete = false;
            this.downloadSuccess = false;
        }
    }

    private ArrayList<DownloaderInfo> downloaderInfos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);

        instance = this;
        PopuplateDownloaderInfo();
        UpdateDownloadCountUI();
    }

    private void PopuplateDownloaderInfo()
    {
        if(downloaderInfos == null) downloaderInfos = new ArrayList<>();

        // FirstDownload.txt
        // FirstDownload.json
        // FirstDownload_Android.json
        // SecondDownload.txt
        // SecondDownload.json
        // SecondDownload_Android.json
        String[] filesToDownload = LoadFileToDownload("res/downloadinfo/FirstDownload.txt");
        LogUtils.d(this, "Loaded Files to download : "+(filesToDownload != null ? filesToDownload.length : 0));

        if(downloaderFiles != null)
            for (String fileName :
                    downloaderFiles) {
                downloaderInfos.add(new DownloaderInfo(fileName));
            }
    }

    private String[] LoadFileToDownload(String fileName)
    {
        String resourcePath = getPackageResourcePath()+"/"+fileName;
        File file = new File(resourcePath, fileName);
        LogUtils.d(this,"LoadFileToDownload resourcePath = "+resourcePath+" isExists?"+file.exists());
        return null;
    }

    private void UpdateDownloadCountUI()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView downloadCounter = (TextView) findViewById(R.id.textDownloadCount);
                downloadCounter.setText("Downloading:" + downloadingCount + " completed:" + downloadCompletedCount + " failed:" + downloadFailedCount);

                ProgressBar downloadProgress = (ProgressBar) findViewById(R.id.progressDownloading);
                double progress = ((float) (downloadCompletedCount + downloadFailedCount) / (float) (downloadCompletedCount + downloadFailedCount + downloadingCount)) * 100f;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    downloadProgress.setProgress((int) Math.floor(progress), true);
                else
                    downloadProgress.setProgress((int) Math.floor(progress));

                StringBuilder downloadFailedFilesList = new StringBuilder();
                if(downloadingCount == 0 && downloadFailedCount > 0) {
                    for (DownloaderInfo download :
                            downloaderInfos) {
                        if (!download.downloadSuccess)
                            downloadFailedFilesList.append(download.fileName).append("\n");
                    }

                    TextView downloadFailedList = (TextView) findViewById(R.id.textDownloadFailedList);
                    downloadFailedList.setText(downloadFailedFilesList.toString());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch(v.getId())
        {
            case R.id.buttonStartDownload: StartDownload(); break;
            case R.id.buttonCancelDownload: CancelDownload(); break;
        }
    }

    NativeDownloadManager downloadManager = null;
    private void StartDownload()
    {
        if(downloadingCount > 0)
            return;
        if(downloadManager == null)
            downloadManager = new NativeDownloadManager();
        downloadingCount = 0;
        downloadCompletedCount = 0;
        downloadFailedCount = 0;
        UpdateDownloadCountUI();

        if(downloaderInfos != null)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (DownloaderInfo download :
                            downloaderInfos) {
                        download.downloadSuccess = false;
                        download.downaloadComplete = false;
                        download.fileReference = downloadManager.StartDownload(download.fileUrl, download.fileName, download.destination);
                        downloadingCount++;
                        UpdateDownloadCountUI();
                    }
                }
            }).start();
        }
    }

    private void CancelDownload()
    {
        downloadingCount = 0;
        UpdateDownloadCountUI();
        if(downloadManager != null && downloaderInfos != null)
        {
            for (DownloaderInfo download :
                    downloaderInfos) {
                if (download.fileReference != -1)
                    downloadManager.CancelDownload(download.fileReference);
            }
        }
    }

    public void onDownloadComplete(String message) {
        try {
            String[] messagesReceived = message.split("@");
            LogUtils.d(this, "OnDownloadComplete : " + message+" messages:"+messagesReceived.length);
            boolean status = messagesReceived.length > 0 && messagesReceived[0].equals("success");
            long fileReference = -1;
            if (messagesReceived.length > 1) fileReference = Long.parseLong(messagesReceived[1]);
            String error = messagesReceived.length > 3 ? messagesReceived[2] : messagesReceived.length > 2 ? messagesReceived[2] : "";
            if (downloaderInfos != null && fileReference != -1) {
                for (DownloaderInfo download :
                        downloaderInfos) {
                    if (download.fileReference == fileReference) {
                        download.downaloadComplete = true;
                        download.downloadSuccess = status;
                        downloadManager.CancelDownload(fileReference);
                        if(status) downloadCompletedCount++;
                        else downloadFailedCount++;
                        downloadingCount--;
                        UpdateDownloadCountUI();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.e(this, "onDownloadComplete exception:" + e.getMessage());
        }
    }



    private String[] downloaderFiles = {
    };
}
