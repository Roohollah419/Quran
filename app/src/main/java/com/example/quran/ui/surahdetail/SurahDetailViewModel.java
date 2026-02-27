package com.example.quran.ui.surahdetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.quran.data.model.Ayah;
import com.example.quran.data.model.Recitation;
import com.example.quran.data.model.Surah;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.data.repository.RecitationRepository;
import com.example.quran.ui.base.BaseViewModel;
import com.example.quran.utils.Constants;

import java.util.List;

/**
 * ViewModel for Surah Detail screen.
 */
public class SurahDetailViewModel extends BaseViewModel {

    private LiveData<Surah> surah;
    private LiveData<List<Ayah>> ayahs;
    private final MutableLiveData<String> selectedReciter;
    private final MutableLiveData<Integer> currentSurahNumber;
    private LiveData<Recitation> currentRecitation;
    private final RecitationRepository recitationRepository;

    public SurahDetailViewModel(QuranRepository repository) {
        super(repository);
        this.recitationRepository = repository.getRecitationRepository();
        this.selectedReciter = new MutableLiveData<>(Constants.RECITER_ALAFASY);
        this.currentSurahNumber = new MutableLiveData<>(0);

        // Load recitation when surah or reciter changes
        currentRecitation = Transformations.switchMap(currentSurahNumber, surahNum ->
            Transformations.switchMap(selectedReciter, reciter ->
                recitationRepository.getRecitationBySurahAndReciter(surahNum, reciter)
            )
        );
    }

    public void loadSurah(int surahNumber) {
        surah = repository.getSurahByNumber(surahNumber);
        ayahs = repository.getAyahsBySurah(surahNumber);
        currentSurahNumber.setValue(surahNumber);
    }

    public void setSelectedReciter(String reciter) {
        selectedReciter.setValue(reciter);
    }

    public LiveData<Surah> getSurah() {
        return surah;
    }

    public LiveData<List<Ayah>> getAyahs() {
        return ayahs;
    }

    public LiveData<String> getSelectedReciter() {
        return selectedReciter;
    }

    public LiveData<Recitation> getCurrentRecitation() {
        return currentRecitation;
    }
}
