package com.example.quran.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.quran.data.local.dao.AyahDao;
import com.example.quran.data.local.dao.SurahDao;
import com.example.quran.data.local.entity.AyahEntity;
import com.example.quran.data.local.entity.SurahEntity;
import com.example.quran.utils.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Room Database for Quran data.
 */
@Database(entities = {SurahEntity.class, AyahEntity.class}, version = Constants.DATABASE_VERSION, exportSchema = false)
public abstract class QuranDatabase extends RoomDatabase {

    private static volatile QuranDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract SurahDao surahDao();
    public abstract AyahDao ayahDao();

    public static QuranDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (QuranDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    QuranDatabase.class, Constants.DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
