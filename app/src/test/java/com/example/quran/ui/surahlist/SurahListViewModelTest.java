package com.example.quran.ui.surahlist;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

public class SurahListViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private QuranRepository repository;

    private SurahListViewModel viewModel;
    private MutableLiveData<List<Surah>> surahsLiveData;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Create test data
        List<Surah> testSurahs = Arrays.asList(
            createSurah(1, "Al-Fatihah", "الفاتحة", 7, "Meccan"),
            createSurah(2, "Al-Baqarah", "البقرة", 286, "Medinan"),
            createSurah(3, "Ali 'Imran", "آل عمران", 200, "Medinan")
        );

        surahsLiveData = new MutableLiveData<>(testSurahs);
        when(repository.getAllSurahs()).thenReturn(surahsLiveData);

        viewModel = new SurahListViewModel(repository);
    }

    @Test
    public void testInitialState() {
        assertNotNull(viewModel.getSortedSurahs());
        assertNotNull(viewModel.getCurrentSortField());
        assertNotNull(viewModel.getIsAscending());

        // Initial sort should be by number ascending
        assertEquals(SurahListViewModel.SortField.NUMBER, viewModel.getCurrentSortField().getValue());
        assertEquals(true, viewModel.getIsAscending().getValue());
    }

    @Test
    public void testSortByName() {
        viewModel.sortBy(SurahListViewModel.SortField.NAME);

        assertEquals(SurahListViewModel.SortField.NAME, viewModel.getCurrentSortField().getValue());
        assertNotNull(viewModel.getIsAscending().getValue());

        assertNotNull(viewModel.getSortedSurahs());
    }

    @Test
    public void testSortByNumberDescending() {
        // First click - should toggle or set ascending
        viewModel.sortBy(SurahListViewModel.SortField.NUMBER);
        Boolean firstSort = viewModel.getIsAscending().getValue();
        assertNotNull(firstSort);

        // Second click - should toggle
        viewModel.sortBy(SurahListViewModel.SortField.NUMBER);
        Boolean secondSort = viewModel.getIsAscending().getValue();
        assertNotNull(secondSort);

        // Verify it toggled
        assertNotEquals(firstSort, secondSort);
    }

    @Test
    public void testSortByAyahCount() {
        viewModel.sortBy(SurahListViewModel.SortField.AYAH_COUNT);

        assertEquals(SurahListViewModel.SortField.AYAH_COUNT, viewModel.getCurrentSortField().getValue());
        assertNotNull(viewModel.getIsAscending().getValue());

        assertNotNull(viewModel.getSortedSurahs());
    }

    @Test
    public void testSortByRevelationType() {
        viewModel.sortBy(SurahListViewModel.SortField.REVELATION_TYPE);

        assertEquals(SurahListViewModel.SortField.REVELATION_TYPE, viewModel.getCurrentSortField().getValue());
        assertEquals(true, viewModel.getIsAscending().getValue());
    }

    @Test
    public void testToggleSortDirection() {
        viewModel.sortBy(SurahListViewModel.SortField.NAME);
        assertEquals(true, viewModel.getIsAscending().getValue());

        viewModel.sortBy(SurahListViewModel.SortField.NAME);
        assertEquals(false, viewModel.getIsAscending().getValue());

        viewModel.sortBy(SurahListViewModel.SortField.NAME);
        assertEquals(true, viewModel.getIsAscending().getValue());
    }

    @Test
    public void testSwitchSortField() {
        viewModel.sortBy(SurahListViewModel.SortField.NAME);
        assertEquals(SurahListViewModel.SortField.NAME, viewModel.getCurrentSortField().getValue());

        viewModel.sortBy(SurahListViewModel.SortField.NUMBER);
        assertEquals(SurahListViewModel.SortField.NUMBER, viewModel.getCurrentSortField().getValue());
        assertEquals(true, viewModel.getIsAscending().getValue());
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
