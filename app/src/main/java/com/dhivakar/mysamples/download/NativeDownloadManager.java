package com.dhivakar.mysamples.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

// Project Based Imports
import com.dhivakar.mysamples.notifications.NotificationHelper;
import com.dhivakar.mysamples.utils.LogUtils;
// ------------------------

public class NativeDownloadManager {

    private static final String TAG = "D_MANAGER";
    private ArrayList<FileDownloader> downloaders = new ArrayList<>();
    private DownloadManager m_downloadManager;
    private boolean isApplicationPaused = false;
    private boolean m_showCumulativeNotification = false;
    private NotificationType m_cumulativeNotificationType = NotificationType.DownloadCount;
    public static final int NOTIFICATION_TYPE_COUNT = 1;
    public static final int NOTIFICATION_TYPE_SIZE = 2;

    private enum NotificationType{
        DownloadCount,
        DownloadSize
    }

    private NotificationType getNotificationTypeFromInt(int notificationType) {
        switch (notificationType) {
            default:
            case NOTIFICATION_TYPE_COUNT:
                return NotificationType.DownloadCount;
            case NOTIFICATION_TYPE_SIZE:
                return NotificationType.DownloadSize;
        }
    }

    // Update based on your project
    public static final String StringSeperator = "@";
    private static final String DOWNLOAD_REQUEST_TITLE = "Dhivakar";
    private static final String DOWNLOAD_REQUEST_DESCRIPTION = "Downloading Game Content...";
    private static final String DOWNLOAD_REQUEST_DESCRIPTION_COMPLETED = "Download Completed";
    private static final String DOWNLOAD_REQUEST_CHANNELID = NotificationHelper.CHANNEL_ID_DEFAULT;
    private static final int DOWNLOAD_REQUEST_NOTIFICATION_ICON = NotificationHelper.SMALL_ICON_DEFAULT;
    private static final int DOWNLOAD_REQUEST_NOTIFICATIONID = 999;

    public Context getAppContext() {
        return null;//DownloaderActivity.instance.getApplicationContext();
    }

    private void OnNotificationClicked(Context context)
    {
        /*Intent launchIntent = new Intent(context, DownloaderActivity.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(launchIntent);*/
    }

    public static void LogError(String message)
    {
        LogUtils.e(TAG, message);
    }

    public static void LogDebug(String message)
    {
        LogUtils.d(TAG, message);
    }

    private void onDownloadComplete(final long fileReference) {
        final FileDownloader downloader = GetFileDownloaderFor(fileReference);
        if (downloader != null) downloader.OnDownloadCompleted(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isApplicationPaused) {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                }
                String fileName = downloader != null ? StringSeperator + downloader.m_fileName : "";
                String message = "success" + StringSeperator + fileReference + fileName;
                LogDebug("onDownloadComplete  fileReference:" + fileReference + "- success" + StringSeperator + fileReference + fileName);
                //((DownloaderActivity) DownloaderActivity.instance).onDownloadComplete(message);
            }
        }).start();
    }

    private void onDownloadFailed(final long fileReference,final int status, final int reason) {
        final FileDownloader downloader = GetFileDownloaderFor(fileReference);
        if (downloader != null) downloader.OnDownloadCompleted(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isApplicationPaused)
                        Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                String fileName = downloader != null ? StringSeperator + downloader.m_fileName : "";
                String message = "failed" + StringSeperator + fileReference + StringSeperator + reason + StringSeperator + GetReasonString(reason) + fileName + StringSeperator + GetStatusString(status);
                LogDebug("onDownloadFailed fileReference:" + fileReference + " reason:" + GetReasonString(reason) + "- failed" + StringSeperator + fileReference + StringSeperator + reason + StringSeperator + GetReasonString(reason) + fileName);
                //((DownloaderActivity) DownloaderActivity.instance).onDownloadComplete(message);
            }
        }).start();
    }
    // ----------------------------

    public void onPause()
    {
        isApplicationPaused = true;
        /*m_isAppInBackground = true;
        if(m_canShowDownloadPogress)
            displayProgressNotification();*/
    }

    public void onResume()
    {
        isApplicationPaused = false;
        /*if(m_isAppInBackground == true)
        {
            m_isAppInBackground = false;
            cancelProgressBar(false);
        }*/
    }

    public File getDestinationFolder() {
        return getAppContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
    }

    public String getDestinationPath() {
        return getDestinationFolder() == null ? "" : getDestinationFolder().toString();
    }

    private void FetchDownloadManger()
    {
        if(m_downloadManager == null)
            //Create request for android download manager
            m_downloadManager = (DownloadManager)getAppContext().getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public NativeDownloadManager() {
        //registerBroadcastReceivers();
        Init(false, NOTIFICATION_TYPE_COUNT);
    }

    public NativeDownloadManager(boolean showCumulativeNotification) {
        //registerBroadcastReceivers();
        Init(showCumulativeNotification, NOTIFICATION_TYPE_COUNT);
    }

    public NativeDownloadManager(boolean showCumulativeNotification, int notificationType) {
        //registerBroadcastReceivers();
        Init(showCumulativeNotification, notificationType);
    }

    public void SetNotificationMaxCount(int totalFiles){
        this.totalFiles = totalFiles;
        downloadedFiles = 0;
    }

    public void SetNotificationMaxSize(long totalDownloadSize){
        this.totalDownloadSize = totalDownloadSize;
        downloadedSize = 0;
    }

    public void Init(boolean showCumulativeNotification, int notificationType){
        m_showCumulativeNotification = showCumulativeNotification;
        m_cumulativeNotificationType = getNotificationTypeFromInt(notificationType);
    }

    public FileDownloader CreateDownload(String fileUrl, String fileName, String destination, boolean showNotification) {

        FetchDownloadManger();

        FileDownloader downloader = new FileDownloader(m_downloadManager, getDestinationFolder());
        downloader.StartDownload(fileUrl, fileName, destination, getAppContext(), showNotification, "Downloading "+fileName, DOWNLOAD_REQUEST_DESCRIPTION);
        downloaders.add(downloader);

        if(downloaders.size() == 1)
            registerBroadcastReceivers();

        /*if(m_isAppInBackground && m_canShowDownloadPogress)
            displayProgressNotification();*/

        ShowDownloadingProgressNotification();
        LogDebug("DownloadStarted fileUrl:" + fileUrl+" fileName:"+fileName+" destination:"+destination+" fileReference:"+downloader.m_fileReference);
        return downloader;
    }

    public FileDownloader CreateDownload(String fileUrl, String fileName, String destination) {
        return CreateDownload(fileUrl, fileName, destination, false);
    }

    public FileDownloader CreateDownload(String fileUrl, String fileName, String destination, int totalFiles, int downloadedFiles) {
        this.totalFiles = totalFiles;
        this.downloadedFiles = downloadedFiles;
        return CreateDownload(fileUrl, fileName, destination);
    }

    public long StartDownload(String fileUrl, String fileName, String destination, int totalFiles, int downloadedFiles) {
        return CreateDownload(fileUrl, fileName, destination, totalFiles, downloadedFiles).m_fileReference;
    }

    public long StartDownload(String fileUrl, String fileName, String destination) {
        return CreateDownload(fileUrl, fileName, destination).m_fileReference;
    }

    public void CancelAllDownloads()
    {
        for(FileDownloader downloader:downloaders){
            CancelDownload(downloader);
        }
        CancelDownloadingProgressNotification();
    }

    public void CancelDownload(FileDownloader downloader)
    {
        if (downloader != null) {
            LogDebug("CancelDownload fileReference:" + downloader.m_fileReference + " isFileExists?" + downloader.isDownloadedFileExists());
            downloader.CancelDownload();
            downloaders.remove(downloader);
        }

        if (downloaders.size() == 0)
            unregisterBroadcastReceivers();
    }

    public void CancelDownload(long fileReference) {
        if (downloaders.size() == 0) return;
        CancelDownload(GetFileDownloaderFor(fileReference));
    }

    public long GetDownloadedSize(long fileReference) {
        //LogDebug("GetDownloadedSize fileReference:" + fileReference);
        long downloadedSize = 0;
        FileDownloader downloader = GetFileDownloaderFor(fileReference);
        if (downloader != null) downloadedSize = downloader.GetDownloadedSize();
        return downloadedSize;
    }

    public long GetFileSize(long fileReference) {
        //LogDebug("GetFileSize fileReference:" + fileReference);
        long fileSize = 0;
        FileDownloader downloader = GetFileDownloaderFor(fileReference);
        if (downloader != null) fileSize = downloader.GetFileSize();
        return fileSize;
    }

    /*
     * Register download broadcast receiver
     */
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Fetch the fileReference from the intent received
            long fileReference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            //Get the status and reason of the download
            int[] statusAndReason = GetDownloadStatusAndReason(fileReference);
            int status = statusAndReason[0];
            int reason = statusAndReason[1];

            FileDownloader downloader = GetFileDownloaderFor(fileReference);
            LogDebug("Download Broadcast receiver onReceive fileReference:"+fileReference+" fileName:"+(downloader != null ? downloader.m_fileName : "Null")+" status:"+GetStatusString(status)+" reason:"+GetReasonString(reason));

            if(status == DownloadManager.STATUS_SUCCESSFUL)
            {
                if(downloader != null && downloader.MoveFileToDestination()) {
                    // Download complete
                    onDownloadComplete(fileReference);
                }else {
                    // Download failed since we can't copy to the destination
                    onDownloadFailed(fileReference, DownloadManager.STATUS_FAILED, DownloadManager.ERROR_FILE_ERROR);
                }
            }
            else// if(status == DownloadManager.STATUS_FAILED)
            {
                // Download failed
                onDownloadFailed(fileReference, status, reason);
            }

            // Succeeded or File not found in server
            if(status == DownloadManager.STATUS_SUCCESSFUL || reason == 404){
                // Update Notification count
                downloadedFiles++;
                if(downloader != null)
                    downloadedSize += downloader.GetDownloadedSize();
                ShowDownloadingProgressNotification();
            }
        }
    };

    /*
     *	Register Notification broadcast receiver
     */
    private BroadcastReceiver notificationClickedReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            OnNotificationClicked(context);
        }
    };

    private void registerBroadcastReceivers()
    {
        LogDebug("registerBroadcastReceivers");
        // Register Notification clicked receiver
        IntentFilter notificationFilter = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        getAppContext().registerReceiver(notificationClickedReceiver, notificationFilter);

        // Register Download complete receiver
        IntentFilter downloadFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getAppContext().registerReceiver(downloadReceiver, downloadFilter);
    }

    private void unregisterBroadcastReceivers()
    {
        try
        {
            getAppContext().unregisterReceiver(notificationClickedReceiver);
            getAppContext().unregisterReceiver(downloadReceiver);
        }
        catch (Exception e)
        {
            LogDebug("unregisterBroadcastReceiver Exception "+e.getMessage());
        }
    }

    private int[] GetDownloadStatusAndReason(long fileReference)
    {
        int status = DownloadManager.ERROR_UNKNOWN;
        int reason = 0;

        try
        {
            DownloadManager.Query downloadQuery = new DownloadManager.Query();

            //Set Query filter to our previously Enqueued download
            downloadQuery.setFilterById(fileReference);

            //Query the download manager about download that have been requested
            Cursor cursor = m_downloadManager.query(downloadQuery);

            if(cursor != null && cursor.moveToFirst())
            {
                //get column for download status
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                status = cursor.getInt(columnIndex);

                //get column for reason code if download failed or paused
                int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                reason = cursor.getInt(columnReason);

                cursor.close();
            }
            else
                LogDebug("checkDownloadStatus fileReference = " + fileReference + "cursor = null Download Canceled by User");
        }
        catch(Exception e)
        {
            LogDebug("checkDownloadStatus fileReference:"+fileReference+" Got Exception : " + e);
        }

        int[] statusAndReason = new int[2];
        statusAndReason[0] = status;
        statusAndReason[1] = reason;
        return statusAndReason;
    }

    private String GetStatusString(int status)
    {
        String statusText = "";

        switch(status) {
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                break;
            default:
                statusText += status;
                break;
        }
        return statusText;
    }

    private String GetReasonString(int reason)
    {
        String reasonText = "";

        switch(reason) {
            case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                reasonText = "PAUSED_QUEUED_FOR_WIFI";
                break;
            case DownloadManager.PAUSED_UNKNOWN:
                reasonText = "PAUSED_UNKNOWN";
                break;
            case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                reasonText = "PAUSED_WAITING_FOR_NETWORK";
                break;
            case DownloadManager.PAUSED_WAITING_TO_RETRY:
                reasonText = "PAUSED_WAITING_TO_RETRY";
                break;
            case DownloadManager.ERROR_CANNOT_RESUME:
                reasonText = "ERROR_CANNOT_RESUME";
                break;
            case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                reasonText = "ERROR_DEVICE_NOT_FOUND";
                break;
            case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                reasonText = "ERROR_FILE_ALREADY_EXISTS";
                break;
            case DownloadManager.ERROR_FILE_ERROR:
                reasonText = "ERROR_FILE_ERROR";
                break;
            case DownloadManager.ERROR_HTTP_DATA_ERROR:
                reasonText = "ERROR_HTTP_DATA_ERROR";
                break;
            case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                reasonText = "ERROR_INSUFFICIENT_SPACE";
                break;
            case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                reasonText = "ERROR_TOO_MANY_REDIRECTS";
                break;
            case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                break;
            case DownloadManager.ERROR_UNKNOWN:
                reasonText = "ERROR_UNKNOWN";
                break;
            default:
                reasonText += reason;
                break;
        }

        return reasonText;
    }

    public boolean isNativeDownloadCompleted(long fileReference) {
        FileDownloader downloader = GetFileDownloaderFor(fileReference);
        return downloader == null || downloader.isDownloadCompleted();
    }

    public boolean isNativeDownloadSucceded(long fileReference) {
        FileDownloader downloader = GetFileDownloaderFor(fileReference);
        return downloader == null || downloader.m_isDownloadSucceded;
    }

    private FileDownloader GetFileDownloaderFor(long fileReference) {
        for (FileDownloader downloader :
                downloaders) {
            if (downloader.m_fileReference == fileReference)
                return downloader;
        }
        return null;
    }

    // Notification -----------------
    private Thread notificationProgressTread = null;
    private int downloadedFiles = 0;
    private int totalFiles = 0;
    private long totalDownloadSize = 0;
    private long downloadedSize = 0;
    private void ShowDownloadingProgressNotification() {
        if (m_showCumulativeNotification) {

            if (m_cumulativeNotificationType == NotificationType.DownloadSize) {
                /*CancelDownloadingProgressNotification();

                notificationProgressTread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            // Need to get the downloaded size and max download size
                            while (downloadedSize < totalDownloadSize) {
                                int progress = Math.round(((float) downloadedFiles / (float) totalFiles) * 100f);
                                DisplayNotification(100, progress);
                                Thread.sleep(1000);
                            }
                            LogDebug("DisplayNotification ended");
                            DisplayDownloadCompletedNotification();
                        } catch (InterruptedException e) {
                            CancelDownloadingProgressNotification();
                            LogError("DisplayNotification ended with exception");
                        }
                    }
                });
                notificationProgressTread.start();*/
                // Option 2
                if(totalDownloadSize != 0 && downloadedSize < totalDownloadSize){
                    int progress = Math.round(((float) downloadedSize / (float) totalDownloadSize) * 100f);
                    DisplayNotification(100, progress);
                }
            }
            else {
                // Download Count Notification
                // For downloaded count we don't need any thread since the download completed can be detected from OnDownloadComplete
                if(totalFiles != 0 && downloadedFiles < totalFiles)
                    DisplayNotification(totalFiles, downloadedFiles);
            }
        }
    }

    private void CancelDownloadingProgressNotification() {
        if (notificationProgressTread != null && notificationProgressTread.isAlive() && !notificationProgressTread.isInterrupted())
            notificationProgressTread.interrupt();
        notificationProgressTread = null;

        if (m_showCumulativeNotification) {
            NotificationHelper.CancelNotification(getAppContext(), NotificationHelper.NOTIFICATION_ID_DEFAULT);
        }
        CancelDownloadNotification();
    }

    public void CancelDownloadNotification(){
        NotificationHelper.CancelNotification(getAppContext(), DOWNLOAD_REQUEST_NOTIFICATIONID);
    }

    private void DisplayNotification(int max, int progress)
    {
        String downloadDescription = DOWNLOAD_REQUEST_DESCRIPTION;
        long bytesToMB = 1024 * 1024;
        switch (m_cumulativeNotificationType) {
            case DownloadCount:
                downloadDescription += " " + progress + "/" + max;
                break;
            case DownloadSize:
                downloadDescription += " " + ((double)(downloadedSize / bytesToMB)) + "/" + ((double)(totalDownloadSize / bytesToMB));
        }
        NotificationHelper.CreateProgressNotification(
                DOWNLOAD_REQUEST_TITLE,
                downloadDescription,
                DOWNLOAD_REQUEST_NOTIFICATION_ICON,
                max,
                progress,
                progress == 0,
                DOWNLOAD_REQUEST_CHANNELID,
                getAppContext(),
                DOWNLOAD_REQUEST_NOTIFICATIONID);
    }

    public void DisplayDownloadCompletedNotification(){
        if(m_showCumulativeNotification){
        NotificationHelper.CreateBigTextNotification(
                DOWNLOAD_REQUEST_TITLE,
                DOWNLOAD_REQUEST_DESCRIPTION_COMPLETED,
                DOWNLOAD_REQUEST_NOTIFICATION_ICON,
                DOWNLOAD_REQUEST_CHANNELID,
                getAppContext(),
                DOWNLOAD_REQUEST_NOTIFICATIONID);
        }
    }
    // ------------------------------

/*
	public static final int PROGRESSBAR_ID	= 555;
	public static final String CHANNEL_ID = "downloads";
	//Variables used to display total dlc download Progress in notification bar
	long m_dlcSize = 0; // Get from total size of all files
	long m_dlcDownloadSize = 0; // Get from total downloaded size
	long m_fileDownloadedSize = 0; // Get from current downloaded size
	long m_fileSize = 0; // Get from current max file size
	boolean m_isDownloadCompleted = false; // True when download complete for the file
	static boolean m_isAppInBackground = false;
	static boolean m_canShowDownloadPogress = false;
    void displayProgressNotification()
    {
        final NotificationManager mNotificationManager = (NotificationManager) CustomNativeActivity.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent1 = new Intent(CustomNativeActivity.getAppContext(), CustomNativeActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(CustomNativeActivity.getAppContext(), 111, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder mCompatBuilder;
        final Notification.Builder mBuilder;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            mCompatBuilder = null;
            //Create notification Channel
            int importance = NotificationManager.IMPORTANCE_LOW;		//IMPORTANCE_LOW makes notification to be triggred silently
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "My Samples", importance);
            mChannel.setDescription("Downloading Contents");
            mChannel.enableLights(true);
            //mChannel.enableVibration(true);
            //mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);

            mBuilder = new Notification.Builder(CustomNativeActivity.getAppContext(), CHANNEL_ID);

            mBuilder.setContentTitle("My Samples");
            mBuilder.setContentText(CustomNativeActivity.getAppContext().getString(R.string.dlc_progressbar_notif_desc));
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setSmallIcon(R.drawable.ic_stat_notify);
            mBuilder.setChannelId(CHANNEL_ID);
            mBuilder.setAutoCancel(true);
        }
        else
        {
            mBuilder = null;
            mCompatBuilder = new NotificationCompat.Builder(CustomNativeActivity.getAppContext());
            mCompatBuilder.setContentTitle("My Samples")
                    .setContentText(CustomNativeActivity.getAppContext().getString(R.string.dlc_progressbar_notif_desc))
                    .setSmallIcon(R.drawable.ic_stat_notify)
                    .setContentIntent(pendingIntent);
        }
        // Start a lengthy operation in a background thread
        new Thread(
                new Runnable() {
                    @Override
                    public void run()
                    {
                        try
                        {
                            while(m_fileDownloadedSize <= 0 && !m_isDownloadCompleted && m_isAppInBackground)
                            {
                                Thread.sleep(100);
                                getDownloadedSize();
                            }
                        } catch (InterruptedException e)
                        {
                            Log.d(TAG, "sleep failure");
                        }

                        //Log.d(TAG, "THREAD Run start " + m_fileDownloadedSize + "/" + m_fileSize);

                        while(m_fileDownloadedSize < m_fileSize && !m_isDownloadCompleted && m_isAppInBackground)
                        {
                            try
                            {
                                getDownloadedSize();
                                int dlcPercentDone = (int)((m_dlcDownloadSize + m_fileDownloadedSize) / (float)m_dlcSize * 100);

                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                {
                                    mBuilder.setProgress(100, dlcPercentDone, false);
                                    mNotificationManager.notify(PROGRESSBAR_ID, mBuilder.build());
                                }
                                else
                                {
                                    mCompatBuilder.setProgress(100, dlcPercentDone, false);
                                    mCompatBuilder.setAutoCancel(true);
                                    mNotificationManager.notify(PROGRESSBAR_ID, mCompatBuilder.build());
                                }

                                Thread.sleep(1000);

                            } catch (InterruptedException e)
                            {
                                Log.d(TAG, "sleep failure");
                            }
                        }
                    }
                }
        ).start();
    }

	public static void cancelProgressBar(boolean isDlcFinished)
	{
		final NotificationManager mNotificationManager = (NotificationManager) CustomNativeActivity.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(NativeDownloadManager.PROGRESSBAR_ID);

		if(isDlcFinished)
			m_canShowDownloadPogress = false;	//Reset after DLC finishes
	}

	public static void showProgressBar(boolean canShow)
	{
		m_canShowDownloadPogress = canShow;
	}*/
}
