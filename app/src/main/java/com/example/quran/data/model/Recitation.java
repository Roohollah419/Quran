package com.example.quran.data.model;

/**
 * Model class representing a Quran recitation audio file.
 * This is a POJO used in the UI layer, separate from the database entity.
 */
public class Recitation {

    /**
     * Enum representing the download status of a recitation.
     */
    public enum DownloadStatus {
        NOT_DOWNLOADED,
        DOWNLOADING,
        DOWNLOADED,
        FAILED
    }

    private int id;
    private int surahNumber;
    private String reciterName;
    private String downloadUrl;
    private String localFilePath;
    private DownloadStatus downloadStatus;
    private int downloadProgress; // 0-100
    private long fileSize; // in bytes
    private long downloadId; // Android DownloadManager ID, -1 if not downloading

    public Recitation() {
    }

    public Recitation(int id, int surahNumber, String reciterName, String downloadUrl,
                      String localFilePath, DownloadStatus downloadStatus, int downloadProgress,
                      long fileSize, long downloadId) {
        this.id = id;
        this.surahNumber = surahNumber;
        this.reciterName = reciterName;
        this.downloadUrl = downloadUrl;
        this.localFilePath = localFilePath;
        this.downloadStatus = downloadStatus;
        this.downloadProgress = downloadProgress;
        this.fileSize = fileSize;
        this.downloadId = downloadId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSurahNumber() {
        return surahNumber;
    }

    public void setSurahNumber(int surahNumber) {
        this.surahNumber = surahNumber;
    }

    public String getReciterName() {
        return reciterName;
    }

    public void setReciterName(String reciterName) {
        this.reciterName = reciterName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public DownloadStatus getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(DownloadStatus downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    /**
     * Helper method to convert string status to enum.
     */
    public static DownloadStatus fromString(String status) {
        try {
            return DownloadStatus.valueOf(status);
        } catch (IllegalArgumentException | NullPointerException e) {
            return DownloadStatus.NOT_DOWNLOADED;
        }
    }

    /**
     * Helper method to check if recitation is downloaded.
     */
    public boolean isDownloaded() {
        return downloadStatus == DownloadStatus.DOWNLOADED;
    }

    /**
     * Helper method to check if recitation is currently downloading.
     */
    public boolean isDownloading() {
        return downloadStatus == DownloadStatus.DOWNLOADING;
    }
}
