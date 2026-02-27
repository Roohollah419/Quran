package com.example.quran.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quran.data.local.entity.RecitationEntity;

import java.util.List;

/**
 * Data Access Object for Recitation operations.
 * Provides methods to access and manipulate recitation data in the database.
 */
@Dao
public interface RecitationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RecitationEntity recitation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RecitationEntity> recitations);

    @Update
    void update(RecitationEntity recitation);

    @Delete
    void delete(RecitationEntity recitation);

    @Query("SELECT * FROM recitations WHERE reciterName = :reciterName ORDER BY surahNumber ASC")
    LiveData<List<RecitationEntity>> getRecitationsByReciter(String reciterName);

    @Query("SELECT * FROM recitations WHERE surahNumber = :surahNumber AND reciterName = :reciterName")
    LiveData<RecitationEntity> getRecitationBySurahAndReciter(int surahNumber, String reciterName);

    @Query("SELECT * FROM recitations WHERE surahNumber = :surahNumber AND reciterName = :reciterName")
    RecitationEntity getRecitationBySurahAndReciterSync(int surahNumber, String reciterName);

    @Query("SELECT * FROM recitations WHERE id = :id")
    LiveData<RecitationEntity> getRecitationById(int id);

    @Query("SELECT * FROM recitations WHERE downloadStatus = :status")
    LiveData<List<RecitationEntity>> getRecitationsByStatus(String status);

    @Query("SELECT * FROM recitations WHERE downloadId = :downloadId")
    RecitationEntity getRecitationByDownloadId(long downloadId);

    @Query("UPDATE recitations SET downloadStatus = :status, downloadProgress = :progress WHERE id = :id")
    void updateDownloadStatus(int id, String status, int progress);

    @Query("UPDATE recitations SET downloadStatus = :status, downloadProgress = :progress, downloadId = :downloadId WHERE id = :id")
    void updateDownloadStatusWithId(int id, String status, int progress, long downloadId);

    @Query("UPDATE recitations SET localFilePath = :filePath, downloadStatus = 'DOWNLOADED', downloadProgress = 100, fileSize = :fileSize WHERE id = :id")
    void markAsDownloaded(int id, String filePath, long fileSize);

    @Query("UPDATE recitations SET downloadStatus = 'FAILED', downloadProgress = 0, downloadId = -1 WHERE id = :id")
    void markAsFailed(int id);

    @Query("DELETE FROM recitations WHERE reciterName = :reciterName")
    void deleteAllByReciter(String reciterName);

    @Query("DELETE FROM recitations")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM recitations")
    int getRecitationCount();

    @Query("SELECT COUNT(*) FROM recitations WHERE downloadStatus = 'DOWNLOADED'")
    int getDownloadedCount();

    @Query("SELECT COUNT(*) FROM recitations WHERE reciterName = :reciterName AND downloadStatus = 'DOWNLOADED'")
    int getDownloadedCountByReciter(String reciterName);
}
