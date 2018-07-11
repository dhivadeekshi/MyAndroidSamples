package com.dhivakar.mysamples.download;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

public class FileDownloader {

    private DownloadManager m_downloadManager;
    private File m_destinationPath;
    public long m_fileReference;
    public String m_fileName;
    private String m_destination;
    private boolean m_isDownloadCompleted;
    public boolean m_isDownloadSucceded;

    private void LogDebug(String message)
    {
        NativeDownloadManager.LogDebug(message);
    }
    
    private void LogError(String message)
    {
        NativeDownloadManager.LogError(message);
    }
    
    public FileDownloader(DownloadManager downloadManager, File destinationPath) {
        m_downloadManager = downloadManager;
        m_destinationPath = destinationPath;
        m_fileReference = 0;
    }

    public void StartDownload(String fileUrl, String fileName, String destination, Context context, boolean showNotification, String title, String description) {
        //Remove the file before downloading
        deleteFileIfExists(fileName);

        //Create request for android download manager
        DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(fileUrl));

        //Setting title of request
        downloadRequest.setTitle(title);

        //Setting description of request
        downloadRequest.setDescription(description);

        //Set the local destination for the downloaded file to a path within the application's external files directory
        downloadRequest.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOCUMENTS, fileName);

        //set allowed network types for download
        downloadRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        //set if notification to be displayed
        downloadRequest.setNotificationVisibility(showNotification ? DownloadManager.Request.VISIBILITY_VISIBLE : DownloadManager.Request.VISIBILITY_HIDDEN);

        try {
            //Enqueue download and save into referenceId
            m_fileReference = m_downloadManager.enqueue(downloadRequest);
        } catch (Exception e) {
            e.printStackTrace();
            LogDebug("Start Download manager failed : " + e.getMessage());
        }

        m_fileName = fileName;
        m_destination = destination;
        m_isDownloadCompleted = false;
        m_isDownloadSucceded = false;
    }

    public void CancelDownload() {
        if (m_downloadManager != null && !m_isDownloadCompleted) {
            m_downloadManager.remove(m_fileReference);
        }
    }

    public long GetDownloadedSize() {
        long downloadedSize = 0;

        try {

            // Create a query to fetch the download info for the respective file
            DownloadManager.Query downloadQuery = new DownloadManager.Query();
            downloadQuery.setFilterById(m_fileReference);

            // Fetch info using the query
            Cursor cursor = m_downloadManager.query(downloadQuery);
            if (cursor != null && cursor.moveToFirst()) {

                //get Column for downloaded file size so far
                int sizeSoFarIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                if(sizeSoFarIndex != -1) downloadedSize = cursor.getLong(sizeSoFarIndex);            //this method throws an exception when the column value is null

                cursor.close();
            } else {
                LogError("GetDownloaded size failed download not found fileReference:"+m_fileReference+" fileName:"+m_fileName);
            }

        } catch (Exception e) {
            LogError("GetDownloaded size failed for fileReference:"+m_fileReference+" fileName:"+m_fileName+" with exception " + e.getMessage());
        }

        return downloadedSize;
    }

    public long GetFileSize() {
        long fileSize = 0;

        try {

            // Create a query to fetch the download info for the respective file
            DownloadManager.Query downloadQuery = new DownloadManager.Query();
            downloadQuery.setFilterById(m_fileReference);

            // Fetch info using the query
            Cursor cursor = m_downloadManager.query(downloadQuery);
            if (cursor != null && cursor.moveToFirst()) {

                //get Column for total file size
                int sizeSoFarIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                if(sizeSoFarIndex != -1) fileSize = cursor.getLong(sizeSoFarIndex);            //this method throws an exception when the column value is null

                cursor.close();
            } else {
                LogError("GetFile size failed download not found fileReference:"+m_fileReference+" fileName:"+m_fileName);
            }

        } catch (Exception e) {
            LogError("GetFile size failed for fileReference:"+m_fileReference+" fileName:"+m_fileName+" with exception " + e.getMessage());
        }

        return fileSize;
    }

    private void deleteFileIfExists(String fileName) {
        if (m_destinationPath != null) {
            try {
                File file = new File(m_destinationPath, fileName);
                if (file.exists()) {
                    if (!file.delete())
                        LogDebug("deleteFileIfExists " + fileName + " Delete Failed");
                }
            } catch (Exception e) {
                LogDebug("deleteFileIfExists Got Exception = " + e);
            }
        }
    }

    public void OnDownloadCompleted(boolean success)
    {
        m_isDownloadCompleted = true;
        m_isDownloadSucceded = success;
    }

    public boolean isDownloadedFileExists()
    {
        if (m_destinationPath != null) {
            try {
                File file = new File(m_destinationPath, m_fileName);
                return file.exists();
            } catch (Exception e) {
                LogDebug("isDownloadedFileExists fileReference:"+m_fileReference+" fileName:"+m_fileName+" Got Exception = " + e);
            }
        }
        return false;
    }

    public boolean isDownloadCompleted()
    {
        return m_isDownloadCompleted;
    }
    public boolean isDownloadSucceeded() { return m_isDownloadSucceded; }

    private boolean CreateDestinationFolders(String destPath)
    {
        File dest = new File(destPath);
        boolean isFileExists = dest.exists();
        boolean isParentExists = dest.getParentFile().exists();
        if(isFileExists)
            return true;
        else if(isParentExists)
            return dest.mkdir();
        else if(CreateDestinationFolders(dest.getParent()))
            return dest.mkdir();
        return true;
    }

    public boolean MoveFileToDestination()
    {
        if (m_destinationPath != null && m_destination != null) {
            try {
                File file = new File(m_destinationPath, m_fileName);

                if(file.exists()) {
                    File dest = new File(m_destination, m_fileName);

                    // Create the parent dir if doesn't exist return failed if we can't create the parent dir
                    CreateDestinationFolders(dest.getParent());

                    // Move the file to the destination and delete the source file and return fail if can't move or delete source
                    if(!file.renameTo(dest) && !file.delete())
                        return false;

                    // Clear the empty folders

                    return true;
                }
                else
                {
                    LogError("Dowmloaded File not Present fileName:"+m_fileName+" fileReference:"+m_fileReference);
                }
            } catch (Exception e) {
                LogDebug("isDownloadedFileExists fileReference:"+m_fileReference+" fileName:"+m_fileName+" Got Exception = " + e.getMessage());
            }
        }
        return false;
    }

}
