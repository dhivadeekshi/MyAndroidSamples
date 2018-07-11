package com.dhivakar.mysamples.download;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
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
import java.util.HashMap;

import static com.dhivakar.mysamples.download.NativeDownloadManager.NOTIFICATION_TYPE_COUNT;
import static com.dhivakar.mysamples.download.NativeDownloadManager.NOTIFICATION_TYPE_SIZE;

public class DownloaderActivity extends BaseAppCompatActivity {

    public static Activity instance = null;
    private int downloadingCount = 0;
    private int downloadStartedCount = 0;
    private int downloadCompletedCount = 0;
    private int downloadFailedCount = 0;
    private boolean isApplicationPaused = false;
    private boolean showCumulativeNotification = false;

    private ArrayList<DownloaderInfo> downloaderInfos = new ArrayList<>();
    private String serverurl = "";
    private AssetBundlePlatform selectedPlatform = AssetBundlePlatform.Android;
    private AssetBundleVariant selectedVariant = AssetBundleVariant.High;
    private ArrayList<DownloaderFailedInfo> downloaderFailedInfos = new ArrayList<>();

    NativeDownloadManager downloadManager = null;
    private boolean downloadCanceled = false;
    private int downloadNotificationType = NOTIFICATION_TYPE_COUNT;

    private enum AssetBundleVariant{
        High,
        Low
    }

    private enum AssetBundlePlatform{
        Android,
        Ios
    }

    private class DownloaderInfo {
        public String fileUrl;
        public String fileName;
        public String destination;
        public long fileReference = -1;

        public boolean downloadComplete;
        public boolean downloadSuccess;
        public String downloadFailedReason;

        private long fileSize = 0;
        public long prevFileSize = 0;
        public FileDownloader downloader = null;

        public DownloaderInfo(String fileUrl, String fileName, String destination) {
            this.fileUrl = fileUrl;
            this.fileName = fileName;
            this.destination = destination;
            this.downloadComplete = false;
            this.downloadSuccess = false;
            this.downloadFailedReason = "";
        }
    }

    private class DownloaderFailedInfo{
        public String failedReason;
        public int failedCount;

        public DownloaderFailedInfo(String failedReason) {
            this.failedCount = 1;
            this.failedReason = failedReason;
        }
    }

    private void ResetDownloadedInfo()
    {
        downloaderFailedInfos.clear();
        UpdateUI();
    }

    private void SetDownloadFailedInfo(String reason)
    {
        boolean foundReason = false;
        for(DownloaderFailedInfo failed : downloaderFailedInfos)
        {
            if(failed.failedReason.equals(reason)){
                failed.failedCount++;
                foundReason = true;
                break;
            }
        }
        if(!foundReason) {
            downloaderFailedInfos.add(new DownloaderFailedInfo(reason));
        }
        UpdateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);

        instance = this;

        UpdateActivityHeader(getString(R.string.downloader_samples));
        SetDefaultUI();
        PopulateDownloaderInfo();
        UpdateUI();
    }

    private String GetAssetBundleFolder() {
        String AssetBundleCL = getString(R.string.AssetBundle);
        String AssetBundleVersion = getString(R.string.AssetBundleVersion);
        String AssetBundleHash = getString(R.string.AssetHash);
        return AssetBundleCL + "_" + AssetBundleVersion + AssetBundleHash;
    }

    private String GetBaseServerUrl() {
        String serverBaseUrl = getString(R.string.AssetServerUrl);
        return serverBaseUrl.replace("AssetBundleFolder", GetAssetBundleFolder());
    }

    private String GetAssetBundlePlatform(AssetBundlePlatform platform){
        String AssetBundlePlatform = "";
        switch (platform)
        {
            case Ios: AssetBundlePlatform = getString(R.string.AssetBundlePlatform_Ios); break;
            case Android: AssetBundlePlatform = getString(R.string.AssetBundlePlatform_Android); break;
        }
        return AssetBundlePlatform;
    }

    private String GetAssetBundleVariant(AssetBundleVariant variant) {
        String AssetBundleVariant = "";
        switch (variant) {
            case Low: AssetBundleVariant = getString(R.string.AssetBundleVariant_Low); break;
            case High: AssetBundleVariant = getString(R.string.AssetBundleVariant_High); break;
        }
        return AssetBundleVariant;
    }


    private void FetchServerUrl(AssetBundleVariant variant, AssetBundlePlatform platfrom) {
        String serverPath = getString(R.string.AssetBundleServerPath);
        serverurl = GetBaseServerUrl() + serverPath;
        serverurl = serverurl.replace("AssetBundleVariant", GetAssetBundleVariant(variant));
        serverurl = serverurl.replace("AssetBundlePlatform", GetAssetBundlePlatform(platfrom));

        LogUtils.d(this, "serverurl = " + serverurl);
    }

    private String GetDownloadListFor(String listName, AssetBundlePlatform platform) {
        //FULL URL  : https://ubistatic-a.akamaihd.net/0081/main/565198_0_cc02e8607ff87d8605e7f764cdedf003/FirstDownload_Android.json
        //BASE URL  : https://ubistatic-a.akamaihd.net/0081/main/565198_0_cc02e8607ff87d8605e7f764cdedf003/
        //FILE      : FirstDownload
        //PLATFORM  : Android
        //EXTENSION :.json
        String downloadUrl = GetBaseServerUrl() + listName + "_" + GetAssetBundlePlatform(platform) + ".json";
        LogUtils.d(this, listName + "Url = " + downloadUrl);
        return downloadUrl;
    }

    private void DownloadListFor(String fileName) {

    }

    private void PopulateDownloaderInfo() {
        FetchServerUrl(selectedVariant, selectedPlatform);
        downloaderInfos = new ArrayList<>();
        // Populate as per the toggle selected
        switch (downloadFilesIndex) {
            case 1: // firstDownload
                PopulateFirstDownload(serverurl);
                break;
            case 2: // secondDownload
                PopulateSecondDownload(serverurl);
                break;
            case 3: // First + Second
                PopulateFirstDownload(serverurl);
                PopulateSecondDownload(serverurl);
            default:
                PopulateDownloaderInfo(serverurl, R.raw.downloadlist);
                break;
        }
    }

    private void PopulateFirstDownload(String serverurl) {
        PopulateDownloaderInfo(serverurl, R.raw.firstdownload);
    }

    private void PopulateSecondDownload(String serverurl) {
        PopulateDownloaderInfo(serverurl, R.raw.seconddownload);
    }

    private void PopulateDownloaderInfo(String serverurl, int fileId) {
        ArrayList<String> filesDownloadList = LoadFileToDownload(fileId);
        LogUtils.d(this, "Loaded Files to download from raw resource : " + (filesDownloadList != null ? filesDownloadList.size() : 0));
        if (filesDownloadList != null && !serverurl.isEmpty()) {
            for (String fileName :
                    filesDownloadList) {
                downloaderInfos.add(new DownloaderInfo(serverurl + fileName, fileName, getString(R.string.AssetDefaultDestination)));
            }
        }
    }

    private String[] LoadFileToDownload(String fileName) {
        String resourcePath = getPackageResourcePath();
        File file = new File(resourcePath, fileName);
        LogUtils.d(this, "LoadFileToDownload resourcePath = " + file + " isExists?" + file.exists());
        return null;
    }

    private ArrayList<String> LoadFileToDownload(int fileId) {
        InputStream inputStream = getResources().openRawResource(fileId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        ArrayList<String> downloadList = new ArrayList<>();

        try {
            while ((line = buffreader.readLine()) != null) {
                downloadList.add(line);
            }
        } catch (IOException e) {
            return null;
        }
        return downloadList;
    }

    private ArrayList<String> LoadJsonFileToDownload(String filePath) {
        // TODO load json file and parse info
        InputStream inputStream = getResources().openRawResource(-1);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        ArrayList<String> downloadList = new ArrayList<>();

        try {
            while ((line = buffreader.readLine()) != null) {
                downloadList.add(line);
            }
        } catch (IOException e) {
            return null;
        }
        return downloadList;
    }

    private void UpdateDownloaderInfoUI() {
        String newLine = "\n";
        HashMap<String, String> downloaderInfo = new HashMap<>();
        downloaderInfo.put("TotalFiles", downloaderInfos.size() + "");
        downloaderInfo.put("Downloading", downloadingCount + "");
        downloaderInfo.put("DownloadStarted", "" + downloadStartedCount);
        downloaderInfo.put("Completed", downloadCompletedCount + "");
        downloaderInfo.put("Failed", downloadFailedCount + "");
        for(DownloaderFailedInfo failedInfo: downloaderFailedInfos)
            downloaderInfo.put(failedInfo.failedReason, failedInfo.failedCount + "");

        StringBuilder downloaderLabel = new StringBuilder();
        StringBuilder downloaderCount = new StringBuilder();
        for (String key :
                downloaderInfo.keySet()) {
            downloaderLabel.append(key).append(newLine);
            downloaderCount.append(downloaderInfo.get(key)).append(newLine);
        }

        TextView downloadLabels = (TextView) findViewById(R.id.textDownloadLabel);
        TextView downloadCounter = (TextView) findViewById(R.id.textDownloadCount);
        downloadLabels.setText(downloaderLabel);
        downloadCounter.setText(downloaderCount);
    }

    private void UpdateDownloadProgressUI() {
        ProgressBar downloadProgress = (ProgressBar) findViewById(R.id.progressDownloading);
        double progress = ((float) (downloadCompletedCount + downloadFailedCount) / (float) (downloadCompletedCount + downloadFailedCount + downloadingCount)) * 100f;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            downloadProgress.setProgress((int) Math.floor(progress), true);
        else
            downloadProgress.setProgress((int) Math.floor(progress));
    }

    private void UpdateDownloadFailedUI() {
        StringBuilder downloadFailedFilesList = new StringBuilder();
        if (downloadingCount == 0 && downloadFailedCount > 0) {
            for (DownloaderInfo download :
                    downloaderInfos) {
                if (!download.downloadSuccess)
                    downloadFailedFilesList.append(download.fileName).append(" reason:").append(download.downloadFailedReason).append("\n");
            }

            TextView downloadFailedList = (TextView) findViewById(R.id.textDownloadFailedList);
            downloadFailedList.setText(downloadFailedFilesList.toString());
        }
    }

    private void UpdateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                UpdateDownloaderInfoUI();
                UpdateDownloadProgressUI();
                UpdateDownloadFailedUI();
            }
        });

    }

    private void SetDefaultUI() {
        RadioButton defaultDownload = (RadioButton) findViewById(R.id.radioButtonDefault);
        defaultDownload.setChecked(true);

        RadioButton defaultNotificationType = (RadioButton) findViewById(R.id.radioButtonTotalFilesCount);
        defaultNotificationType.setChecked(true);

        TextView title = (TextView) findViewById(R.id.textNotificationTitle);
        TextView description = (TextView) findViewById(R.id.textNotificationDescription);

        title.setText("Dhivakar");
        description.setText("Downloading Content...");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.buttonStartDownload:
                StartDownload();
                break;
            case R.id.buttonCancelDownload:
                CancelDownload();
                break;
            case R.id.buttonClearData:
                ClearData();
                break;

            case R.id.radioButtonDefault:
                SetDownloadIndex(v, 0);
                break;
            case R.id.radioButtonFirstDownload:
                SetDownloadIndex(v, 1);
                break;
            case R.id.radioButtonSecondDownload:
                SetDownloadIndex(v, 2);
                break;
            case R.id.radioButtonDownloadAll:
                SetDownloadIndex(v, 3);
                break;

            case R.id.radioButtonTotalFilesCount:
                downloadNotificationType = NOTIFICATION_TYPE_COUNT;
                break;
            case R.id.radioButtonTotalFilesSize:
                downloadNotificationType = NOTIFICATION_TYPE_SIZE;
                break;

            case R.id.checkBoxShowCumulativeNotification:
                if(downloadingCount == 0 || downloadCanceled){
                    downloadManager = null;
                    showCumulativeNotification = ((CheckBox)v).isChecked();
                }
                else
                    ((CheckBox)v).setChecked(showCumulativeNotification);
                break;
        }
    }

    private int downloadFilesIndex = 0;

    private void SetDownloadIndex(View radioView, int index) {
        if (((RadioButton) radioView).isChecked())
            downloadFilesIndex = index;
        PopulateDownloaderInfo();
        UpdateUI();
        LogUtils.d(this, "SetDownloadIndex : " + ((RadioButton) radioView).getText());
    }

    // Notifications
    // -----------------------

    private Thread ThreadStartDownloading = null;
    private void StartDownload() {
        if (downloadingCount > 0)
            return;
        if (downloadManager == null)
            downloadManager = new NativeDownloadManager(showCumulativeNotification, downloadNotificationType);

        CancelDownload();
        PopulateDownloaderInfo();
        ResetDownloadedInfo();

        downloadCanceled = false;
        downloadingCount = 0;
        downloadStartedCount = 0;
        downloadCompletedCount = 0;
        downloadFailedCount = 0;
        UpdateUI();

        // Notification
        if(downloadNotificationType == NOTIFICATION_TYPE_COUNT)
            downloadManager.SetNotificationMaxCount(downloaderInfos.size());
        else if(downloadNotificationType == NOTIFICATION_TYPE_SIZE){
            double firstDownloadSizeInMB = 72.25d;
            double secoondDownloadSizeInMb = 72.25d;
            long mbToBytes = 1024 * 1024;
            double totalDownloadSize = 1;
            switch (downloadFilesIndex){
                case 1: totalDownloadSize = firstDownloadSizeInMB; break;
                case 2: totalDownloadSize = secoondDownloadSizeInMb; break;
                case 3: totalDownloadSize = firstDownloadSizeInMB + secoondDownloadSizeInMb; break;
            }
            downloadManager.SetNotificationMaxSize((long) totalDownloadSize * mbToBytes);
        }

        if (downloaderInfos != null) {
            ThreadStartDownloading = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (DownloaderInfo download :
                            downloaderInfos) {
                        /*try {
                            while(isApplicationPaused)
                                Thread.sleep(1000);
                        }catch (InterruptedException e){}*/

                        if(ThreadStartDownloading == null)
                            break;

                        download.downloadSuccess = false;
                        download.downloadComplete = false;
                        download.downloadFailedReason = "";
                        download.downloader = downloadManager.CreateDownload(download.fileUrl, download.fileName, download.destination);
                        download.fileReference = download.downloader.m_fileReference;
                        downloadingCount++;
                        downloadStartedCount++;
                        /*while (download.downloader.GetFileSize() == -1)
                        {
                            try{
                                Thread.sleep(1);
                            }catch (InterruptedException e){}
                        }
                        download.fileSize = download.downloader.GetFileSize();*/
                        LogUtils.d("DownloadSize","downloadSize = "+download.downloader.GetDownloadedSize()+" fileSize:"+download.fileSize);
                        UpdateUI();
                    }
                    LogUtils.d(TAG, "All DownloadsAdded");

                }
            });
            ThreadStartDownloading.start();
        }
    }

    private void CancelDownload() {
        downloadCanceled = true;
        if(ThreadStartDownloading != null && ThreadStartDownloading.isAlive() && !ThreadStartDownloading.isInterrupted())
            ThreadStartDownloading.interrupt();
        ThreadStartDownloading = null;
        downloadingCount = 0;
        UpdateUI();
        if (downloadManager != null)
            downloadManager.CancelAllDownloads();
    }

    private void ClearData() {
        File destination = new File(getString(R.string.AssetDefaultDestination));
        if (destination.exists() && destination.isDirectory())
            LogUtils.d(this, "ClearData dest:" + destination + " succeed?" + DeleteDir(destination.toString()));
        File docPath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (docPath != null && docPath.exists())
            LogUtils.d(this, "ClearDocuments doc:" + docPath + " succeed?" + DeleteDir(docPath.toString()));
    }

    private boolean DeleteDir(String dirPath) {
        File file = new File(dirPath);
        if(!file.exists()) return false;
        for (File child : file.listFiles()) {
            if (child.isDirectory())
                DeleteDir(child.toString());
            else
                child.delete();
        }
        return file.delete();
    }

    public void onDownloadComplete(String message) {
        if(downloadCanceled) return;
        try {
            String[] messagesReceived = message.split(NativeDownloadManager.StringSeperator);
            LogUtils.d(this, "OnDownloadComplete : " + message);
            boolean status = messagesReceived.length > 0 && messagesReceived[0].equals("success");
            long fileReference = -1;
            if (messagesReceived.length > 1) fileReference = Long.parseLong(messagesReceived[1]);
            String error = messagesReceived.length > 3 ? messagesReceived[3] : messagesReceived.length > 2 ? messagesReceived[2] : "";
            if (downloaderInfos != null && fileReference != -1) {
                for (DownloaderInfo download :
                        downloaderInfos) {
                    if (download.fileReference == fileReference) {
                        download.downloadComplete = true;
                        download.downloadSuccess = status;
                        download.downloadFailedReason = error;
                        downloadManager.CancelDownload(download.downloader);
                        if (status) downloadCompletedCount++;
                        else {
                            downloadFailedCount++;
                            SetDownloadFailedInfo("FailedReason:"+error);
                        }

                        downloadingCount--;
                        UpdateUI();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.e(this, "onDownloadComplete exception:" + e.getMessage());
        }
        if(downloadFailedCount + downloadCompletedCount == downloadStartedCount)
            downloadManager.DisplayDownloadCompletedNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isApplicationPaused = true;
        //if(downloadManager != null) downloadManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isApplicationPaused = false;
        //if(downloadManager != null) downloadManager.onResume();
    }
}
