package com.example.quran.utils;

import android.content.Context;

import com.example.quran.R;

/**
 * Utility class for formatting Ayah text with Quranic conventions.
 * <p>
 * Handles text formatting including:
 * - Adding Quranic ornamental brackets around verse numbers
 * - Applying Tajweed highlighting based on user preferences
 * </p>
 */
public class AyahTextFormatter {

    /**
     * Format ayah text with Quranic ornamental brackets around the number.
     * <p>
     * Example: "Arabic text ﴿١٢٣﴾"
     * </p>
     *
     * @param arabicText  The Arabic text of the ayah
     * @param ayahNumber  The verse number
     * @return Formatted text with ornamental brackets
     */
    public static String formatWithNumber(String arabicText, int ayahNumber) {
        String arabicNumber = ArabicNumeralConverter.convert(ayahNumber);
        return arabicText + " ﴿" + arabicNumber + "﴾";
    }

    /**
     * Apply Tajweed highlighting to text if enabled in settings.
     * <p>
     * Returns styled text with color-coded Tajweed rules if enabled,
     * otherwise returns plain text.
     * </p>
     *
     * @param text             The text to apply Tajweed highlighting to
     * @param context          Context for accessing color resources
     * @param settingsManager  Settings manager to check if Tajweed is enabled
     * @return Styled CharSequence if Tajweed enabled, plain text otherwise
     */
    public static CharSequence applyTajweedIfEnabled(
            String text,
            Context context,
            SettingsManager settingsManager) {

        if (!settingsManager.isTajweedEnabled()) {
            return text;
        }

        return TajweedHelper.applyTajweed(
            text,
            context.getColor(R.color.tajweed_ghunnah),
            context.getColor(R.color.tajweed_iqlaab),
            context.getColor(R.color.tajweed_ikhfaa),
            context.getColor(R.color.tajweed_qalqalah),
            context.getColor(R.color.tajweed_madd),
            context.getColor(R.color.tajweed_heavy),
            context.getColor(R.color.tajweed_laam_allah)
        );
    }

    /**
     * Format text with number and apply Tajweed highlighting.
     * <p>
     * Convenience method that combines formatWithNumber() and applyTajweedIfEnabled().
     * </p>
     *
     * @param arabicText       The Arabic text of the ayah
     * @param ayahNumber       The verse number
     * @param context          Context for accessing color resources
     * @param settingsManager  Settings manager to check if Tajweed is enabled
     * @return Formatted and styled CharSequence
     */
    public static CharSequence formatAndStyleAyah(
            String arabicText,
            int ayahNumber,
            Context context,
            SettingsManager settingsManager) {

        String formattedText = formatWithNumber(arabicText, ayahNumber);
        return applyTajweedIfEnabled(formattedText, context, settingsManager);
    }
}
