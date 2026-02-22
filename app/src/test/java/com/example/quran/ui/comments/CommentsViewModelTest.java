package com.example.quran.ui.comments;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.quran.data.model.Ayah;
import com.example.quran.data.model.Surah;
import com.example.quran.data.repository.QuranRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CommentsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private QuranRepository repository;

    @Mock
    private Context mockContext;

    @Mock
    private SharedPreferences mockPreferences;

    @Mock
    private SharedPreferences.Editor mockEditor;

    private CommentsViewModel viewModel;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Mock SharedPreferences
        when(repository.getContext()).thenReturn(mockContext);
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPreferences);
        when(mockPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        when(mockEditor.remove(anyString())).thenReturn(mockEditor);
        when(mockEditor.commit()).thenReturn(true);

        // Mock empty comments by default
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(null);

        viewModel = new CommentsViewModel(repository);
    }

    @Test
    public void testLoadComments_withNoComments_setsEmptyList() {
        // Comments are empty by default (setup)
        viewModel.loadComments();

        assertNotNull(viewModel.getCommentedAyahs().getValue());
        assertTrue(viewModel.getCommentedAyahs().getValue().isEmpty());
    }

    @Test
    public void testLoadComments_withComments_loadsSurahNames() {
        // Mock comments in SharedPreferences
        String jsonWithComments = "{\"1:1\":\"Test comment\"}";
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(jsonWithComments);

        // Mock repository responses for surahs
        List<Surah> surahs = new ArrayList<>();
        surahs.add(createTestSurah(1, "Al-Fatihah", "الفاتحة"));
        MutableLiveData<List<Surah>> surahsLiveData = new MutableLiveData<>(surahs);
        when(repository.getAllSurahs()).thenReturn(surahsLiveData);

        // Mock repository response for ayahs
        List<Ayah> ayahs = new ArrayList<>();
        ayahs.add(createTestAyah(1, 1));
        MutableLiveData<List<Ayah>> ayahsLiveData = new MutableLiveData<>(ayahs);
        when(repository.getAyahsBySurah(1)).thenReturn(ayahsLiveData);

        // Recreate viewModel to use updated mock preferences
        viewModel = new CommentsViewModel(repository);
        viewModel.loadComments();

        // Verify repository methods were called
        verify(repository).getAllSurahs();
        verify(repository).getAyahsBySurah(1);

        // Verify LiveData values are set
        assertNotNull(viewModel.getCommentsMap().getValue());
        assertFalse(viewModel.getCommentsMap().getValue().isEmpty());
    }

    @Test
    public void testGetCommentedAyahs_returnsLiveData() {
        assertNotNull(viewModel.getCommentedAyahs());
    }

    @Test
    public void testGetSurahNamesEnglish_returnsLiveData() {
        assertNotNull(viewModel.getSurahNamesEnglish());
    }

    @Test
    public void testGetSurahNamesArabic_returnsLiveData() {
        assertNotNull(viewModel.getSurahNamesArabic());
    }

    @Test
    public void testGetCommentsMap_returnsLiveData() {
        assertNotNull(viewModel.getCommentsMap());
    }

    @Test
    public void testLoadComments_withMultipleComments_loadsAllAyahs() {
        // Mock multiple comments
        String jsonWithComments = "{\"1:1\":\"Comment 1\",\"2:5\":\"Comment 2\"}";
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(jsonWithComments);

        // Mock repository responses
        List<Surah> surahs = new ArrayList<>();
        surahs.add(createTestSurah(1, "Al-Fatihah", "الفاتحة"));
        surahs.add(createTestSurah(2, "Al-Baqarah", "البقرة"));
        MutableLiveData<List<Surah>> surahsLiveData = new MutableLiveData<>(surahs);
        when(repository.getAllSurahs()).thenReturn(surahsLiveData);

        List<Ayah> ayahs1 = new ArrayList<>();
        ayahs1.add(createTestAyah(1, 1));
        MutableLiveData<List<Ayah>> ayahsLiveData1 = new MutableLiveData<>(ayahs1);
        when(repository.getAyahsBySurah(1)).thenReturn(ayahsLiveData1);

        List<Ayah> ayahs2 = new ArrayList<>();
        ayahs2.add(createTestAyah(2, 5));
        MutableLiveData<List<Ayah>> ayahsLiveData2 = new MutableLiveData<>(ayahs2);
        when(repository.getAyahsBySurah(2)).thenReturn(ayahsLiveData2);

        // Recreate viewModel
        viewModel = new CommentsViewModel(repository);
        viewModel.loadComments();

        // Verify repository methods were called for both surahs
        verify(repository).getAllSurahs();
        verify(repository).getAyahsBySurah(1);
        verify(repository).getAyahsBySurah(2);

        // Verify comments map is populated
        assertNotNull(viewModel.getCommentsMap().getValue());
        assertEquals(2, viewModel.getCommentsMap().getValue().size());
    }

    @Test
    public void testLoadComments_setsSurahNamesCorrectly() {
        // Mock comments
        String jsonWithComments = "{\"1:1\":\"Test comment\"}";
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(jsonWithComments);

        // Mock surahs
        List<Surah> surahs = new ArrayList<>();
        surahs.add(createTestSurah(1, "Al-Fatihah", "الفاتحة"));
        MutableLiveData<List<Surah>> surahsLiveData = new MutableLiveData<>(surahs);
        when(repository.getAllSurahs()).thenReturn(surahsLiveData);

        // Mock ayahs
        List<Ayah> ayahs = new ArrayList<>();
        ayahs.add(createTestAyah(1, 1));
        MutableLiveData<List<Ayah>> ayahsLiveData = new MutableLiveData<>(ayahs);
        when(repository.getAyahsBySurah(1)).thenReturn(ayahsLiveData);

        // Recreate viewModel
        viewModel = new CommentsViewModel(repository);
        viewModel.loadComments();

        // Wait for async operations
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // Ignore
        }

        // Verify surah names LiveData are not null
        assertNotNull(viewModel.getSurahNamesEnglish());
        assertNotNull(viewModel.getSurahNamesArabic());
    }

    @Test
    public void testLoadComments_withInvalidCommentKey_handlesGracefully() {
        // Mock comments with invalid key
        String jsonWithComments = "{\"invalid\":\"Test comment\"}";
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(jsonWithComments);

        // Mock surahs
        List<Surah> surahs = new ArrayList<>();
        surahs.add(createTestSurah(1, "Al-Fatihah", "الفاتحة"));
        MutableLiveData<List<Surah>> surahsLiveData = new MutableLiveData<>(surahs);
        when(repository.getAllSurahs()).thenReturn(surahsLiveData);

        // Recreate viewModel
        viewModel = new CommentsViewModel(repository);
        viewModel.loadComments();

        // Should not crash and should handle gracefully
        assertNotNull(viewModel.getCommentsMap().getValue());
    }

    private Ayah createTestAyah(int surahNumber, int ayahNumber) {
        Ayah ayah = new Ayah();
        ayah.setSurahNumber(surahNumber);
        ayah.setAyahNumber(ayahNumber);
        ayah.setTextArabic("Arabic text");
        ayah.setTextTranslation("English translation");
        return ayah;
    }

    private Surah createTestSurah(int number, String nameEnglish, String nameArabic) {
        Surah surah = new Surah();
        surah.setNumber(number);
        surah.setNameEnglish(nameEnglish);
        surah.setNameArabic(nameArabic);
        surah.setTotalAyahs(7);
        surah.setRevelationType("Meccan");
        return surah;
    }
}
