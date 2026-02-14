package com.example.quran.ui.settings;

import android.content.Context;

import com.example.quran.utils.SettingsManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class SettingsDialogFragmentTest {

    private Context context;
    private SettingsManager settingsManager;
    private boolean listenerCalled;

    @Before
    public void setup() {
        context = RuntimeEnvironment.getApplication();
        settingsManager = new SettingsManager(context);
        listenerCalled = false;
    }

    @Test
    public void testFragmentCreation() {
        SettingsDialogFragment fragment = SettingsDialogFragment.newInstance(() -> {
            listenerCalled = true;
        });
        assertNotNull(fragment);
    }

    @Test
    public void testListenerCallback() {
        SettingsDialogFragment.OnSettingsChangedListener listener = () -> {
            listenerCalled = true;
        };
        listener.onSettingsChanged();
        assertTrue(listenerCalled);
    }

    @Test
    public void testSettingsManagerIntegration_ThemeLight() {
        settingsManager.setTheme(SettingsManager.THEME_LIGHT);
        assertFalse(settingsManager.isDarkTheme());
    }

    @Test
    public void testSettingsManagerIntegration_ThemeDark() {
        settingsManager.setTheme(SettingsManager.THEME_DARK);
        assertTrue(settingsManager.isDarkTheme());
    }

    @Test
    public void testSettingsManagerIntegration_FontSizeSmall() {
        settingsManager.setFontSize(0);
        assertEquals(0, settingsManager.getFontSize());
    }

    @Test
    public void testSettingsManagerIntegration_FontSizeMedium() {
        settingsManager.setFontSize(1);
        assertEquals(1, settingsManager.getFontSize());
    }

    @Test
    public void testSettingsManagerIntegration_FontSizeLarge() {
        settingsManager.setFontSize(2);
        assertEquals(2, settingsManager.getFontSize());
    }

    @Test
    public void testSettingsManagerIntegration_FontSizeXLarge() {
        settingsManager.setFontSize(3);
        assertEquals(3, settingsManager.getFontSize());
    }

    @Test
    public void testSettingsManagerIntegration_LanguageEnglish() {
        settingsManager.setLanguage(SettingsManager.LANGUAGE_ENGLISH);
        assertFalse(settingsManager.isArabicLanguage());
    }

    @Test
    public void testSettingsManagerIntegration_LanguageArabic() {
        settingsManager.setLanguage(SettingsManager.LANGUAGE_ARABIC);
        assertTrue(settingsManager.isArabicLanguage());
    }

    @Test
    public void testMultipleThemeChanges() {
        // Test switching themes multiple times
        settingsManager.setTheme(SettingsManager.THEME_LIGHT);
        assertFalse(settingsManager.isDarkTheme());

        settingsManager.setTheme(SettingsManager.THEME_DARK);
        assertTrue(settingsManager.isDarkTheme());

        settingsManager.setTheme(SettingsManager.THEME_LIGHT);
        assertFalse(settingsManager.isDarkTheme());

        settingsManager.setTheme(SettingsManager.THEME_DARK);
        assertTrue(settingsManager.isDarkTheme());
    }

    @Test
    public void testMultipleFontSizeChanges() {
        // Test switching font sizes multiple times
        settingsManager.setFontSize(0);
        assertEquals(0, settingsManager.getFontSize());

        settingsManager.setFontSize(3);
        assertEquals(3, settingsManager.getFontSize());

        settingsManager.setFontSize(1);
        assertEquals(1, settingsManager.getFontSize());

        settingsManager.setFontSize(2);
        assertEquals(2, settingsManager.getFontSize());
    }

    @Test
    public void testThemeChangesArePersistent() {
        // Set theme to dark
        settingsManager.setTheme(SettingsManager.THEME_DARK);
        assertTrue(settingsManager.isDarkTheme());

        // Create new instance to verify persistence
        SettingsManager newSettingsManager = new SettingsManager(context);
        assertTrue(newSettingsManager.isDarkTheme());
    }

    @Test
    public void testFontSizeChangesArePersistent() {
        // Set font size to large
        settingsManager.setFontSize(2);
        assertEquals(2, settingsManager.getFontSize());

        // Create new instance to verify persistence
        SettingsManager newSettingsManager = new SettingsManager(context);
        assertEquals(2, newSettingsManager.getFontSize());
    }

    @Test
    public void testLanguageChangesArePersistent() {
        // Set language to Arabic
        settingsManager.setLanguage(SettingsManager.LANGUAGE_ARABIC);
        assertTrue(settingsManager.isArabicLanguage());

        // Create new instance to verify persistence
        SettingsManager newSettingsManager = new SettingsManager(context);
        assertTrue(newSettingsManager.isArabicLanguage());
    }

    @Test
    public void testAllSettingsChangesTogether() {
        // Change all settings
        settingsManager.setTheme(SettingsManager.THEME_DARK);
        settingsManager.setFontSize(3);
        settingsManager.setLanguage(SettingsManager.LANGUAGE_ARABIC);

        // Verify all settings
        assertTrue(settingsManager.isDarkTheme());
        assertEquals(3, settingsManager.getFontSize());
        assertTrue(settingsManager.isArabicLanguage());

        // Change all settings again
        settingsManager.setTheme(SettingsManager.THEME_LIGHT);
        settingsManager.setFontSize(0);
        settingsManager.setLanguage(SettingsManager.LANGUAGE_ENGLISH);

        // Verify all settings changed
        assertFalse(settingsManager.isDarkTheme());
        assertEquals(0, settingsManager.getFontSize());
        assertFalse(settingsManager.isArabicLanguage());
    }

    @Test
    public void testSettingsManagerConstants() {
        assertNotNull(SettingsManager.THEME_LIGHT);
        assertNotNull(SettingsManager.THEME_DARK);
        assertNotNull(SettingsManager.LANGUAGE_ENGLISH);
        assertNotNull(SettingsManager.LANGUAGE_ARABIC);
    }

    @Test
    public void testRapidThemeSwitching() {
        // Simulate rapid theme switching
        for (int i = 0; i < 5; i++) {
            settingsManager.setTheme(SettingsManager.THEME_DARK);
            assertTrue(settingsManager.isDarkTheme());

            settingsManager.setTheme(SettingsManager.THEME_LIGHT);
            assertFalse(settingsManager.isDarkTheme());
        }
    }

    @Test
    public void testRapidFontSizeSwitching() {
        // Simulate rapid font size switching
        settingsManager.setFontSize(0);
        assertEquals(0, settingsManager.getFontSize());

        settingsManager.setFontSize(3);
        assertEquals(3, settingsManager.getFontSize());

        settingsManager.setFontSize(1);
        assertEquals(1, settingsManager.getFontSize());

        settingsManager.setFontSize(2);
        assertEquals(2, settingsManager.getFontSize());

        settingsManager.setFontSize(0);
        assertEquals(0, settingsManager.getFontSize());
    }

    @Test
    public void testRapidLanguageSwitching() {
        // Simulate rapid language switching
        for (int i = 0; i < 5; i++) {
            settingsManager.setLanguage(SettingsManager.LANGUAGE_ARABIC);
            assertTrue(settingsManager.isArabicLanguage());

            settingsManager.setLanguage(SettingsManager.LANGUAGE_ENGLISH);
            assertFalse(settingsManager.isArabicLanguage());
        }
    }

    @Test
    public void testMixedRapidSettingsChanges() {
        // Simulate rapid changes across different settings
        settingsManager.setTheme(SettingsManager.THEME_DARK);
        settingsManager.setFontSize(3);
        settingsManager.setLanguage(SettingsManager.LANGUAGE_ARABIC);

        assertTrue(settingsManager.isDarkTheme());
        assertEquals(3, settingsManager.getFontSize());
        assertTrue(settingsManager.isArabicLanguage());

        settingsManager.setTheme(SettingsManager.THEME_LIGHT);
        settingsManager.setFontSize(0);
        settingsManager.setLanguage(SettingsManager.LANGUAGE_ENGLISH);

        assertFalse(settingsManager.isDarkTheme());
        assertEquals(0, settingsManager.getFontSize());
        assertFalse(settingsManager.isArabicLanguage());

        settingsManager.setTheme(SettingsManager.THEME_DARK);
        settingsManager.setFontSize(2);
        settingsManager.setLanguage(SettingsManager.LANGUAGE_ARABIC);

        assertTrue(settingsManager.isDarkTheme());
        assertEquals(2, settingsManager.getFontSize());
        assertTrue(settingsManager.isArabicLanguage());
    }
}
