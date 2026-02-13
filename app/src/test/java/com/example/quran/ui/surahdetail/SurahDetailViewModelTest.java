package com.example.quran.ui.surahdetail;

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

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SurahDetailViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private QuranRepository repository;

    private SurahDetailViewModel viewModel;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        viewModel = new SurahDetailViewModel(repository);
    }

    @Test
    public void testLoadSurah_validNumber_loadsSurahAndAyahs() {
        // Arrange
        Surah testSurah = createTestSurah(1);
        List<Ayah> testAyahs = createTestAyahs(1, 7);

        MutableLiveData<Surah> surahLiveData = new MutableLiveData<>(testSurah);
        MutableLiveData<List<Ayah>> ayahsLiveData = new MutableLiveData<>(testAyahs);

        when(repository.getSurahByNumber(1)).thenReturn(surahLiveData);
        when(repository.getAyahsBySurah(1)).thenReturn(ayahsLiveData);

        // Act
        viewModel.loadSurah(1);

        // Assert
        assertNotNull(viewModel.getSurah());
        assertNotNull(viewModel.getAyahs());
        assertEquals(testSurah, viewModel.getSurah().getValue());
        assertEquals(testAyahs, viewModel.getAyahs().getValue());
        verify(repository).getSurahByNumber(1);
        verify(repository).getAyahsBySurah(1);
    }

    @Test
    public void testLoadSurah_surah114_loadsCorrectly() {
        // Arrange
        Surah testSurah = createTestSurah(114);
        List<Ayah> testAyahs = createTestAyahs(114, 6);

        MutableLiveData<Surah> surahLiveData = new MutableLiveData<>(testSurah);
        MutableLiveData<List<Ayah>> ayahsLiveData = new MutableLiveData<>(testAyahs);

        when(repository.getSurahByNumber(114)).thenReturn(surahLiveData);
        when(repository.getAyahsBySurah(114)).thenReturn(ayahsLiveData);

        // Act
        viewModel.loadSurah(114);

        // Assert
        assertNotNull(viewModel.getSurah().getValue());
        assertEquals(114, viewModel.getSurah().getValue().getNumber());
        verify(repository).getSurahByNumber(114);
    }

    @Test
    public void testGetSurah_beforeLoad_returnsNull() {
        assertNull(viewModel.getSurah());
    }

    @Test
    public void testGetAyahs_beforeLoad_returnsNull() {
        assertNull(viewModel.getAyahs());
    }

    @Test
    public void testLoadSurah_multipleNumbers_loadsEachCorrectly() {
        // Test loading different surahs
        for (int i = 1; i <= 5; i++) {
            Surah testSurah = createTestSurah(i);
            List<Ayah> testAyahs = createTestAyahs(i, 5);

            MutableLiveData<Surah> surahLiveData = new MutableLiveData<>(testSurah);
            MutableLiveData<List<Ayah>> ayahsLiveData = new MutableLiveData<>(testAyahs);

            when(repository.getSurahByNumber(i)).thenReturn(surahLiveData);
            when(repository.getAyahsBySurah(i)).thenReturn(ayahsLiveData);

            viewModel.loadSurah(i);

            assertEquals(i, viewModel.getSurah().getValue().getNumber());
        }
    }

    private Surah createTestSurah(int number) {
        Surah surah = new Surah();
        surah.setNumber(number);
        surah.setNameEnglish("Test Surah " + number);
        surah.setNameArabic("سورة " + number);
        surah.setTotalAyahs(10);
        surah.setRevelationType("Meccan");
        return surah;
    }

    private List<Ayah> createTestAyahs(int surahNumber, int count) {
        Ayah[] ayahs = new Ayah[count];
        for (int i = 0; i < count; i++) {
            Ayah ayah = new Ayah();
            ayah.setSurahNumber(surahNumber);
            ayah.setAyahNumber(i + 1);
            ayah.setTextArabic("نص عربي " + (i + 1));
            ayah.setTextTranslation("English text " + (i + 1));
            ayahs[i] = ayah;
        }
        return Arrays.asList(ayahs);
    }
}
