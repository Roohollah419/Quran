package com.example.quran.ui.downloadmanager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quran.data.model.Recitation;
import com.example.quran.data.model.Surah;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.data.repository.RecitationRepository;
import com.example.quran.ui.base.BaseViewModel;
import com.example.quran.utils.Constants;
import com.example.quran.utils.DownloadManagerHelper;
import com.example.quran.utils.HttpDownloader;
import com.example.quran.utils.StorageHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ViewModel for Download Manager screen.
 * Manages recitation downloads and provides data to the UI.
 */
public class DownloadManagerViewModel extends BaseViewModel {

    private final RecitationRepository recitationRepository;
    private final DownloadManagerHelper downloadManagerHelper;
    private final HttpDownloader httpDownloader;
    private final Map<Integer, Boolean> activeDownloads;
    private final MutableLiveData<String> selectedReciter;
    private final MediatorLiveData<List<Recitation>> currentRecitations;
    private final LiveData<List<Surah>> allSurahs;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<Integer> downloadedCount;
    private final MutableLiveData<String> storageUsed;
    private final Handler progressUpdateHandler;
    private final Runnable progressUpdateRunnable;
    private final Context context;

    public DownloadManagerViewModel(QuranRepository repository) {
        super(repository);
        this.context = repository.getContext();
        this.recitationRepository = repository.getRecitationRepository();
        this.downloadManagerHelper = new DownloadManagerHelper(context);
        this.httpDownloader = new HttpDownloader();
        this.activeDownloads = new HashMap<>();
        this.selectedReciter = new MutableLiveData<>(Constants.RECITER_ALAFASY);
        this.currentRecitations = new MediatorLiveData<>();
        this.allSurahs = repository.getAllSurahs();
        this.errorMessage = new MutableLiveData<>();
        this.downloadedCount = new MutableLiveData<>(0);
        this.storageUsed = new MutableLiveData<>("0 B");
        this.progressUpdateHandler = new Handler(Looper.getMainLooper());

        // Update recitations when reciter selection changes
        currentRecitations.addSource(selectedReciter, reciter -> {
            LiveData<List<Recitation>> recitations = recitationRepository.getRecitationsByReciter(reciter);
            currentRecitations.addSource(recitations, currentRecitations::setValue);
        });

        // Start progress update polling
        progressUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateDownloadProgress();
                progressUpdateHandler.postDelayed(this, 500);
            }
        };
        progressUpdateHandler.post(progressUpdateRunnable);

        // Update downloaded count and storage when recitations change
        currentRecitations.observeForever(recitations -> {
            if (recitations != null) {
                updateDownloadedCount();
                updateStorageUsed();
            }
        });
    }

    public LiveData<String> getSelectedReciter() {
        return selectedReciter;
    }

    public LiveData<List<Recitation>> getCurrentRecitations() {
        return currentRecitations;
    }

    public LiveData<List<Surah>> getAllSurahs() {
        return allSurahs;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Integer> getDownloadedCount() {
        return downloadedCount;
    }

    public LiveData<String> getStorageUsed() {
        return storageUsed;
    }

    public void setSelectedReciter(String reciter) {
        selectedReciter.setValue(reciter);
    }

    /**
     * Start downloading a recitation using custom HTTP downloader.
     */
    public void downloadRecitation(Recitation recitation, Surah surah) {

        // Check storage space
        if (!StorageHelper.hasEnoughSpace(context, 10 * 1024 * 1024)) { // 10 MB minimum
            errorMessage.setValue("Insufficient storage space");
            return;
        }

        // Check if already downloading
        if (activeDownloads.containsKey(recitation.getId())) {
            return;
        }

        // Mark as downloading
        activeDownloads.put(recitation.getId(), true);
        recitationRepository.updateDownloadStatus(
                recitation.getId(),
                Constants.STATUS_DOWNLOADING,
                0
        );

        // Start download
        httpDownloader.download(
                recitation.getDownloadUrl(),
                recitation.getLocalFilePath(),
                new HttpDownloader.DownloadListener() {
                    @Override
                    public void onProgress(int progress, long bytesDownloaded, long totalBytes) {
                        recitationRepository.updateDownloadStatus(
                                recitation.getId(),
                                Constants.STATUS_DOWNLOADING,
                                progress
                        );
                    }

                    @Override
                    public void onSuccess(String filePath, long fileSize) {
                        activeDownloads.remove(recitation.getId());
                        // Update to 100% first, then mark as downloaded
                        recitationRepository.updateDownloadStatus(
                                recitation.getId(),
                                Constants.STATUS_DOWNLOADING,
                                100
                        );
                        // Small delay to ensure UI shows 100% before switching to downloaded state
                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                            recitationRepository.markAsDownloaded(recitation.getId(), filePath, fileSize);
                        }, 200); // 200ms delay
                    }

                    @Override
                    public void onError(String error) {
                        activeDownloads.remove(recitation.getId());
                        recitationRepository.markAsFailed(recitation.getId());
                        errorMessage.postValue("Download failed: " + error);
                    }
                }
        );
    }

    /**
     * Cancel a download.
     * Note: HttpDownloader downloads will complete in background but won't update database.
     */
    public void cancelDownload(Recitation recitation) {
        activeDownloads.remove(recitation.getId());
        recitationRepository.updateDownloadStatus(
                recitation.getId(),
                Constants.STATUS_NOT_DOWNLOADED,
                0
        );
    }

    /**
     * Delete a recitation.
     */
    public void deleteRecitation(Recitation recitation) {
        recitationRepository.deleteRecitation(recitation.getId(), () -> {
            updateDownloadedCount();
            updateStorageUsed();
        });
    }

    /**
     * Delete all recitations for the current reciter.
     */
    public void deleteAllRecitations() {
        String reciter = selectedReciter.getValue();
        if (reciter != null) {
            recitationRepository.deleteAllByReciter(reciter, () -> {
                updateDownloadedCount();
                updateStorageUsed();
            });
        }
    }

    /**
     * Update download progress for all downloading recitations.
     */
    private void updateDownloadProgress() {
        // Progress updates are now handled directly by HttpDownloader callbacks
        // This method is kept for compatibility but does nothing
    }

    /**
     * Update the downloaded count for the current reciter.
     */
    private void updateDownloadedCount() {
        String reciter = selectedReciter.getValue();
        if (reciter != null) {
            recitationRepository.getDownloadedCountByReciter(reciter, count -> {
                downloadedCount.postValue(count);
            });
        }
    }

    /**
     * Update the storage used for the current reciter.
     */
    private void updateStorageUsed() {
        String reciter = selectedReciter.getValue();
        if (reciter != null) {
            long size = StorageHelper.getReciterDownloadedSize(context, reciter);
            storageUsed.postValue(StorageHelper.formatFileSize(size));
        }
    }

    /**
     * Get display name for a reciter.
     */
    private String getReciterDisplayName(String reciterName) {
        if (reciterName.equals(Constants.RECITER_ALAFASY)) {
            return "Mishary Alafasy";
        } else if (reciterName.equals(Constants.RECITER_MINSHAWI)) {
            return "Mohamed Siddiq El-Minshawi";
        }
        return reciterName;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Stop progress updates
        progressUpdateHandler.removeCallbacks(progressUpdateRunnable);
        // Shutdown HTTP downloader
        httpDownloader.shutdown();
    }
}
