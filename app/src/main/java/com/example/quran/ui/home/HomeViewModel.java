package com.example.quran.ui.home;

import androidx.lifecycle.LiveData;

import com.example.quran.data.model.Surah;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseViewModel;

import java.util.List;

/**
 * ViewModel for Home screen.
 */
public class HomeViewModel extends BaseViewModel {

    private final LiveData<List<Surah>> allSurahs;

    public HomeViewModel(QuranRepository repository) {
        super(repository);
        this.allSurahs = repository.getAllSurahs();
    }

    public LiveData<List<Surah>> getAllSurahs() {
        return allSurahs;
    }
}
