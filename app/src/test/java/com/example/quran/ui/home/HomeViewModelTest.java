package com.example.quran.ui.home;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.quran.data.model.Ayah;
import com.example.quran.data.model.Surah;
import com.example.quran.data.repository.QuranRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;

public class HomeViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private QuranRepository repository;

    private HomeViewModel viewModel;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Mock repository responses
        MutableLiveData<Ayah> ayahLiveData = new MutableLiveData<>(createTestAyah());
        when(repository.getRandomAyah()).thenReturn(ayahLiveData);

        MutableLiveData<Surah> surahLiveData = new MutableLiveData<>(createTestSurah());
        when(repository.getSurahByNumber(1)).thenReturn(surahLiveData);

        viewModel = new HomeViewModel(repository);
    }

    @Test
    public void testGetRandomAyah() {
        assertNotNull(viewModel.getRandomAyah());
        Ayah ayah = viewModel.getRandomAyah().getValue();
        assertNotNull(ayah);
        assertEquals(1, ayah.getSurahNumber());
        assertEquals(1, ayah.getAyahNumber());
    }

    @Test
    public void testGetSurahByNumber() {
        assertNotNull(viewModel.getSurahByNumber(1));
        Surah surah = viewModel.getSurahByNumber(1).getValue();
        assertNotNull(surah);
        assertEquals(1, surah.getNumber());
        assertEquals("Al-Fatihah", surah.getNameEnglish());
    }

    @Test
    public void testGetRandomSurahNumber() {
        // Just verify the method doesn't throw an exception
        viewModel.getRandomSurahNumber(number -> {
            // Verify callback receives a valid surah number (1-114)
            assertTrue(number >= 1 && number <= 114);
        });

        // Give time for async operation
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    @Test
    public void testGetRandomAyahReturnsLiveData() {
        // Just verify the method returns data
        assertNotNull(viewModel.getRandomAyah());
        verify(repository).getRandomAyah();
    }

    private Ayah createTestAyah() {
        Ayah ayah = new Ayah();
        ayah.setSurahNumber(1);
        ayah.setAyahNumber(1);
        ayah.setTextArabic("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ");
        ayah.setTextTranslation("In the name of Allah, the Entirely Merciful, the Especially Merciful.");
        return ayah;
    }

    private Surah createTestSurah() {
        Surah surah = new Surah();
        surah.setNumber(1);
        surah.setNameEnglish("Al-Fatihah");
        surah.setNameArabic("الفاتحة");
        surah.setTotalAyahs(7);
        surah.setRevelationType("Meccan");
        return surah;
    }
}
