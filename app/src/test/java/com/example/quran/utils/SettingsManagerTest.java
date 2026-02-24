package com.example.quran.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SettingsManagerTest {

    private SettingsManager settingsManager;
    @Mock
    private Context mockContext;
    @Mock
    private SharedPreferences mockPreferences;
    @Mock
    private SharedPreferences.Editor mockEditor;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Mock SharedPreferences behavior
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPreferences);
        when(mockPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        when(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor);
        when(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor);
        doNothing().when(mockEditor).apply();

        // Set default values
        when(mockPreferences.getString(eq("language"), anyString())).thenReturn(SettingsManager.LANGUAGE_ARABIC);
        when(mockPreferences.getString(eq("theme"), anyString())).thenReturn(SettingsManager.THEME_LIGHT);
        when(mockPreferences.getInt(eq("font_size"), anyInt())).thenReturn(SettingsManager.FONT_SIZE_MEDIUM);
        when(mockPreferences.getBoolean(eq("tajweed_enabled"), anyBoolean())).thenReturn(true);

        settingsManager = new SettingsManager(mockContext);
    }

    @Test
    public void testDefaultLanguage() {
        String language = settingsManager.getLanguage();
        assertEquals(SettingsManager.LANGUAGE_ARABIC, language);
    }

    @Test
    public void testDefaultTheme() {
        String theme = settingsManager.getTheme();
        assertEquals(SettingsManager.THEME_LIGHT, theme);
    }

    @Test
    public void testDefaultFontSize() {
        int fontSize = settingsManager.getFontSize();
        assertEquals(SettingsManager.FONT_SIZE_MEDIUM, fontSize);
    }

    @Test
    public void testIsArabicLanguage() {
        assertTrue(settingsManager.isArabicLanguage());
    }

    @Test
    public void testIsDarkTheme() {
        assertFalse(settingsManager.isDarkTheme());
    }

    @Test
    public void testGetFontSizeMultiplierMedium() {
        assertEquals(1.0f, settingsManager.getFontSizeMultiplier(), 0.01f);
    }

    @Test
    public void testSetLanguageCallsEditor() {
        settingsManager.setLanguage(SettingsManager.LANGUAGE_ENGLISH);
        verify(mockEditor).putString("language", SettingsManager.LANGUAGE_ENGLISH);
        verify(mockEditor).apply();
    }

    @Test
    public void testSetThemeCallsEditor() {
        settingsManager.setTheme(SettingsManager.THEME_DARK);
        verify(mockEditor).putString("theme", SettingsManager.THEME_DARK);
        verify(mockEditor).apply();
    }

    @Test
    public void testSetFontSizeCallsEditor() {
        settingsManager.setFontSize(SettingsManager.FONT_SIZE_LARGE);
        verify(mockEditor).putInt("font_size", SettingsManager.FONT_SIZE_LARGE);
        verify(mockEditor).apply();
    }

    @Test
    public void testFontSizeConstants() {
        assertEquals(0, SettingsManager.FONT_SIZE_SMALL);
        assertEquals(1, SettingsManager.FONT_SIZE_MEDIUM);
        assertEquals(2, SettingsManager.FONT_SIZE_LARGE);
        assertEquals(3, SettingsManager.FONT_SIZE_EXTRA_LARGE);
    }

    @Test
    public void testThemeConstants() {
        assertEquals("light", SettingsManager.THEME_LIGHT);
        assertEquals("dark", SettingsManager.THEME_DARK);
    }

    @Test
    public void testLanguageConstants() {
        assertEquals("english", SettingsManager.LANGUAGE_ENGLISH);
        assertEquals("arabic", SettingsManager.LANGUAGE_ARABIC);
    }

    @Test
    public void testGetFontSizeLabel() {
        assertEquals("Small", SettingsManager.getFontSizeLabel(SettingsManager.FONT_SIZE_SMALL));
        assertEquals("Medium", SettingsManager.getFontSizeLabel(SettingsManager.FONT_SIZE_MEDIUM));
        assertEquals("Large", SettingsManager.getFontSizeLabel(SettingsManager.FONT_SIZE_LARGE));
        assertEquals("Extra Large", SettingsManager.getFontSizeLabel(SettingsManager.FONT_SIZE_EXTRA_LARGE));
    }

    // Tajweed tests
    @Test
    public void testDefaultTajweedEnabled() {
        // Default should be true (ON)
        boolean tajweedEnabled = settingsManager.getTajweed();
        assertTrue(tajweedEnabled);
    }

    @Test
    public void testGetTajweedReturnsTrue() {
        // When enabled
        when(mockPreferences.getBoolean(eq("tajweed_enabled"), anyBoolean())).thenReturn(true);
        assertTrue(settingsManager.getTajweed());
    }

    @Test
    public void testGetTajweedReturnsFalse() {
        // When disabled
        when(mockPreferences.getBoolean(eq("tajweed_enabled"), anyBoolean())).thenReturn(false);
        assertFalse(settingsManager.getTajweed());
    }

    @Test
    public void testSetTajweedCallsEditor() {
        // Verify that setting Tajweed persists to SharedPreferences
        settingsManager.setTajweed(true);
        verify(mockEditor).putBoolean("tajweed_enabled", true);
        verify(mockEditor).apply();
    }

    @Test
    public void testIsTajweedEnabledMatchesGetTajweed() {
        // isTajweedEnabled() should be consistent with getTajweed()
        when(mockPreferences.getBoolean(eq("tajweed_enabled"), anyBoolean())).thenReturn(true);
        assertEquals(settingsManager.getTajweed(), settingsManager.isTajweedEnabled());

        when(mockPreferences.getBoolean(eq("tajweed_enabled"), anyBoolean())).thenReturn(false);
        assertEquals(settingsManager.getTajweed(), settingsManager.isTajweedEnabled());
    }
}
