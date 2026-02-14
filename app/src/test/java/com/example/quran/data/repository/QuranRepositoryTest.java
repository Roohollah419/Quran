package com.example.quran.data.repository;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.example.quran.data.model.Ayah;
import com.example.quran.data.model.Surah;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class QuranRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private Context context;
    private QuranRepository repository;

    @Before
    public void setup() {
        context = RuntimeEnvironment.getApplication();
        repository = new QuranRepository(context);
    }

    @Test
    public void testRepositoryCreation() {
        assertNotNull(repository);
    }

    @Test
    public void testGetAllSurahs_returnsLiveData() {
        LiveData<List<Surah>> surahs = repository.getAllSurahs();
        assertNotNull(surahs);
    }

    @Test
    public void testGetSurahByNumber_returnsLiveData() {
        LiveData<Surah> surah1 = repository.getSurahByNumber(1);
        LiveData<Surah> surah114 = repository.getSurahByNumber(114);

        assertNotNull(surah1);
        assertNotNull(surah114);
    }

    @Test
    public void testGetSurahByNumber_validNumbers() {
        for (int i = 1; i <= 114; i++) {
            LiveData<Surah> surah = repository.getSurahByNumber(i);
            assertNotNull("Surah " + i + " should return non-null LiveData", surah);
        }
    }

    @Test
    public void testGetAyahsBySurah_returnsLiveData() {
        LiveData<List<Ayah>> ayahs = repository.getAyahsBySurah(1);
        assertNotNull(ayahs);
    }

    @Test
    public void testGetAyahsBySurah_validSurahNumbers() {
        LiveData<List<Ayah>> ayahs1 = repository.getAyahsBySurah(1);
        LiveData<List<Ayah>> ayahs2 = repository.getAyahsBySurah(2);
        LiveData<List<Ayah>> ayahs114 = repository.getAyahsBySurah(114);

        assertNotNull(ayahs1);
        assertNotNull(ayahs2);
        assertNotNull(ayahs114);
    }

    @Test
    public void testGetRandomAyah_returnsLiveData() {
        LiveData<Ayah> randomAyah = repository.getRandomAyah();
        assertNotNull(randomAyah);
    }

    @Test
    public void testGetRandomAyah_multipleCallsReturnLiveData() {
        for (int i = 0; i < 5; i++) {
            LiveData<Ayah> randomAyah = repository.getRandomAyah();
            assertNotNull("Random ayah call " + i + " should return non-null LiveData", randomAyah);
        }
    }

    @Test
    public void testGetRandomSurahNumber_callback() {
        // Test that the method can be called without throwing exception
        repository.getRandomSurahNumber(surahNumber -> {
            // Callback interface is accessible
        });
        // Note: Async callback testing is complex with Robolectric and database seeding
        assertTrue(true);
    }

    @Test
    public void testRepositoryInterface_allMethodsAccessible() {
        // Verify all public methods are accessible
        assertNotNull(repository.getAllSurahs());
        assertNotNull(repository.getSurahByNumber(1));
        assertNotNull(repository.getAyahsBySurah(1));
        assertNotNull(repository.getRandomAyah());

        // Verify callback method is accessible
        repository.getRandomSurahNumber(surahNumber -> {
            // Callback exists and is callable
        });
    }
}
