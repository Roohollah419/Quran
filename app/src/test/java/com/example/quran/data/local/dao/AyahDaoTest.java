package com.example.quran.data.local.dao;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;

import com.example.quran.data.local.QuranDatabase;
import com.example.quran.data.local.entity.AyahEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class AyahDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private QuranDatabase database;
    private AyahDao ayahDao;

    @Before
    public void setup() {
        Context context = RuntimeEnvironment.getApplication();
        database = Room.inMemoryDatabaseBuilder(context, QuranDatabase.class)
                .allowMainThreadQueries()
                .build();
        ayahDao = database.ayahDao();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testInsertAndGetAyah() {
        AyahEntity ayah = createTestAyah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah");
        ayahDao.insert(ayah);

        assertEquals(1, ayahDao.getAyahCount());
    }

    @Test
    public void testInsertAll() {
        List<AyahEntity> ayahs = Arrays.asList(
                createTestAyah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah"),
                createTestAyah(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Praise be to Allah"),
                createTestAyah(1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful")
        );

        ayahDao.insertAll(ayahs);

        assertEquals(3, ayahDao.getAyahCount());
    }

    @Test
    public void testGetAyahsBySurah() throws InterruptedException {
        List<AyahEntity> ayahs = Arrays.asList(
                createTestAyah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah"),
                createTestAyah(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Praise be to Allah"),
                createTestAyah(2, 1, "الم", "Alif Lam Meem")
        );
        ayahDao.insertAll(ayahs);

        List<AyahEntity> result = LiveDataTestUtil.getValue(ayahDao.getAyahsBySurah(1));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getAyahNumber());
        assertEquals(2, result.get(1).getAyahNumber());
    }

    @Test
    public void testGetAyahsBySurah_orderedByAyahNumber() throws InterruptedException {
        List<AyahEntity> ayahs = Arrays.asList(
                createTestAyah(1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful"),
                createTestAyah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah"),
                createTestAyah(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Praise be to Allah")
        );
        ayahDao.insertAll(ayahs);

        List<AyahEntity> result = LiveDataTestUtil.getValue(ayahDao.getAyahsBySurah(1));

        assertEquals(1, result.get(0).getAyahNumber());
        assertEquals(2, result.get(1).getAyahNumber());
        assertEquals(3, result.get(2).getAyahNumber());
    }

    @Test
    public void testGetAyahsBySurah_emptySurah() throws InterruptedException {
        List<AyahEntity> ayahs = Arrays.asList(
                createTestAyah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah")
        );
        ayahDao.insertAll(ayahs);

        List<AyahEntity> result = LiveDataTestUtil.getValue(ayahDao.getAyahsBySurah(2));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAyahById() throws InterruptedException {
        AyahEntity ayah = createTestAyah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah");
        ayahDao.insert(ayah);

        // Get the auto-generated ID
        List<AyahEntity> allAyahs = LiveDataTestUtil.getValue(ayahDao.getAyahsBySurah(1));
        int id = allAyahs.get(0).getId();

        AyahEntity result = LiveDataTestUtil.getValue(ayahDao.getAyahById(id));

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(1, result.getSurahNumber());
        assertEquals(1, result.getAyahNumber());
        assertEquals("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", result.getTextArabic());
        assertEquals("In the name of Allah", result.getTextTranslation());
    }

    @Test
    public void testGetAyahById_nonExistent() throws InterruptedException {
        AyahEntity result = LiveDataTestUtil.getValue(ayahDao.getAyahById(999));
        assertNull(result);
    }

    @Test
    public void testGetAyahCount_empty() {
        assertEquals(0, ayahDao.getAyahCount());
    }

    @Test
    public void testGetAyahCount_withData() {
        List<AyahEntity> ayahs = Arrays.asList(
                createTestAyah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah"),
                createTestAyah(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Praise be to Allah"),
                createTestAyah(1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful")
        );
        ayahDao.insertAll(ayahs);

        assertEquals(3, ayahDao.getAyahCount());
    }

    @Test
    public void testGetRandomAyah() throws InterruptedException {
        List<AyahEntity> ayahs = Arrays.asList(
                createTestAyah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah"),
                createTestAyah(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Praise be to Allah"),
                createTestAyah(1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful")
        );
        ayahDao.insertAll(ayahs);

        AyahEntity result = LiveDataTestUtil.getValue(ayahDao.getRandomAyah());

        assertNotNull(result);
        assertEquals(1, result.getSurahNumber());
        assertTrue(result.getAyahNumber() >= 1 && result.getAyahNumber() <= 3);
    }

    @Test
    public void testDeleteAll() {
        List<AyahEntity> ayahs = Arrays.asList(
                createTestAyah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah"),
                createTestAyah(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Praise be to Allah")
        );
        ayahDao.insertAll(ayahs);
        assertEquals(2, ayahDao.getAyahCount());

        ayahDao.deleteAll();

        assertEquals(0, ayahDao.getAyahCount());
    }

    @Test
    public void testInsertMultipleSurahs() throws InterruptedException {
        List<AyahEntity> ayahs = Arrays.asList(
                createTestAyah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah"),
                createTestAyah(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Praise be to Allah"),
                createTestAyah(2, 1, "الم", "Alif Lam Meem"),
                createTestAyah(2, 2, "ذَٰلِكَ الْكِتَابُ", "This is the Book"),
                createTestAyah(3, 1, "الم", "Alif Lam Meem")
        );
        ayahDao.insertAll(ayahs);

        List<AyahEntity> surah1 = LiveDataTestUtil.getValue(ayahDao.getAyahsBySurah(1));
        List<AyahEntity> surah2 = LiveDataTestUtil.getValue(ayahDao.getAyahsBySurah(2));
        List<AyahEntity> surah3 = LiveDataTestUtil.getValue(ayahDao.getAyahsBySurah(3));

        assertEquals(2, surah1.size());
        assertEquals(2, surah2.size());
        assertEquals(1, surah3.size());
    }

    private AyahEntity createTestAyah(int surahNumber, int ayahNumber, String textArabic, String textTranslation) {
        return new AyahEntity(surahNumber, ayahNumber, textArabic, textTranslation);
    }
}
