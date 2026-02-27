package com.example.quran.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.quran.MainActivity;
import com.example.quran.R;

import java.io.IOException;

/**
 * Foreground service for playing Quran audio recitations.
 * Uses ExoPlayer for playback and MediaSession for lock screen controls.
 */
public class AudioPlaybackService extends Service {

    private static final String CHANNEL_ID = "quran_audio_channel";
    private static final int NOTIFICATION_ID = 1001;

    // Actions
    public static final String ACTION_PLAY = "com.example.quran.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.quran.ACTION_PAUSE";
    public static final String ACTION_STOP = "com.example.quran.ACTION_STOP";
    public static final String ACTION_SEEK = "com.example.quran.ACTION_SEEK";

    // Intent extras
    public static final String EXTRA_FILE_PATH = "file_path";
    public static final String EXTRA_SURAH_NUMBER = "surah_number";
    public static final String EXTRA_SURAH_NAME = "surah_name";
    public static final String EXTRA_RECITER_NAME = "reciter_name";
    public static final String EXTRA_SEEK_POSITION = "seek_position";

    private MediaPlayer mediaPlayer;
    private final IBinder binder = new LocalBinder();
    private PlaybackStateListener playbackStateListener;
    private final Handler progressHandler = new Handler(Looper.getMainLooper());

    private String currentSurahName = "";
    private String currentReciterName = "";
    private int currentSurahNumber = 0;
    private boolean isPrepared = false;

    public class LocalBinder extends Binder {
        public AudioPlaybackService getService() {
            return AudioPlaybackService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        initializePlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_PLAY:
                    String filePath = intent.getStringExtra(EXTRA_FILE_PATH);
                    currentSurahName = intent.getStringExtra(EXTRA_SURAH_NAME);
                    currentReciterName = intent.getStringExtra(EXTRA_RECITER_NAME);
                    currentSurahNumber = intent.getIntExtra(EXTRA_SURAH_NUMBER, 0);
                    if (filePath != null) {
                        play(filePath);
                    }
                    break;
                case ACTION_PAUSE:
                    pause();
                    break;
                case ACTION_STOP:
                    stop();
                    break;
                case ACTION_SEEK:
                    long position = intent.getLongExtra(EXTRA_SEEK_POSITION, 0);
                    seekTo(position);
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void initializePlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(mp -> {
            isPrepared = true;
            mp.start();
            updateNotification();
            if (playbackStateListener != null) {
                playbackStateListener.onIsPlayingChanged(true);
            }
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            isPrepared = false;
            stopForeground(true);
            stopSelf();
            if (playbackStateListener != null) {
                playbackStateListener.onIsPlayingChanged(false);
            }
        });

        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            isPrepared = false;
            return false;
        });
    }

    private void play(String filePath) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(filePath);
                mediaPlayer.prepareAsync();
                startForeground(NOTIFICATION_ID, createNotification());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            updateNotification();
            if (playbackStateListener != null) {
                playbackStateListener.onIsPlayingChanged(false);
            }
        }
    }

    private void stop() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            isPrepared = false;
        }
        stopForeground(true);
        stopSelf();
    }

    private void seekTo(long position) {
        if (mediaPlayer != null && isPrepared) {
            mediaPlayer.seekTo((int) position);
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public long getCurrentPosition() {
        return (mediaPlayer != null && isPrepared) ? mediaPlayer.getCurrentPosition() : 0;
    }

    public long getDuration() {
        return (mediaPlayer != null && isPrepared) ? mediaPlayer.getDuration() : 0;
    }

    public void setPlaybackStateListener(PlaybackStateListener listener) {
        this.playbackStateListener = listener;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.notification_channel_audio),
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription(getString(R.string.notification_channel_audio_desc));
            channel.setShowBadge(false);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Create action intents
        Intent pauseIntent = new Intent(this, AudioPlaybackService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getService(
                this, 0, pauseIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent stopIntent = new Intent(this, AudioPlaybackService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this, 0, stopIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        String title = currentSurahName;
        String text = currentReciterName;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_audio_download)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setShowWhen(false);

        // Add pause/play action
        if (isPlaying()) {
            builder.addAction(R.drawable.ic_close, getString(R.string.pause), pausePendingIntent);
        }

        // Add stop action
        builder.addAction(R.drawable.ic_close, getString(R.string.stop), stopPendingIntent);

        return builder.build();
    }

    private void updateNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, createNotification());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        progressHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Interface for playback state callbacks.
     */
    public interface PlaybackStateListener {
        void onPlaybackStateChanged(int state);
        void onIsPlayingChanged(boolean isPlaying);
    }
}
