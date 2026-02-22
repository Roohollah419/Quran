package com.example.quran.ui.comments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quran.data.model.Ayah;
import com.example.quran.data.model.Surah;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseViewModel;
import com.example.quran.utils.CommentManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ViewModel for CommentsFragment.
 */
public class CommentsViewModel extends BaseViewModel {

    private final QuranRepository repository;
    private final CommentManager commentManager;
    private final MutableLiveData<List<Ayah>> commentedAyahs = new MutableLiveData<>();
    private final MutableLiveData<Map<Integer, String>> surahNamesEnglish = new MutableLiveData<>();
    private final MutableLiveData<Map<Integer, String>> surahNamesArabic = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> commentsMap = new MutableLiveData<>();

    public CommentsViewModel(QuranRepository repository) {
        super(repository);
        this.repository = repository;
        this.commentManager = new CommentManager(repository.getContext());
    }

    public LiveData<List<Ayah>> getCommentedAyahs() {
        return commentedAyahs;
    }

    public LiveData<Map<Integer, String>> getSurahNamesEnglish() {
        return surahNamesEnglish;
    }

    public LiveData<Map<Integer, String>> getSurahNamesArabic() {
        return surahNamesArabic;
    }

    public LiveData<Map<String, String>> getCommentsMap() {
        return commentsMap;
    }

    public void loadComments() {
        Map<String, String> comments = commentManager.getAllComments();

        if (comments.isEmpty()) {
            commentedAyahs.setValue(new ArrayList<>());
            commentsMap.setValue(new HashMap<>());
            return;
        }

        // Set comments map immediately
        commentsMap.setValue(comments);

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

        // Load all commented Ayahs from database
        List<Ayah> ayahList = new ArrayList<>();
        for (String commentKey : comments.keySet()) {
            int[] numbers = CommentManager.parseCommentKey(commentKey);
            if (numbers != null) {
                int surahNumber = numbers[0];
                int ayahNumber = numbers[1];

                // Load ayah from repository
                repository.getAyahsBySurah(surahNumber).observeForever(ayahs -> {
                    if (ayahs != null) {
                        for (Ayah ayah : ayahs) {
                            if (ayah.getAyahNumber() == ayahNumber) {
                                ayahList.add(ayah);
                                commentedAyahs.setValue(new ArrayList<>(ayahList));
                                break;
                            }
                        }
                    }
                });
            }
        }
    }
}
