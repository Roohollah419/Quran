package com.example.quran.ui.surahlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quran.data.model.Surah;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ViewModel for Surah List screen.
 */
public class SurahListViewModel extends BaseViewModel {

    public enum SortField {
        NUMBER, NAME, AYAH_COUNT, REVELATION_TYPE
    }

    private final LiveData<List<Surah>> allSurahs;
    private final MediatorLiveData<List<Surah>> sortedSurahs;
    private final MutableLiveData<SortField> currentSortField;
    private final MutableLiveData<Boolean> isAscending;

    public SurahListViewModel(QuranRepository repository) {
        super(repository);
        this.allSurahs = repository.getAllSurahs();
        this.sortedSurahs = new MediatorLiveData<>();
        this.currentSortField = new MutableLiveData<>(SortField.NUMBER);
        this.isAscending = new MutableLiveData<>(true);

        // Observe the original data and re-sort when it changes
        sortedSurahs.addSource(allSurahs, surahs -> {
            if (surahs != null) {
                sortedSurahs.setValue(sortSurahs(surahs));
            }
        });

        // Re-sort when sort field changes
        sortedSurahs.addSource(currentSortField, field -> {
            List<Surah> current = allSurahs.getValue();
            if (current != null) {
                sortedSurahs.setValue(sortSurahs(current));
            }
        });

        // Re-sort when sort order changes
        sortedSurahs.addSource(isAscending, ascending -> {
            List<Surah> current = allSurahs.getValue();
            if (current != null) {
                sortedSurahs.setValue(sortSurahs(current));
            }
        });
    }

    public LiveData<List<Surah>> getSortedSurahs() {
        return sortedSurahs;
    }

    public LiveData<SortField> getCurrentSortField() {
        return currentSortField;
    }

    public LiveData<Boolean> getIsAscending() {
        return isAscending;
    }

    public void sortBy(SortField field) {
        SortField current = currentSortField.getValue();
        if (current == field) {
            // Toggle sort order if clicking same field
            Boolean ascending = isAscending.getValue();
            isAscending.setValue(ascending == null || !ascending);
        } else {
            // New field, default to ascending
            currentSortField.setValue(field);
            isAscending.setValue(true);
        }
    }

    private List<Surah> sortSurahs(List<Surah> surahs) {
        if (surahs == null || surahs.isEmpty()) {
            return surahs;
        }

        List<Surah> sorted = new ArrayList<>(surahs);
        SortField field = currentSortField.getValue();
        Boolean ascending = isAscending.getValue();

        if (field == null) field = SortField.NUMBER;
        if (ascending == null) ascending = true;

        Comparator<Surah> comparator = getComparator(field);
        if (!ascending) {
            comparator = Collections.reverseOrder(comparator);
        }

        Collections.sort(sorted, comparator);
        return sorted;
    }

    private Comparator<Surah> getComparator(SortField field) {
        switch (field) {
            case NAME:
                return Comparator.comparing(Surah::getNameArabic);
            case AYAH_COUNT:
                return Comparator.comparingInt(Surah::getTotalAyahs);
            case REVELATION_TYPE:
                return Comparator.comparing(Surah::getRevelationType);
            case NUMBER:
            default:
                return Comparator.comparingInt(Surah::getNumber);
        }
    }
}
