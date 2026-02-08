package com.example.quran.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.quran.data.local.QuranDatabase;
import com.example.quran.data.local.dao.AyahDao;
import com.example.quran.data.local.dao.SurahDao;
import com.example.quran.data.local.entity.AyahEntity;
import com.example.quran.data.local.entity.SurahEntity;
import com.example.quran.data.model.Ayah;
import com.example.quran.data.model.Surah;
import com.example.quran.utils.Constants;
import com.example.quran.utils.QuranJsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing Quran data from local database.
 * Provides a clean API for data access to ViewModels.
 */
public class QuranRepository {

    private final Context context;
    private final SurahDao surahDao;
    private final AyahDao ayahDao;
    private final LiveData<List<SurahEntity>> allSurahs;

    public QuranRepository(Context context) {
        this.context = context;
        QuranDatabase database = QuranDatabase.getInstance(context);
        surahDao = database.surahDao();
        ayahDao = database.ayahDao();
        allSurahs = surahDao.getAllSurahs();

        // Seed database if empty
        seedDatabaseIfEmpty();
    }

    /**
     * Get all Surahs from database.
     */
    public LiveData<List<Surah>> getAllSurahs() {
        return Transformations.map(allSurahs, this::convertSurahEntitiesToModels);
    }

    /**
     * Get a specific Surah by number.
     */
    public LiveData<Surah> getSurahByNumber(int surahNumber) {
        return Transformations.map(surahDao.getSurahByNumber(surahNumber), this::convertSurahEntityToModel);
    }

    /**
     * Get all Ayahs for a specific Surah.
     */
    public LiveData<List<Ayah>> getAyahsBySurah(int surahNumber) {
        return Transformations.map(ayahDao.getAyahsBySurah(surahNumber), this::convertAyahEntitiesToModels);
    }

    /**
     * Seed database with sample data if it's empty.
     */
    private void seedDatabaseIfEmpty() {
        QuranDatabase.databaseWriteExecutor.execute(() -> {
            int count = surahDao.getSurahCount();
            if (count == 0) {
                insertSampleData();
            }
        });
    }

    /**
     * Load complete Quran data from JSON file and insert into database.
     */
    private void insertSampleData() {
        // Parse JSON file from assets
        QuranJsonParser.QuranData data = QuranJsonParser.parseAndConvert(context);

        // Insert all Surahs and Ayahs
        surahDao.insertAll(data.surahs);
        ayahDao.insertAll(data.ayahs);
    }

    /**
     * Convert list of SurahEntity to list of Surah models.
     */
    private List<Surah> convertSurahEntitiesToModels(List<SurahEntity> entities) {
        List<Surah> surahs = new ArrayList<>();
        for (SurahEntity entity : entities) {
            surahs.add(convertSurahEntityToModel(entity));
        }
        return surahs;
    }

    /**
     * Convert SurahEntity to Surah model.
     */
    private Surah convertSurahEntityToModel(SurahEntity entity) {
        if (entity == null) return null;
        return new Surah(
                entity.getNumber(),
                entity.getNameArabic(),
                entity.getNameEnglish(),
                entity.getTranslation(),
                entity.getTotalAyahs(),
                entity.getRevelationType()
        );
    }

    /**
     * Convert list of AyahEntity to list of Ayah models.
     */
    private List<Ayah> convertAyahEntitiesToModels(List<AyahEntity> entities) {
        List<Ayah> ayahs = new ArrayList<>();
        for (AyahEntity entity : entities) {
            ayahs.add(convertAyahEntityToModel(entity));
        }
        return ayahs;
    }

    /**
     * Convert AyahEntity to Ayah model.
     */
    private Ayah convertAyahEntityToModel(AyahEntity entity) {
        if (entity == null) return null;
        return new Ayah(
                entity.getId(),
                entity.getSurahNumber(),
                entity.getAyahNumber(),
                entity.getTextArabic(),
                entity.getTextTranslation()
        );
    }
}
