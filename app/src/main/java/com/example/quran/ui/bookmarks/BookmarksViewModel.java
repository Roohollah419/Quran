package com.example.quran.ui.bookmarks;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quran.data.model.Ayah;
import com.example.quran.data.model.Surah;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseViewModel;
import com.example.quran.utils.BookmarkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ViewModel for BookmarksFragment.
 */
public class BookmarksViewModel extends BaseViewModel {

    private final QuranRepository repository;
    private final BookmarkManager bookmarkManager;
    private final MutableLiveData<List<Ayah>> bookmarkedAyahs = new MutableLiveData<>();
    private final MutableLiveData<Map<Integer, String>> surahNamesEnglish = new MutableLiveData<>();
    private final MutableLiveData<Map<Integer, String>> surahNamesArabic = new MutableLiveData<>();

    public BookmarksViewModel(QuranRepository repository) {
        super(repository);
        this.repository = repository;
        this.bookmarkManager = new BookmarkManager(repository.getContext());
    }

    public LiveData<List<Ayah>> getBookmarkedAyahs() {
        return bookmarkedAyahs;
    }

    public LiveData<Map<Integer, String>> getSurahNamesEnglish() {
        return surahNamesEnglish;
    }

    public LiveData<Map<Integer, String>> getSurahNamesArabic() {
        return surahNamesArabic;
    }

    public void loadBookmarks() {
        Set<String> bookmarks = bookmarkManager.getBookmarks();

        if (bookmarks.isEmpty()) {
            bookmarkedAyahs.setValue(new ArrayList<>());
            return;
        }

        // Load all Surah names first (both English and Arabic)
        repository.getAllSurahs().observeForever(surahs -> {
            if (surahs != null) {
                Map<Integer, String> englishMap = new HashMap<>();
                Map<Integer, String> arabicMap = new HashMap<>();
                for (Surah surah : surahs) {
                    englishMap.put(surah.getNumber(), surah.getNameEnglish());
                    arabicMap.put(surah.getNumber(), surah.getNameArabic());
                }
                surahNamesEnglish.setValue(englishMap);
                surahNamesArabic.setValue(arabicMap);
            }
        });

        // Load all bookmarked Ayahs from database
        List<Ayah> ayahList = new ArrayList<>();
        for (String bookmarkKey : bookmarks) {
            int[] numbers = BookmarkManager.parseBookmarkKey(bookmarkKey);
            if (numbers != null) {
                int surahNumber = numbers[0];
                int ayahNumber = numbers[1];

                // Load ayah from repository
                repository.getAyahsBySurah(surahNumber).observeForever(ayahs -> {
                    if (ayahs != null) {
                        for (Ayah ayah : ayahs) {
                            if (ayah.getAyahNumber() == ayahNumber) {
                                ayahList.add(ayah);
                                bookmarkedAyahs.setValue(new ArrayList<>(ayahList));
                                break;
                            }
                        }
                    }
                });
            }
        }
    }
}
