package com.example.quran.ui.home;

import androidx.lifecycle.LiveData;

import com.example.quran.data.model.Ayah;
import com.example.quran.data.model.Surah;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseViewModel;

/**
 * ViewModel for Home screen.
 */
public class HomeViewModel extends BaseViewModel {

    public HomeViewModel(QuranRepository repository) {
        super(repository);
    }

    /**
     * Get a random Ayah from the entire Quran.
     */
    public LiveData<Ayah> getRandomAyah() {
        return repository.getRandomAyah();
    }

    /**
     * Get a Surah by its number.
     */
    public LiveData<Surah> getSurahByNumber(int surahNumber) {
        return repository.getSurahByNumber(surahNumber);
    }

    /**
     * Get a random Surah number for "I'm Feeling Lucky" feature.
     */
    public void getRandomSurahNumber(QuranRepository.RandomSurahCallback callback) {
        repository.getRandomSurahNumber(callback);
    }
}
