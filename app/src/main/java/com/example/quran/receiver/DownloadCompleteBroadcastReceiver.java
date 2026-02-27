package com.example.quran.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.example.quran.data.repository.RecitationRepository;
import com.example.quran.utils.Constants;
import com.example.quran.utils.StorageHelper;

import java.io.File;

/**
 * BroadcastReceiver that listens for download completion events.
 * Updates the recitation database when downloads finish successfully or fail.
 */
public class DownloadCompleteBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        // Check if this is a download complete action
        if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            return;
        }

        // Get the download ID
        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (downloadId == -1) {
            return;
        }

        // Handle the completed download
        handleDownloadComplete(context, downloadId);
    }

    /**
     * Handle a completed download.
     * Checks if the download was successful and updates the database accordingly.
     *
     * @param context    Application context
     * @param downloadId The download ID from DownloadManager
     */
    private void handleDownloadComplete(Context context, long downloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager == null) {
            return;
        }

        // Query the download manager for this download
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        try (Cursor cursor = downloadManager.query(query)) {
            if (cursor != null && cursor.moveToFirst()) {
                int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = cursor.getInt(statusIndex);

                RecitationRepository repository = new RecitationRepository(context);

                // Get the recitation from the database using the download ID
                repository.getRecitationByDownloadId(downloadId, recitation -> {
                    if (recitation == null) {
                        return;
                    }

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        // Download successful - validate the file
                        String filePath = recitation.getLocalFilePath();
                        if (StorageHelper.validateDownloadedFile(filePath, 50 * 1024)) { // Minimum 50KB
                            // Get actual file size
                            File file = new File(filePath);
                            long fileSize = file.length();
                            // Mark as downloaded
                            repository.markAsDownloaded(recitation.getId(), filePath, fileSize);
                        } else {
                            // File validation failed - mark as failed
                            repository.markAsFailed(recitation.getId());
                        }
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        // Download failed
                        repository.markAsFailed(recitation.getId());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
