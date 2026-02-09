package com.example.quran.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.quran.data.local.entity.AyahEntity;

import java.util.List;

/**
 * Data Access Object for Ayah operations.
 */
@Dao
public interface AyahDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AyahEntity ayah);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AyahEntity> ayahs);

    @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    LiveData<List<AyahEntity>> getAyahsBySurah(int surahNumber);

    @Query("SELECT * FROM ayahs WHERE id = :ayahId")
    LiveData<AyahEntity> getAyahById(int ayahId);

    @Query("SELECT COUNT(*) FROM ayahs")
    int getAyahCount();

    @Query("SELECT * FROM ayahs ORDER BY RANDOM() LIMIT 1")
    LiveData<AyahEntity> getRandomAyah();

    @Query("DELETE FROM ayahs")
    void deleteAll();
}
