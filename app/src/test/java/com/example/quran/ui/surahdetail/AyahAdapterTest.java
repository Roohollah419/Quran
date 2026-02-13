package com.example.quran.ui.surahdetail;

import android.content.Context;

import com.example.quran.data.model.Ayah;

import org.junit.Before;
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
public class AyahAdapterTest {

    private Context context;
    private AyahAdapter adapter;

    @Before
    public void setup() {
        context = RuntimeEnvironment.getApplication();
        adapter = new AyahAdapter(1.0f, context);
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
    public void testSetAyahs_updatesItemCount() {
        List<Ayah> ayahs = Arrays.asList(
                createAyah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah"),
                createAyah(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Praise be to Allah")
        );

        adapter.setAyahs(ayahs);

        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testSetAyahs_emptyList() {
        adapter.setAyahs(Arrays.asList());
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testSetAyahs_multipleCalls() {
        adapter.setAyahs(Arrays.asList(
                createAyah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah")
        ));
        assertEquals(1, adapter.getItemCount());

        adapter.setAyahs(Arrays.asList(
                createAyah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah"),
                createAyah(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Praise be to Allah"),
                createAyah(1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful")
        ));
        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void testDifferentFontSizes() {
        AyahAdapter smallAdapter = new AyahAdapter(0.8f, context);
        AyahAdapter mediumAdapter = new AyahAdapter(1.0f, context);
        AyahAdapter largeAdapter = new AyahAdapter(1.2f, context);
        AyahAdapter extraLargeAdapter = new AyahAdapter(1.4f, context);

        assertNotNull(smallAdapter);
        assertNotNull(mediumAdapter);
        assertNotNull(largeAdapter);
        assertNotNull(extraLargeAdapter);
    }

    @Test
    public void testSingleAyah() {
        Ayah ayah = createAyah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah");
        adapter.setAyahs(Arrays.asList(ayah));

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void testMultipleSurahs() {
        List<Ayah> ayahs = Arrays.asList(
                createAyah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah"),
                createAyah(2, 1, "الم", "Alif Lam Meem"),
                createAyah(3, 1, "الم", "Alif Lam Meem")
        );
        adapter.setAyahs(ayahs);

        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void testLongAyahText() {
        Ayah longAyah = createAyah(2, 282,
                "يَا أَيُّهَا الَّذِينَ آمَنُوا إِذَا تَدَايَنتُم بِدَيْنٍ إِلَىٰ أَجَلٍ مُّسَمًّى فَاكْتُبُوهُ",
                "O you who have believed, when you contract a debt for a specified term, write it down.");
        adapter.setAyahs(Arrays.asList(longAyah));

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void testSequentialAyahNumbers() {
        List<Ayah> ayahs = Arrays.asList(
                createAyah(1, 1, "Text 1", "Translation 1"),
                createAyah(1, 2, "Text 2", "Translation 2"),
                createAyah(1, 3, "Text 3", "Translation 3"),
                createAyah(1, 4, "Text 4", "Translation 4"),
                createAyah(1, 5, "Text 5", "Translation 5")
        );
        adapter.setAyahs(ayahs);

        assertEquals(5, adapter.getItemCount());
    }

    @Test
    public void testEmptyTextFields() {
        Ayah emptyAyah = createAyah(1, 1, "", "");
        adapter.setAyahs(Arrays.asList(emptyAyah));

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void testLargeDataSet() {
        List<Ayah> ayahs = Arrays.asList(
                createAyah(2, 1, "Text 1", "Translation 1"),
                createAyah(2, 2, "Text 2", "Translation 2"),
                createAyah(2, 3, "Text 3", "Translation 3"),
                createAyah(2, 4, "Text 4", "Translation 4"),
                createAyah(2, 5, "Text 5", "Translation 5"),
                createAyah(2, 6, "Text 6", "Translation 6"),
                createAyah(2, 7, "Text 7", "Translation 7"),
                createAyah(2, 8, "Text 8", "Translation 8"),
                createAyah(2, 9, "Text 9", "Translation 9"),
                createAyah(2, 10, "Text 10", "Translation 10")
        );
        adapter.setAyahs(ayahs);

        assertEquals(10, adapter.getItemCount());
    }

    private Ayah createAyah(int surahNumber, int ayahNumber, String textArabic, String textTranslation) {
        Ayah ayah = new Ayah();
        ayah.setSurahNumber(surahNumber);
        ayah.setAyahNumber(ayahNumber);
        ayah.setTextArabic(textArabic);
        ayah.setTextTranslation(textTranslation);
        return ayah;
    }
}
