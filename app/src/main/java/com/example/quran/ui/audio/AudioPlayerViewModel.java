package com.example.quran.ui.audio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quran.data.repository.QuranRepository;
import com.example.quran.service.AudioPlaybackService;
import com.example.quran.ui.base.BaseViewModel;

/**
 * Shared ViewModel for audio playback state.
 * Manages connection to AudioPlaybackService and provides playback controls.
 */
public class AudioPlayerViewModel extends BaseViewModel {

    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private final MutableLiveData<Long> currentPosition = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> duration = new MutableLiveData<>(0L);
    private final MutableLiveData<Integer> currentSurahNumber = new MutableLiveData<>(0);
    private final MutableLiveData<String> currentSurahName = new MutableLiveData<>("");
    private final MutableLiveData<String> currentReciterName = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> isServiceBound = new MutableLiveData<>(false);

    private AudioPlaybackService audioService;
    private boolean isBound = false;
    private final Context context;
    private final Handler progressUpdateHandler;
    private final Runnable progressUpdateRunnable;

    public AudioPlayerViewModel(QuranRepository repository) {
        super(repository);
        this.context = repository.getContext();
        this.progressUpdateHandler = new Handler(Looper.getMainLooper());

        // Update progress every 100ms when playing
        this.progressUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isBound && audioService != null && Boolean.TRUE.equals(isPlaying.getValue())) {
                    currentPosition.postValue(audioService.getCurrentPosition());
                    duration.postValue(audioService.getDuration());
                    progressUpdateHandler.postDelayed(this, 100);
                }
            }
        };
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioPlaybackService.LocalBinder binder = (AudioPlaybackService.LocalBinder) service;
            audioService = binder.getService();
            isBound = true;
            isServiceBound.postValue(true);

            // Set up listener for playback state changes
            audioService.setPlaybackStateListener(new AudioPlaybackService.PlaybackStateListener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    // Update UI based on state if needed
                }

                @Override
                public void onIsPlayingChanged(boolean playing) {
                    isPlaying.postValue(playing);
                    if (playing) {
                        startProgressUpdates();
                    } else {
                        stopProgressUpdates();
                    }
                }
            });

            // Initial state update
            isPlaying.postValue(audioService.isPlaying());
            currentPosition.postValue(audioService.getCurrentPosition());
            duration.postValue(audioService.getDuration());

            if (audioService.isPlaying()) {
                startProgressUpdates();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            audioService = null;
            isBound = false;
            isServiceBound.postValue(false);
            stopProgressUpdates();
        }
    };

    /**
     * Bind to the AudioPlaybackService.
     */
    public void bindService() {
        if (!isBound) {
            Intent intent = new Intent(context, AudioPlaybackService.class);
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * Unbind from the AudioPlaybackService.
     */
    public void unbindService() {
        if (isBound) {
            stopProgressUpdates();
            context.unbindService(serviceConnection);
            isBound = false;
            isServiceBound.postValue(false);
            audioService = null;
        }
    }

    /**
     * Play audio for a specific surah.
     */
    public void play(String filePath, int surahNumber, String surahName, String reciterName) {
        currentSurahNumber.setValue(surahNumber);
        currentSurahName.setValue(surahName);
        currentReciterName.setValue(reciterName);

        Intent intent = new Intent(context, AudioPlaybackService.class);
        intent.setAction(AudioPlaybackService.ACTION_PLAY);
        intent.putExtra(AudioPlaybackService.EXTRA_FILE_PATH, filePath);
        intent.putExtra(AudioPlaybackService.EXTRA_SURAH_NUMBER, surahNumber);
        intent.putExtra(AudioPlaybackService.EXTRA_SURAH_NAME, surahName);
        intent.putExtra(AudioPlaybackService.EXTRA_RECITER_NAME, reciterName);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

        // Bind to service if not already bound
        if (!isBound) {
            bindService();
        }
    }

    /**
     * Pause audio playback.
     */
    public void pause() {
        Intent intent = new Intent(context, AudioPlaybackService.class);
        intent.setAction(AudioPlaybackService.ACTION_PAUSE);
        context.startService(intent);
    }

    /**
     * Stop audio playback.
     */
    public void stop() {
        Intent intent = new Intent(context, AudioPlaybackService.class);
        intent.setAction(AudioPlaybackService.ACTION_STOP);
        context.startService(intent);

        // Reset state
        isPlaying.setValue(false);
        currentPosition.setValue(0L);
        duration.setValue(0L);
        stopProgressUpdates();
    }

    /**
     * Seek to a specific position.
     */
    public void seekTo(long position) {
        Intent intent = new Intent(context, AudioPlaybackService.class);
        intent.setAction(AudioPlaybackService.ACTION_SEEK);
        intent.putExtra(AudioPlaybackService.EXTRA_SEEK_POSITION, position);
        context.startService(intent);
    }

    /**
     * Toggle play/pause.
     */
    public void togglePlayPause() {
        if (Boolean.TRUE.equals(isPlaying.getValue())) {
            pause();
        } else {
            // Cannot resume without file path, so this is mainly for pause
            pause();
        }
    }

    private void startProgressUpdates() {
        progressUpdateHandler.removeCallbacks(progressUpdateRunnable);
        progressUpdateHandler.post(progressUpdateRunnable);
    }

    private void stopProgressUpdates() {
        progressUpdateHandler.removeCallbacks(progressUpdateRunnable);
    }

    // Getters for LiveData
    public LiveData<Boolean> getIsPlaying() {
        return isPlaying;
    }

    public LiveData<Long> getCurrentPosition() {
        return currentPosition;
    }

    public LiveData<Long> getDuration() {
        return duration;
    }

    public LiveData<Integer> getCurrentSurahNumber() {
        return currentSurahNumber;
    }

    public LiveData<String> getCurrentSurahName() {
        return currentSurahName;
    }

    public LiveData<String> getCurrentReciterName() {
        return currentReciterName;
    }

    public LiveData<Boolean> getIsServiceBound() {
        return isServiceBound;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        unbindService();
        stopProgressUpdates();
    }
}
