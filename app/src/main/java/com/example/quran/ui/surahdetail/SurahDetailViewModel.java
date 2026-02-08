package com.example.quran.ui.surahdetail;

import androidx.lifecycle.LiveData;

import com.example.quran.data.model.Ayah;
import com.example.quran.data.model.Surah;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseViewModel;

import java.util.List;

/**
 * ViewModel for Surah Detail screen.
 */
public class SurahDetailViewModel extends BaseViewModel {

    private LiveData<Surah> surah;
    private LiveData<List<Ayah>> ayahs;

    public SurahDetailViewModel(QuranRepository repository) {
        super(repository);
    }

    public void loadSurah(int surahNumber) {
        surah = repository.getSurahByNumber(surahNumber);
        ayahs = repository.getAyahsBySurah(surahNumber);
    }

    public LiveData<Surah> getSurah() {
        return surah;
    }

    public LiveData<List<Ayah>> getAyahs() {
        return ayahs;
    }
}
