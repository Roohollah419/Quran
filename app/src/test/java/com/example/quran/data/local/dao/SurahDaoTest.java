package com.example.quran.data.local.dao;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;

import com.example.quran.data.local.QuranDatabase;
import com.example.quran.data.local.entity.SurahEntity;

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
public class SurahDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private QuranDatabase database;
    private SurahDao surahDao;

    @Before
    public void setup() {
        Context context = RuntimeEnvironment.getApplication();
        database = Room.inMemoryDatabaseBuilder(context, QuranDatabase.class)
                .allowMainThreadQueries()
                .build();
        surahDao = database.surahDao();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testInsertAndGetSurah() {
        SurahEntity surah = createTestSurah(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "Meccan");
        surahDao.insert(surah);

        assertEquals(1, surahDao.getSurahCount());
    }

    @Test
    public void testInsertAll() {
        List<SurahEntity> surahs = Arrays.asList(
                createTestSurah(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "Meccan"),
                createTestSurah(2, "البقرة", "Al-Baqarah", "The Cow", 286, "Medinan"),
                createTestSurah(3, "آل عمران", "Ali 'Imran", "Family of Imran", 200, "Medinan")
        );

        surahDao.insertAll(surahs);

        assertEquals(3, surahDao.getSurahCount());
    }

    @Test
    public void testGetAllSurahs() throws InterruptedException {
        List<SurahEntity> surahs = Arrays.asList(
                createTestSurah(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "Meccan"),
                createTestSurah(2, "البقرة", "Al-Baqarah", "The Cow", 286, "Medinan")
        );
        surahDao.insertAll(surahs);

        List<SurahEntity> result = LiveDataTestUtil.getValue(surahDao.getAllSurahs());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getNumber());
        assertEquals(2, result.get(1).getNumber());
    }

    @Test
    public void testGetSurahByNumber() throws InterruptedException {
        SurahEntity surah = createTestSurah(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "Meccan");
        surahDao.insert(surah);

        SurahEntity result = LiveDataTestUtil.getValue(surahDao.getSurahByNumber(1));

        assertNotNull(result);
        assertEquals(1, result.getNumber());
        assertEquals("الفاتحة", result.getNameArabic());
        assertEquals("Al-Fatihah", result.getNameEnglish());
        assertEquals("The Opening", result.getTranslation());
        assertEquals(7, result.getTotalAyahs());
        assertEquals("Meccan", result.getRevelationType());
    }

    @Test
    public void testGetSurahByNumber_nonExistent() throws InterruptedException {
        SurahEntity result = LiveDataTestUtil.getValue(surahDao.getSurahByNumber(999));
        assertNull(result);
    }

    @Test
    public void testGetSurahCount_empty() {
        assertEquals(0, surahDao.getSurahCount());
    }

    @Test
    public void testGetSurahCount_withData() {
        List<SurahEntity> surahs = Arrays.asList(
                createTestSurah(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "Meccan"),
                createTestSurah(2, "البقرة", "Al-Baqarah", "The Cow", 286, "Medinan"),
                createTestSurah(3, "آل عمران", "Ali 'Imran", "Family of Imran", 200, "Medinan")
        );
        surahDao.insertAll(surahs);

        assertEquals(3, surahDao.getSurahCount());
    }

    @Test
    public void testGetRandomSurahNumber() {
        List<SurahEntity> surahs = Arrays.asList(
                createTestSurah(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "Meccan"),
                createTestSurah(2, "البقرة", "Al-Baqarah", "The Cow", 286, "Medinan"),
                createTestSurah(3, "آل عمران", "Ali 'Imran", "Family of Imran", 200, "Medinan")
        );
        surahDao.insertAll(surahs);

        int randomNumber = surahDao.getRandomSurahNumber();

        assertTrue(randomNumber >= 1 && randomNumber <= 3);
    }

    @Test
    public void testDeleteAll() {
        List<SurahEntity> surahs = Arrays.asList(
                createTestSurah(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "Meccan"),
                createTestSurah(2, "البقرة", "Al-Baqarah", "The Cow", 286, "Medinan")
        );
        surahDao.insertAll(surahs);
        assertEquals(2, surahDao.getSurahCount());

        surahDao.deleteAll();

        assertEquals(0, surahDao.getSurahCount());
    }

    @Test
    public void testInsertWithConflict_shouldReplace() {
        SurahEntity surah1 = createTestSurah(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "Meccan");
        surahDao.insert(surah1);

        SurahEntity surah2 = createTestSurah(1, "Updated", "Updated", "Updated", 10, "Updated");
        surahDao.insert(surah2);

        assertEquals(1, surahDao.getSurahCount());
    }

    @Test
    public void testGetAllSurahs_orderedByNumber() throws InterruptedException {
        List<SurahEntity> surahs = Arrays.asList(
                createTestSurah(3, "آل عمران", "Ali 'Imran", "Family of Imran", 200, "Medinan"),
                createTestSurah(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "Meccan"),
                createTestSurah(2, "البقرة", "Al-Baqarah", "The Cow", 286, "Medinan")
        );
        surahDao.insertAll(surahs);

        List<SurahEntity> result = LiveDataTestUtil.getValue(surahDao.getAllSurahs());

        assertEquals(1, result.get(0).getNumber());
        assertEquals(2, result.get(1).getNumber());
        assertEquals(3, result.get(2).getNumber());
    }

    private SurahEntity createTestSurah(int number, String nameArabic, String nameEnglish,
                                       String translation, int totalAyahs, String revelationType) {
        return new SurahEntity(number, nameArabic, nameEnglish, translation, totalAyahs, revelationType);
    }
}
