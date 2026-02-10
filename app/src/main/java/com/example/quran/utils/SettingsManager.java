package com.example.quran.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manager class for app settings and preferences.
 */
public class SettingsManager {

    private static final String PREFS_NAME = "QuranPreferences";
    private static final String KEY_THEME = "theme";
    private static final String KEY_FONT_SIZE = "font_size";
    private static final String KEY_LANGUAGE = "language";

    // Theme constants
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";

    // Font size constants
    public static final int FONT_SIZE_SMALL = 0;
    public static final int FONT_SIZE_MEDIUM = 1;
    public static final int FONT_SIZE_LARGE = 2;
    public static final int FONT_SIZE_EXTRA_LARGE = 3;

    // Language constants
    public static final String LANGUAGE_ENGLISH = "english";
    public static final String LANGUAGE_ARABIC = "arabic";

    private final SharedPreferences preferences;

    public SettingsManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Get current theme setting.
     * @return THEME_LIGHT or THEME_DARK
     */
    public String getTheme() {
        return preferences.getString(KEY_THEME, THEME_LIGHT);
    }

    /**
     * Set theme preference.
     * @param theme THEME_LIGHT or THEME_DARK
     */
    public void setTheme(String theme) {
        preferences.edit().putString(KEY_THEME, theme).apply();
    }

    /**
     * Check if dark theme is enabled.
     */
    public boolean isDarkTheme() {
        return THEME_DARK.equals(getTheme());
    }

    /**
     * Get current font size setting.
     * @return Font size index (0-3)
     */
    public int getFontSize() {
        return preferences.getInt(KEY_FONT_SIZE, FONT_SIZE_MEDIUM);
    }

    /**
     * Set font size preference.
     * @param fontSize Font size index (0-3)
     */
    public void setFontSize(int fontSize) {
        preferences.edit().putInt(KEY_FONT_SIZE, fontSize).apply();
    }

    /**
     * Get font size multiplier for scaling text.
     * @return Multiplier value (0.8 to 1.4)
     */
    public float getFontSizeMultiplier() {
        switch (getFontSize()) {
            case FONT_SIZE_SMALL:
                return 0.8f;
            case FONT_SIZE_MEDIUM:
                return 1.0f;
            case FONT_SIZE_LARGE:
                return 1.2f;
            case FONT_SIZE_EXTRA_LARGE:
                return 1.4f;
            default:
                return 1.0f;
        }
    }

    /**
     * Get font size label for display.
     */
    public static String getFontSizeLabel(int fontSize) {
        switch (fontSize) {
            case FONT_SIZE_SMALL:
                return "Small";
            case FONT_SIZE_MEDIUM:
                return "Medium";
            case FONT_SIZE_LARGE:
                return "Large";
            case FONT_SIZE_EXTRA_LARGE:
                return "Extra Large";
            default:
                return "Medium";
        }
    }

    /**
     * Get current language setting.
     * @return LANGUAGE_ENGLISH or LANGUAGE_ARABIC
     */
    public String getLanguage() {
        return preferences.getString(KEY_LANGUAGE, LANGUAGE_ENGLISH);
    }

    /**
     * Set language preference.
     * @param language LANGUAGE_ENGLISH or LANGUAGE_ARABIC
     */
    public void setLanguage(String language) {
        preferences.edit().putString(KEY_LANGUAGE, language).apply();
    }

    /**
     * Check if Arabic language is enabled.
     */
    public boolean isArabicLanguage() {
        return LANGUAGE_ARABIC.equals(getLanguage());
    }
}
