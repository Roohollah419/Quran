package com.example.quran.utils;

public class Constants {

    // Database
    public static final String DATABASE_NAME = "quran_database";
    public static final int DATABASE_VERSION = 2;

    // Revelation Types
    public static final String MECCAN = "Meccan";
    public static final String MEDINAN = "Medinan";

    // Bundle Keys
    public static final String KEY_SURAH_NUMBER = "surah_number";
    public static final String KEY_SURAH_NAME = "surah_name";

    // Total Surahs in Quran
    public static final int TOTAL_SURAHS = 114;

    // Reciters
    public static final String RECITER_ALAFASY = "alafasy";
    public static final String RECITER_MINSHAWI = "minshawi";

    // Audio URLs (QuranicAudio.com CDN - Fast, reliable downloads)
    public static final String AUDIO_BASE_URL_ALAFASY = "https://download.quranicaudio.com/quran/mishaari_raashid_al_3afaasee/";
    public static final String AUDIO_BASE_URL_MINSHAWI = "https://download.quranicaudio.com/quran/muhammad_siddeeq_al-minshaawee/";

    // Download Status
    public static final String STATUS_NOT_DOWNLOADED = "NOT_DOWNLOADED";
    public static final String STATUS_DOWNLOADING = "DOWNLOADING";
    public static final String STATUS_DOWNLOADED = "DOWNLOADED";
    public static final String STATUS_FAILED = "FAILED";

    private Constants() {
        // Private constructor to prevent instantiation
    }
}
