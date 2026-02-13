package com.example.quran.ui.surahlist;

import android.content.Context;

import com.example.quran.data.model.Surah;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class SurahAdapterTest {

    private Context context;
    private SurahAdapter adapter;
    private SurahAdapter.OnSurahClickListener mockListener;

    @Before
    public void setup() {
        context = RuntimeEnvironment.getApplication();
        mockListener = mock(SurahAdapter.OnSurahClickListener.class);
        adapter = new SurahAdapter(mockListener, 1.0f, context);
    }

    @Test
    public void testAdapterCreation() {
        assertNotNull(adapter);
    }

    @Test
    public void testInitialItemCount_isZero() {
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testSetSurahs_updatesItemCount() {
        List<Surah> surahs = Arrays.asList(
                createSurah(1, "Al-Fatihah", "الفاتحة", 7, "Meccan"),
                createSurah(2, "Al-Baqarah", "البقرة", 286, "Medinan")
        );

        adapter.setSurahs(surahs);

        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testSetSurahs_emptyList() {
        adapter.setSurahs(Arrays.asList());
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testSetSurahs_multipleCalls() {
        adapter.setSurahs(Arrays.asList(createSurah(1, "Al-Fatihah", "الفاتحة", 7, "Meccan")));
        assertEquals(1, adapter.getItemCount());

        adapter.setSurahs(Arrays.asList(
                createSurah(1, "Al-Fatihah", "الفاتحة", 7, "Meccan"),
                createSurah(2, "Al-Baqarah", "البقرة", 286, "Medinan"),
                createSurah(3, "Ali 'Imran", "آل عمران", 200, "Medinan")
        ));
        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void testDifferentFontSizes() {
        SurahAdapter smallAdapter = new SurahAdapter(mockListener, 0.8f, context);
        SurahAdapter largeAdapter = new SurahAdapter(mockListener, 1.4f, context);

        assertNotNull(smallAdapter);
        assertNotNull(largeAdapter);
    }

    @Test
    public void testLargeDataSet() {
        List<Surah> surahs = Arrays.asList(
                createSurah(1, "Al-Fatihah", "الفاتحة", 7, "Meccan"),
                createSurah(2, "Al-Baqarah", "البقرة", 286, "Medinan"),
                createSurah(3, "Ali 'Imran", "آل عمران", 200, "Medinan"),
                createSurah(4, "An-Nisa", "النساء", 176, "Medinan"),
                createSurah(5, "Al-Ma'idah", "المائدة", 120, "Medinan")
        );

        adapter.setSurahs(surahs);
        assertEquals(5, adapter.getItemCount());
    }

    @Test
    public void testNullListener() {
        SurahAdapter adapterWithNullListener = new SurahAdapter(null, 1.0f, context);
        assertNotNull(adapterWithNullListener);
        assertEquals(0, adapterWithNullListener.getItemCount());
    }

    @Test
    public void testMeccanSurahData() {
        Surah meccanSurah = createSurah(1, "Al-Fatihah", "الفاتحة", 7, "Meccan");
        adapter.setSurahs(Arrays.asList(meccanSurah));
        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void testMedinanSurahData() {
        Surah medinanSurah = createSurah(2, "Al-Baqarah", "البقرة", 286, "Medinan");
        adapter.setSurahs(Arrays.asList(medinanSurah));
        assertEquals(1, adapter.getItemCount());
    }

    private Surah createSurah(int number, String nameEn, String nameAr, int totalAyahs, String revelationType) {
        Surah surah = new Surah();
        surah.setNumber(number);
        surah.setNameEnglish(nameEn);
        surah.setNameArabic(nameAr);
        surah.setTotalAyahs(totalAyahs);
        surah.setRevelationType(revelationType);
        return surah;
    }
}
