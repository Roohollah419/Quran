package com.example.quran.utils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quran.data.model.Ayah;
import com.example.quran.data.model.Surah;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.home.HomeViewModel;
import com.example.quran.ui.surahdetail.SurahDetailViewModel;
import com.example.quran.ui.surahlist.SurahListViewModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class ViewModelFactoryTest {

    @Mock
    private QuranRepository mockRepository;

    private ViewModelFactory factory;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Configure mock repository to return LiveData for all methods
        MutableLiveData<List<Surah>> surahsLiveData = new MutableLiveData<>(new ArrayList<>());
        MutableLiveData<Surah> surahLiveData = new MutableLiveData<>();
        MutableLiveData<List<Ayah>> ayahsLiveData = new MutableLiveData<>(new ArrayList<>());
        MutableLiveData<Ayah> ayahLiveData = new MutableLiveData<>();

        when(mockRepository.getAllSurahs()).thenReturn(surahsLiveData);
        when(mockRepository.getSurahByNumber(anyInt())).thenReturn(surahLiveData);
        when(mockRepository.getAyahsBySurah(anyInt())).thenReturn(ayahsLiveData);
        when(mockRepository.getRandomAyah()).thenReturn(ayahLiveData);

        factory = new ViewModelFactory(mockRepository);
    }

    @Test
    public void testCreateHomeViewModel() {
        ViewModel viewModel = factory.create(HomeViewModel.class);
        assertNotNull(viewModel);
        assertTrue(viewModel instanceof HomeViewModel);
    }

    @Test
    public void testCreateSurahListViewModel() {
        ViewModel viewModel = factory.create(SurahListViewModel.class);
        assertNotNull(viewModel);
        assertTrue(viewModel instanceof SurahListViewModel);
    }

    @Test
    public void testCreateSurahDetailViewModel() {
        ViewModel viewModel = factory.create(SurahDetailViewModel.class);
        assertNotNull(viewModel);
        assertTrue(viewModel instanceof SurahDetailViewModel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUnknownViewModel_throwsException() {
        factory.create(UnknownViewModel.class);
    }

    @Test
    public void testFactoryCreation() {
        assertNotNull(factory);
    }

    // Test class for unknown ViewModel
    private static class UnknownViewModel extends ViewModel {
    }
}
