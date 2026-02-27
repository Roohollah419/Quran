package com.example.quran.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.quran.data.local.QuranDatabase;
import com.example.quran.data.local.dao.RecitationDao;
import com.example.quran.data.local.entity.RecitationEntity;
import com.example.quran.data.model.Recitation;
import com.example.quran.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing Quran recitation audio data.
 * Handles download management, file storage, and database operations.
 */
public class RecitationRepository {

    private final Context context;
    private final RecitationDao recitationDao;

    public RecitationRepository(Context context) {
        this.context = context;
        QuranDatabase database = QuranDatabase.getInstance(context);
        recitationDao = database.recitationDao();

        // Seed recitation metadata if empty
        seedRecitationMetadataIfEmpty();
    }

    /**
     * Get all recitations for a specific reciter.
     */
    public LiveData<List<Recitation>> getRecitationsByReciter(String reciterName) {
        return Transformations.map(recitationDao.getRecitationsByReciter(reciterName),
                this::convertRecitationEntitiesToModels);
    }

    /**
     * Get recitation for a specific surah and reciter.
     */
    public LiveData<Recitation> getRecitationBySurahAndReciter(int surahNumber, String reciterName) {
        return Transformations.map(recitationDao.getRecitationBySurahAndReciter(surahNumber, reciterName),
                this::convertRecitationEntityToModel);
    }

    /**
     * Get recitation by ID.
     */
    public LiveData<Recitation> getRecitationById(int id) {
        return Transformations.map(recitationDao.getRecitationById(id),
                this::convertRecitationEntityToModel);
    }

    /**
     * Get recitations by download status.
     */
    public LiveData<List<Recitation>> getRecitationsByStatus(String status) {
        return Transformations.map(recitationDao.getRecitationsByStatus(status),
                this::convertRecitationEntitiesToModels);
    }

    /**
     * Update download status.
     */
    public void updateDownloadStatus(int id, String status, int progress) {
        QuranDatabase.databaseWriteExecutor.execute(() ->
                recitationDao.updateDownloadStatus(id, status, progress));
    }

    /**
     * Update download status with download ID.
     */
    public void updateDownloadStatusWithId(int id, String status, int progress, long downloadId) {
        QuranDatabase.databaseWriteExecutor.execute(() ->
                recitationDao.updateDownloadStatusWithId(id, status, progress, downloadId));
    }

    /**
     * Mark recitation as downloaded.
     */
    public void markAsDownloaded(int id, String filePath, long fileSize) {
        QuranDatabase.databaseWriteExecutor.execute(() ->
                recitationDao.markAsDownloaded(id, filePath, fileSize));
    }

    /**
     * Mark recitation as failed.
     */
    public void markAsFailed(int id) {
        QuranDatabase.databaseWriteExecutor.execute(() ->
                recitationDao.markAsFailed(id));
    }

    /**
     * Delete recitation and its file.
     */
    public void deleteRecitation(int id, DeleteCallback callback) {
        QuranDatabase.databaseWriteExecutor.execute(() -> {
            RecitationEntity entity = recitationDao.getRecitationByDownloadId(id);
            if (entity != null) {
                // Delete file if exists
                if (entity.getLocalFilePath() != null) {
                    File file = new File(entity.getLocalFilePath());
                    if (file.exists()) {
                        file.delete();
                    }
                }

                // Reset entity
                entity.setDownloadStatus(Constants.STATUS_NOT_DOWNLOADED);
                entity.setDownloadProgress(0);
                entity.setDownloadId(-1);
                entity.setLocalFilePath(null);
                recitationDao.update(entity);

                if (callback != null) {
                    callback.onDeleted();
                }
            }
        });
    }

    /**
     * Delete all recitations for a reciter.
     */
    public void deleteAllByReciter(String reciterName, DeleteCallback callback) {
        QuranDatabase.databaseWriteExecutor.execute(() -> {
            // Delete all files for this reciter
            File reciterDir = new File(context.getFilesDir(), "recitations/" + reciterName);
            if (reciterDir.exists() && reciterDir.isDirectory()) {
                File[] files = reciterDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
                reciterDir.delete();
            }

            // Reset all recitations for this reciter in database
            recitationDao.deleteAllByReciter(reciterName);
            seedRecitationMetadataForReciter(reciterName);

            if (callback != null) {
                callback.onDeleted();
            }
        });
    }

    /**
     * Get recitation by Android DownloadManager download ID.
     */
    public void getRecitationByDownloadId(long downloadId, RecitationCallback callback) {
        QuranDatabase.databaseWriteExecutor.execute(() -> {
            RecitationEntity entity = recitationDao.getRecitationByDownloadId(downloadId);
            if (callback != null) {
                callback.onRecitation(convertRecitationEntityToModel(entity));
            }
        });
    }

    /**
     * Get downloaded count for a reciter.
     */
    public void getDownloadedCountByReciter(String reciterName, CountCallback callback) {
        QuranDatabase.databaseWriteExecutor.execute(() -> {
            int count = recitationDao.getDownloadedCountByReciter(reciterName);
            if (callback != null) {
                callback.onCount(count);
            }
        });
    }

    /**
     * Seed recitation metadata if empty.
     */
    private void seedRecitationMetadataIfEmpty() {
        QuranDatabase.databaseWriteExecutor.execute(() -> {
            int count = recitationDao.getRecitationCount();
            if (count == 0) {
                seedAllRecitationMetadata();
            }
        });
    }

    /**
     * Seed metadata for all reciters and surahs.
     */
    private void seedAllRecitationMetadata() {
        seedRecitationMetadataForReciter(Constants.RECITER_ALAFASY);
        seedRecitationMetadataForReciter(Constants.RECITER_MINSHAWI);
    }

    /**
     * Seed metadata for a specific reciter.
     */
    private void seedRecitationMetadataForReciter(String reciterName) {
        List<RecitationEntity> recitations = new ArrayList<>();
        String baseUrl = reciterName.equals(Constants.RECITER_ALAFASY)
                ? Constants.AUDIO_BASE_URL_ALAFASY
                : Constants.AUDIO_BASE_URL_MINSHAWI;

        for (int i = 1; i <= Constants.TOTAL_SURAHS; i++) {
            String surahNumberPadded = String.format("%03d", i);
            String downloadUrl = baseUrl + surahNumberPadded + ".mp3";
            String localFilePath = getLocalFilePath(reciterName, i);

            RecitationEntity entity = new RecitationEntity(
                    i,
                    reciterName,
                    downloadUrl,
                    localFilePath,
                    Constants.STATUS_NOT_DOWNLOADED,
                    0,
                    0, // fileSize will be determined during download
                    -1 // no download ID yet
            );
            recitations.add(entity);
        }

        recitationDao.insertAll(recitations);
    }

    /**
     * Get local file path for a recitation.
     */
    public String getLocalFilePath(String reciterName, int surahNumber) {
        File recitationsDir = new File(context.getFilesDir(), "recitations");
        File reciterDir = new File(recitationsDir, reciterName);
        if (!reciterDir.exists()) {
            reciterDir.mkdirs();
        }
        String surahNumberPadded = String.format("%03d", surahNumber);
        return new File(reciterDir, surahNumberPadded + ".mp3").getAbsolutePath();
    }

    /**
     * Convert list of RecitationEntity to list of Recitation models.
     */
    private List<Recitation> convertRecitationEntitiesToModels(List<RecitationEntity> entities) {
        List<Recitation> recitations = new ArrayList<>();
        for (RecitationEntity entity : entities) {
            recitations.add(convertRecitationEntityToModel(entity));
        }
        return recitations;
    }

    /**
     * Convert RecitationEntity to Recitation model.
     */
    private Recitation convertRecitationEntityToModel(RecitationEntity entity) {
        if (entity == null) return null;
        return new Recitation(
                entity.getId(),
                entity.getSurahNumber(),
                entity.getReciterName(),
                entity.getDownloadUrl(),
                entity.getLocalFilePath(),
                Recitation.fromString(entity.getDownloadStatus()),
                entity.getDownloadProgress(),
                entity.getFileSize(),
                entity.getDownloadId()
        );
    }

    /**
     * Callback for delete operations.
     */
    public interface DeleteCallback {
        void onDeleted();
    }

    /**
     * Callback for recitation retrieval.
     */
    public interface RecitationCallback {
        void onRecitation(Recitation recitation);
    }

    /**
     * Callback for count operations.
     */
    public interface CountCallback {
        void onCount(int count);
    }
}
