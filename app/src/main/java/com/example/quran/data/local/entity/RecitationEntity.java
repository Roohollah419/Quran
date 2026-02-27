package com.example.quran.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room entity representing a Quran recitation audio file in the database.
 * Stores metadata about downloaded or downloadable audio files for each Surah.
 */
@Entity(tableName = "recitations")
public class RecitationEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int surahNumber;
    private String reciterName;
    private String downloadUrl;
    private String localFilePath;
    private String downloadStatus; // NOT_DOWNLOADED, DOWNLOADING, DOWNLOADED, FAILED
    private int downloadProgress; // 0-100
    private long fileSize; // in bytes
    private long downloadId; // Android DownloadManager ID, -1 if not downloading

    public RecitationEntity() {
    }

    @androidx.room.Ignore
    public RecitationEntity(int surahNumber, String reciterName, String downloadUrl,
                           String localFilePath, String downloadStatus, int downloadProgress,
                           long fileSize, long downloadId) {
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

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(String downloadStatus) {
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
}
