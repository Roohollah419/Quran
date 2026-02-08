package com.example.quran.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.quran.data.local.entity.SurahEntity;

import java.util.List;

/**
 * Data Access Object for Surah operations.
 */
@Dao
public interface SurahDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SurahEntity surah);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<SurahEntity> surahs);

    @Query("SELECT * FROM surahs ORDER BY number ASC")
    LiveData<List<SurahEntity>> getAllSurahs();

    @Query("SELECT * FROM surahs WHERE number = :surahNumber")
    LiveData<SurahEntity> getSurahByNumber(int surahNumber);

    @Query("SELECT COUNT(*) FROM surahs")
    int getSurahCount();

    @Query("DELETE FROM surahs")
    void deleteAll();
}
