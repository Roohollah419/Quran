package com.example.quran.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.io.File;

/**
 * Helper class to manage audio file downloads using Android's DownloadManager.
 * Provides methods to enqueue, monitor, and cancel downloads.
 */
public class DownloadManagerHelper {

    private final Context context;
    private final DownloadManager downloadManager;

    public DownloadManagerHelper(Context context) {
        this.context = context;
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    /**
     * Enqueue a download request.
     *
     * @param url         The URL to download from
     * @param destination The local file path to save to
     * @param title       The title to display in the notification
     * @return The download ID assigned by DownloadManager, or -1 if failed
     */
    public long enqueueDownload(String url, String destination, String title) {
        if (downloadManager == null) {
            return -1;
        }

        try {
            // Extract relative path from destination
            // destination format: /data/data/com.example.quran/files/recitations/alafasy/001.mp3
            // We need: recitations/alafasy/001.mp3
            File destFile = new File(destination);
            String fileName = destFile.getName(); // 001.mp3
            String parentDir = destFile.getParentFile().getName(); // alafasy

            // Create download request
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(title);
            request.setDescription("Downloading Quran recitation");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

            // Use external files directory which is compatible with DownloadManager
            // This is still app-private and gets deleted when app is uninstalled
            request.setDestinationInExternalFilesDir(context, "recitations/" + parentDir, fileName);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

            // Enqueue the download
            return downloadManager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Get download progress for a specific download ID.
     *
     * @param downloadId The download ID
     * @return DownloadProgress object containing status and progress, or null if not found
     */
    public DownloadProgress getDownloadProgress(long downloadId) {
        if (downloadManager == null || downloadId == -1) {
            return null;
        }

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        try (Cursor cursor = downloadManager.query(query)) {
            if (cursor != null && cursor.moveToFirst()) {
                int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int bytesDownloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                int bytesTotalIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);

                int status = cursor.getInt(statusIndex);
                long bytesDownloaded = cursor.getLong(bytesDownloadedIndex);
                long bytesTotal = cursor.getLong(bytesTotalIndex);

                int progress = 0;
                if (bytesTotal > 0) {
                    progress = (int) ((bytesDownloaded * 100L) / bytesTotal);
                }

                return new DownloadProgress(status, progress, bytesDownloaded, bytesTotal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Cancel a download.
     *
     * @param downloadId The download ID to cancel
     * @return true if successfully canceled, false otherwise
     */
    public boolean cancelDownload(long downloadId) {
        if (downloadManager == null || downloadId == -1) {
            return false;
        }

        try {
            int removed = downloadManager.remove(downloadId);
            return removed > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if a download has completed successfully.
     *
     * @param downloadId The download ID
     * @return true if completed successfully, false otherwise
     */
    public boolean isDownloadSuccessful(long downloadId) {
        DownloadProgress progress = getDownloadProgress(downloadId);
        return progress != null && progress.status == DownloadManager.STATUS_SUCCESSFUL;
    }

    /**
     * Check if a download has failed.
     *
     * @param downloadId The download ID
     * @return true if failed, false otherwise
     */
    public boolean isDownloadFailed(long downloadId) {
        DownloadProgress progress = getDownloadProgress(downloadId);
        return progress != null && progress.status == DownloadManager.STATUS_FAILED;
    }

    /**
     * Get the local file URI for a completed download.
     *
     * @param downloadId The download ID
     * @return The local file URI, or null if not found
     */
    public String getDownloadedFileUri(long downloadId) {
        if (downloadManager == null || downloadId == -1) {
            return null;
        }

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        try (Cursor cursor = downloadManager.query(query)) {
            if (cursor != null && cursor.moveToFirst()) {
                int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                return cursor.getString(uriIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Class to hold download progress information.
     */
    public static class DownloadProgress {
        public final int status; // DownloadManager.STATUS_*
        public final int progress; // 0-100
        public final long bytesDownloaded;
        public final long bytesTotal;

        public DownloadProgress(int status, int progress, long bytesDownloaded, long bytesTotal) {
            this.status = status;
            this.progress = progress;
            this.bytesDownloaded = bytesDownloaded;
            this.bytesTotal = bytesTotal;
        }

        public boolean isRunning() {
            return status == DownloadManager.STATUS_RUNNING || status == DownloadManager.STATUS_PENDING;
        }

        public boolean isSuccessful() {
            return status == DownloadManager.STATUS_SUCCESSFUL;
        }

        public boolean isFailed() {
            return status == DownloadManager.STATUS_FAILED;
        }

        public boolean isPaused() {
            return status == DownloadManager.STATUS_PAUSED;
        }
    }
}
