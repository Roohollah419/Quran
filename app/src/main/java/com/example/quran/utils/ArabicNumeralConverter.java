package com.example.quran.utils;

/**
 * Utility class for converting Western numerals to Arabic-Indic numerals.
 * <p>
 * Arabic-Indic numerals (٠١٢٣٤٥٦٧٨٩) are used throughout the Quran app
 * for verse numbers and other numeric displays.
 * </p>
 */
public class ArabicNumeralConverter {

    private static final char[] ARABIC_NUMERALS = {'٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'};

    /**
     * Convert a string containing Western numerals to Arabic-Indic numerals.
     *
     * @param number String containing Western numerals (0-9)
     * @return String with Arabic-Indic numerals (٠-٩)
     */
    public static String convert(String number) {
        if (number == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (char c : number.toCharArray()) {
            if (Character.isDigit(c)) {
                result.append(ARABIC_NUMERALS[c - '0']);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Convert an integer to Arabic-Indic numeral string.
     *
     * @param number Integer to convert
     * @return String with Arabic-Indic numerals (٠-٩)
     */
    public static String convert(int number) {
        return convert(String.valueOf(number));
    }
}
