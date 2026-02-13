package com.example.quran.data.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AyahTest {

    private Ayah ayah;

    @Before
    public void setup() {
        ayah = new Ayah();
    }

    @Test
    public void testAyahCreation() {
        assertNotNull(ayah);
    }

    @Test
    public void testSetAndGetSurahNumber() {
        ayah.setSurahNumber(1);
        assertEquals(1, ayah.getSurahNumber());
    }

    @Test
    public void testSetAndGetAyahNumber() {
        ayah.setAyahNumber(1);
        assertEquals(1, ayah.getAyahNumber());
    }

    @Test
    public void testSetAndGetTextArabic() {
        String arabicText = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ";
        ayah.setTextArabic(arabicText);
        assertEquals(arabicText, ayah.getTextArabic());
    }

    @Test
    public void testSetAndGetTextTranslation() {
        String translation = "In the name of Allah";
        ayah.setTextTranslation(translation);
        assertEquals(translation, ayah.getTextTranslation());
    }

    @Test
    public void testSetAndGetId() {
        ayah.setId(100);
        assertEquals(100, ayah.getId());
    }

    @Test
    public void testCompleteAyahData() {
        ayah.setId(100);
        ayah.setSurahNumber(1);
        ayah.setAyahNumber(1);
        ayah.setTextArabic("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ");
        ayah.setTextTranslation("In the name of Allah, the Entirely Merciful, the Especially Merciful.");

        assertEquals(100, ayah.getId());
        assertEquals(1, ayah.getSurahNumber());
        assertEquals(1, ayah.getAyahNumber());
        assertEquals("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", ayah.getTextArabic());
        assertEquals("In the name of Allah, the Entirely Merciful, the Especially Merciful.", ayah.getTextTranslation());
    }
}
