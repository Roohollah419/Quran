package com.example.quran.utils;

import android.content.Context;
import android.os.StatFs;

import java.io.File;
import java.util.Locale;

/**
 * Helper class for storage-related operations.
 * Provides methods to check available space and format file sizes.
 */
public class StorageHelper {

    // Minimum required free space: 100 MB
    public static final long MIN_REQUIRED_SPACE_BYTES = 100 * 1024 * 1024;

    /**
     * Get available internal storage space in bytes.
     *
     * @param context Application context
     * @return Available space in bytes
     */
    public static long getAvailableSpace(Context context) {
        try {
            File filesDir = context.getFilesDir();
            StatFs stat = new StatFs(filesDir.getPath());
            return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Check if there is enough space for a download.
     *
     * @param context      Application context
     * @param requiredSize Required size in bytes
     * @return true if there is enough space, false otherwise
     */
    public static boolean hasEnoughSpace(Context context, long requiredSize) {
        long availableSpace = getAvailableSpace(context);
        return availableSpace >= (requiredSize + MIN_REQUIRED_SPACE_BYTES);
    }

    /**
     * Format file size in human-readable format.
     *
     * @param bytes File size in bytes
     * @return Formatted string (e.g., "1.5 MB", "500 KB")
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 0) {
            return "0 B";
        }

        final long KB = 1024;
        final long MB = KB * 1024;
        final long GB = MB * 1024;

        if (bytes >= GB) {
            return String.format(Locale.US, "%.2f GB", bytes / (double) GB);
        } else if (bytes >= MB) {
            return String.format(Locale.US, "%.2f MB", bytes / (double) MB);
        } else if (bytes >= KB) {
            return String.format(Locale.US, "%.2f KB", bytes / (double) KB);
        } else {
            return bytes + " B";
        }
    }

    /**
     * Calculate total size of all files in a directory.
     *
     * @param directory The directory to calculate
     * @return Total size in bytes
     */
    public static long getDirectorySize(File directory) {
        long size = 0;
        if (directory != null && directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        size += file.length();
                    } else if (file.isDirectory()) {
                        size += getDirectorySize(file);
                    }
                }
            }
        }
        return size;
    }

    /**
     * Get total size of downloaded recitations.
     *
     * @param context Application context
     * @return Total size in bytes
     */
    public static long getTotalDownloadedSize(Context context) {
        File recitationsDir = new File(context.getFilesDir(), "recitations");
        return getDirectorySize(recitationsDir);
    }

    /**
     * Get total size of downloaded recitations for a specific reciter.
     *
     * @param context     Application context
     * @param reciterName Reciter name
     * @return Total size in bytes
     */
    public static long getReciterDownloadedSize(Context context, String reciterName) {
        File reciterDir = new File(context.getFilesDir(), "recitations/" + reciterName);
        return getDirectorySize(reciterDir);
    }

    /**
     * Delete all downloaded files for a reciter.
     *
     * @param context     Application context
     * @param reciterName Reciter name
     * @return true if successful, false otherwise
     */
    public static boolean deleteReciterFiles(Context context, String reciterName) {
        File reciterDir = new File(context.getFilesDir(), "recitations/" + reciterName);
        return deleteDirectory(reciterDir);
    }

    /**
     * Delete a directory and all its contents.
     *
     * @param directory The directory to delete
     * @return true if successful, false otherwise
     */
    private static boolean deleteDirectory(File directory) {
        if (directory != null && directory.exists()) {
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        deleteDirectory(file);
                    }
                }
            }
            return directory.delete();
        }
        return false;
    }

    /**
     * Validate file after download.
     * Checks if file exists and has a reasonable size.
     *
     * @param filePath Path to the downloaded file
     * @param minSize  Minimum expected file size in bytes (e.g., 100KB for MP3)
     * @return true if file is valid, false otherwise
     */
    public static boolean validateDownloadedFile(String filePath, long minSize) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        File file = new File(filePath);
        return file.exists() && file.isFile() && file.length() >= minSize;
    }
}
