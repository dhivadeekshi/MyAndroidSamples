package com.dhivakar.mysamples.download;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dhivakar.mysamples.BaseAppCompatActivity;
import com.dhivakar.mysamples.R;
import com.dhivakar.mysamples.utils.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DownloaderActivity extends BaseAppCompatActivity {

    public static Activity instance = null;
    private int downloadingCount= 0;
    private int downloadCompletedCount= 0;
    private int downloadFailedCount = 0;

    // Download Progress
    private int downloadProgressMax = 0;
    private int downloadProgress = 0;

    private class DownloaderInfo {
        public String fileUrl;
        public String fileName;
        public String destination;
        public long fileReference = -1;

        private String serverBaseURL = "";
        private String defaultDestination = "/storage/emulated/0/Android/data/com.dhivakar.mysamples/files/Assets/";
        public boolean downloadComplete;
        public boolean downloadSuccess;

        private int fileSize = 0;

        public DownloaderInfo(String fileUrl, String fileName, String destination)
        {
            this.fileUrl = !fileUrl.isEmpty() ? fileUrl : serverBaseURL + fileName;
            this.fileName = fileName;
            this.destination = destination.isEmpty() ? defaultDestination : destination;
            this.downloadComplete = false;
            this.downloadSuccess = false;
        }

        public DownloaderInfo(String fileName)
        {
            this.fileName = fileName;
            this.fileUrl = serverBaseURL + fileName;
            this.destination = defaultDestination;
            this.downloadComplete = false;
            this.downloadSuccess = false;
        }
    }

    private ArrayList<DownloaderInfo> downloaderInfos = new ArrayList<>();
    private String serverurl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);

        instance = this;

        SetDefaultUI();
        FetchServerUrl();
        PopulateDownloaderInfo();
        UpdateUI();
    }

    private void FetchServerUrl()
    {
        ArrayList<String> server = LoadFileToDownload(R.raw.serverurl);
        serverurl = server != null && !server.get(0).isEmpty() ? server.get(0) : "";
    }

    private void PopulateDownloaderInfo()
    {
        if(downloaderInfos == null) downloaderInfos = new ArrayList<>();

        // FirstDownload.txt
        // FirstDownload.json
        // FirstDownload_Android.json
        // SecondDownload.txt
        // SecondDownload.json
        // SecondDownload_Android.json

        downloaderInfos = new ArrayList<>();
        // Populate as per the toggle selected
        switch (downloadFilesIndex) {
            case 1: // firstDownload
                PopulateFirstDownload(serverurl);
                break;
            case 2: // secondDownload
                PopulateSecondDownload(serverurl);
                break;
            default:
                PopulateDownloaderInfo(serverurl, R.raw.downloadlist);
                break;
        }
    }

    private void PopulateFirstDownload(String serverurl)
    {
        PopulateDownloaderInfo(serverurl, R.raw.firstdownload);
    }

    private void PopulateSecondDownload(String serverurl)
    {
        PopulateDownloaderInfo(serverurl, R.raw.seconddownload);
    }

    private void PopulateDownloaderInfo(String serverurl, int fileId)
    {
        ArrayList<String> filesDownloadList = LoadFileToDownload(fileId);
        LogUtils.d(this, "Loaded Files to download from raw resource : "+(filesDownloadList != null ? filesDownloadList.size() : 0));
        if(filesDownloadList != null && !serverurl.isEmpty())
        {
            for(String fileName :
                    filesDownloadList) {
                //LogUtils.d(this, "filesToDownload : "+fileName);
                downloaderInfos.add(new DownloaderInfo(serverurl+fileName, fileName, ""));
            }
        }
    }

    private String[] LoadFileToDownload(String fileName)
    {
        String resourcePath = getPackageResourcePath();
        File file = new File(resourcePath, fileName);
        LogUtils.d(this,"LoadFileToDownload resourcePath = "+file+" isExists?"+file.exists());
        return null;
    }

    private ArrayList<String> LoadFileToDownload(int fileId)
    {
        InputStream inputStream = getResources().openRawResource(fileId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        ArrayList<String> downloadList = new ArrayList<>();

        try {
            while (( line = buffreader.readLine()) != null) {
                downloadList.add(line);
            }
        } catch (IOException e) {
            return null;
        }
        return downloadList;
    }

    private void UpdateUI()
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

    private void SetDefaultUI()
    {
        RadioButton defaultDownload = (RadioButton) findViewById(R.id.radioButtonDefault);
        defaultDownload.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch(v.getId())
        {
            case R.id.buttonStartDownload: StartDownload(); break;
            case R.id.buttonCancelDownload: CancelDownload(); break;

            case R.id.radioButtonDefault: SetDownloadIndex(v, 0); break;
            case R.id.radioButtonFirstDownload: SetDownloadIndex(v, 1); break;
            case R.id.radioButtonSecondDownload: SetDownloadIndex(v, 2); break;
        }
    }

    private int downloadFilesIndex = 0;
    private void SetDownloadIndex(View radioView, int index) {
        if(((RadioButton)radioView).isChecked())
            downloadFilesIndex = index;
        LogUtils.d(this, "SetDownloadIndex : "+((RadioButton)radioView).getText());
    }

    NativeDownloadManager downloadManager = null;
    private void StartDownload()
    {
        if(downloadingCount > 0)
            return;
        if(downloadManager == null)
            downloadManager = new NativeDownloadManager();

        PopulateDownloaderInfo();

        downloadingCount = 0;
        downloadCompletedCount = 0;
        downloadFailedCount = 0;
        UpdateUI();

        if(downloaderInfos != null)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (DownloaderInfo download :
                            downloaderInfos) {
                        download.downloadSuccess = false;
                        download.downloadComplete = false;
                        download.fileReference = downloadManager.StartDownload(download.fileUrl, download.fileName, download.destination);
                        //LogUtils.d("DownloadSize","downloadSize = "+downloadManager.GetDownloadedSize(download.fileReference)+" fileSize:"+downloadManager.GetFileSize(download.fileReference) );
                        downloadingCount++;
                        UpdateUI();
                    }
                }
            }).start();
        }
    }

    private void CancelDownload()
    {
        downloadingCount = 0;
        UpdateUI();
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
                        download.downloadComplete = true;
                        download.downloadSuccess = status;
                        downloadManager.CancelDownload(fileReference);
                        if(status) downloadCompletedCount++;
                        else downloadFailedCount++;
                        downloadingCount--;
                        UpdateUI();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.e(this, "onDownloadComplete exception:" + e.getMessage());
        }
    }
}
