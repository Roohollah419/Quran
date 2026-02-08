package com.example.quran.ui.surahlist;

import androidx.lifecycle.LiveData;

import com.example.quran.data.model.Surah;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseViewModel;

import java.util.List;

/**
 * ViewModel for Surah List screen.
 */
public class SurahListViewModel extends BaseViewModel {

    private final LiveData<List<Surah>> allSurahs;

    public SurahListViewModel(QuranRepository repository) {
        super(repository);
        this.allSurahs = repository.getAllSurahs();
    }

    public LiveData<List<Surah>> getAllSurahs() {
        return allSurahs;
    }
}
