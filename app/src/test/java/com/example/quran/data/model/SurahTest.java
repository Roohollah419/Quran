package com.example.quran.data.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SurahTest {

    private Surah surah;

    @Before
    public void setup() {
        surah = new Surah();
    }

    @Test
    public void testSurahCreation() {
        assertNotNull(surah);
    }

    @Test
    public void testSetAndGetNumber() {
        surah.setNumber(1);
        assertEquals(1, surah.getNumber());
    }

    @Test
    public void testSetAndGetNameEnglish() {
        surah.setNameEnglish("Al-Fatihah");
        assertEquals("Al-Fatihah", surah.getNameEnglish());
    }

    @Test
    public void testSetAndGetNameArabic() {
        surah.setNameArabic("الفاتحة");
        assertEquals("الفاتحة", surah.getNameArabic());
    }

    @Test
    public void testSetAndGetTotalAyahs() {
        surah.setTotalAyahs(7);
        assertEquals(7, surah.getTotalAyahs());
    }

    @Test
    public void testSetAndGetRevelationType() {
        surah.setRevelationType("Meccan");
        assertEquals("Meccan", surah.getRevelationType());
    }

    @Test
    public void testSetAndGetTranslation() {
        surah.setTranslation("The Opening");
        assertEquals("The Opening", surah.getTranslation());
    }

    @Test
    public void testCompleteSurahData() {
        surah.setNumber(1);
        surah.setNameEnglish("Al-Fatihah");
        surah.setNameArabic("الفاتحة");
        surah.setTranslation("The Opening");
        surah.setTotalAyahs(7);
        surah.setRevelationType("Meccan");

        assertEquals(1, surah.getNumber());
        assertEquals("Al-Fatihah", surah.getNameEnglish());
        assertEquals("الفاتحة", surah.getNameArabic());
        assertEquals("The Opening", surah.getTranslation());
        assertEquals(7, surah.getTotalAyahs());
        assertEquals("Meccan", surah.getRevelationType());
    }
}
