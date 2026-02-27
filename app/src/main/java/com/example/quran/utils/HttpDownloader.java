package com.example.quran.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Custom HTTP downloader with configurable timeouts and better slow network handling.
 * Alternative to DownloadManager for more control over download process.
 */
public class HttpDownloader {

    private static final String TAG = "HttpDownloader";
    private static final int CONNECTION_TIMEOUT = 30000; // 30 seconds
    private static final int READ_TIMEOUT = 60000; // 60 seconds
    private static final int BUFFER_SIZE = 8192; // 8 KB
    private static final int PROGRESS_UPDATE_INTERVAL = 500; // 500ms

    private final ExecutorService executorService;
    private final Handler mainHandler;

    public HttpDownloader() {
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Download a file from URL to destination path.
     */
    public void download(String urlString, String destinationPath, DownloadListener listener) {
        Log.d(TAG, "Download started: " + urlString);
        executorService.execute(() -> {
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            FileOutputStream outputStream = null;

            try {
                // Create parent directories
                File destFile = new File(destinationPath);
                File parentDir = destFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }
                Log.d(TAG, "Destination: " + destinationPath);

                // Setup connection
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setRequestMethod("GET");
                Log.d(TAG, "Connecting...");
                connection.connect();

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    notifyError(listener, "HTTP error code: " + responseCode);
                    return;
                }

                long fileSize = connection.getContentLength();
                Log.d(TAG, "File size: " + fileSize + " bytes");
                inputStream = connection.getInputStream();
                outputStream = new FileOutputStream(destFile);

                byte[] buffer = new byte[BUFFER_SIZE];
                long totalBytesRead = 0;
                int bytesRead;
                long lastProgressUpdate = System.currentTimeMillis();

                notifyProgress(listener, 0, fileSize);

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    // Update progress every 500ms
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastProgressUpdate >= PROGRESS_UPDATE_INTERVAL) {
                        notifyProgress(listener, totalBytesRead, fileSize);
                        lastProgressUpdate = currentTime;
                    }
                }

                outputStream.flush();
                notifyProgress(listener, totalBytesRead, fileSize); // Final update
                Log.d(TAG, "Download completed successfully: " + totalBytesRead + " bytes");
                notifySuccess(listener, destinationPath, fileSize);

            } catch (Exception e) {
                Log.e(TAG, "Download failed: " + e.getMessage(), e);
                // Delete partial file
                File destFile = new File(destinationPath);
                if (destFile.exists()) {
                    destFile.delete();
                }
                notifyError(listener, e.getMessage());
            } finally {
                try {
                    if (outputStream != null) outputStream.close();
                    if (inputStream != null) inputStream.close();
                    if (connection != null) connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void notifyProgress(DownloadListener listener, long bytesDownloaded, long totalBytes) {
        if (listener != null) {
            int progress = totalBytes > 0 ? (int) ((bytesDownloaded * 100) / totalBytes) : 0;
            mainHandler.post(() -> listener.onProgress(progress, bytesDownloaded, totalBytes));
        }
    }

    private void notifySuccess(DownloadListener listener, String filePath, long fileSize) {
        if (listener != null) {
            mainHandler.post(() -> listener.onSuccess(filePath, fileSize));
        }
    }

    private void notifyError(DownloadListener listener, String error) {
        if (listener != null) {
            mainHandler.post(() -> listener.onError(error));
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }

    /**
     * Listener interface for download callbacks.
     */
    public interface DownloadListener {
        void onProgress(int progress, long bytesDownloaded, long totalBytes);
        void onSuccess(String filePath, long fileSize);
        void onError(String error);
    }
}
