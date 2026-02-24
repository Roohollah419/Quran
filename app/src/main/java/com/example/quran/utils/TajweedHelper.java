package com.example.quran.utils;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

/**
 * TajweedHelper - Utility class for applying Tajweed (Quranic recitation rules) highlighting
 * to Arabic Quranic text using the traditional Mushaf al-Tajweed color scheme.
 *
 * Implements 7 essential Tajweed rules:
 * 1. Noon Sakinah rules (Iqlaab, Ikhfaa)
 * 2. Meem Sakinah rules (Idghaam Shafawi, Ikhfaa Shafawi)
 * 3. Qalqalah (echoing pronunciation)
 * 4. Madd (prolongation)
 * 5. Heavy letters (Tafkheem)
 * 6. Lam in Allah's name
 *
 * The class performs character-by-character Unicode analysis with diacritic-aware parsing
 * to detect these rules and applies ForegroundColorSpan for colored rendering.
 */
public class TajweedHelper {

    // Arabic Letter Unicode Constants
    private static final char NOON = '\u0646';              // ن
    private static final char MEEM = '\u0645';              // م
    private static final char BA = '\u0628';                // ب
    private static final char ALEF = '\u0627';              // ا
    private static final char WAW = '\u0648';               // و
    private static final char YA = '\u064A';                // ي
    private static final char LAM = '\u0644';               // ل
    private static final char HEH = '\u0647';               // ه

    // Diacritic Unicode Constants
    private static final char FATHA = '\u064E';             // َ (a)
    private static final char DAMMA = '\u064F';             // ُ (u)
    private static final char KASRA = '\u0650';             // ِ (i)
    private static final char SUKUN = '\u0652';             // ْ (no vowel)
    private static final char SHADDA = '\u0651';            // ّ (double)
    private static final char TANWEEN_FATH = '\u064B';      // ً (an)
    private static final char TANWEEN_DAMM = '\u064C';      // ٌ (un)
    private static final char TANWEEN_KASR = '\u064D';      // ٍ (in)
    private static final char SUPERSCRIPT_ALEF = '\u0670';  // ٰ

    // Qalqalah Letters: ق ط ب ج د
    private static final char[] QALQALAH_LETTERS = {
        '\u0642',  // ق (Qaf)
        '\u0637',  // ط (Taa)
        '\u0628',  // ب (Ba)
        '\u062C',  // ج (Jeem)
        '\u062F'   // د (Dal)
    };

    // Heavy Letters (Tafkheem): ص ض ط ظ خ غ ق
    private static final char[] HEAVY_LETTERS = {
        '\u0635',  // ص (Saad)
        '\u0636',  // ض (Daad)
        '\u0637',  // ط (Taa)
        '\u0638',  // ظ (Dhaa)
        '\u062E',  // خ (Khaa)
        '\u063A',  // غ (Ghayn)
        '\u0642'   // ق (Qaf)
    };

    // Idhaar Letters (after Noon Sakinah - no color applied)
    private static final char[] IDHAAR_LETTERS = {
        '\u0621',  // ء (Hamza)
        '\u0647',  // ه (Haa)
        '\u0639',  // ع (Ayn)
        '\u062D',  // ح (Haa)
        '\u063A',  // غ (Ghayn)
        '\u062E'   // خ (Khaa)
    };

    // Idghaam Letters (after Noon Sakinah - partial, some with Ghunnah)
    private static final char[] IDGHAAM_LETTERS = {
        '\u064A',  // ي (Ya)
        '\u0631',  // ر (Ra)
        '\u0645',  // م (Meem)
        '\u0644',  // ل (Lam)
        '\u0648',  // و (Waw)
        '\u0646'   // ن (Noon)
    };

    // Ikhfaa Letters (15 letters after Noon Sakinah)
    private static final char[] IKHFAA_LETTERS = {
        '\u0635',  // ص (Saad)
        '\u0630',  // ذ (Dhal)
        '\u062B',  // ث (Thaa)
        '\u0643',  // ك (Kaf)
        '\u062C',  // ج (Jeem)
        '\u0634',  // ش (Sheen)
        '\u0642',  // ق (Qaf)
        '\u0633',  // س (Seen)
        '\u062F',  // د (Dal)
        '\u0637',  // ط (Taa)
        '\u0632',  // ز (Zay)
        '\u0641',  // ف (Fa)
        '\u062A',  // ت (Ta)
        '\u0636',  // ض (Daad)
        '\u0638'   // ظ (Dhaa)
    };

    /**
     * Applies Tajweed highlighting to Arabic Quranic text.
     *
     * @param arabicText The Arabic text to analyze
     * @param ghunnahColor Color for Ghunnah (nasal sound) - Dark Green
     * @param iqlaabColor Color for Iqlaab (conversion) - Purple
     * @param ikhfaaColor Color for Ikhfaa (concealment) - Orange
     * @param qalqalahColor Color for Qalqalah (echoing) - Dark Green
     * @param maddColor Color for Madd (prolongation) - Red
     * @param heavyLetterColor Color for Heavy letters - Blue
     * @param laamAllahColor Color for Lam in Allah - Golden
     * @return SpannableString with color spans applied
     */
    public static SpannableString applyTajweed(
            String arabicText,
            int ghunnahColor,
            int iqlaabColor,
            int ikhfaaColor,
            int qalqalahColor,
            int maddColor,
            int heavyLetterColor,
            int laamAllahColor) {

        if (arabicText == null || arabicText.isEmpty()) {
            return new SpannableString("");
        }

        SpannableString spannable = new SpannableString(arabicText);

        // Process each character
        for (int i = 0; i < arabicText.length(); i++) {
            char currentChar = arabicText.charAt(i);

            // Skip if already processed or is a diacritic
            if (isDiacritic(currentChar)) {
                continue;
            }

            // Check rules in priority order
            // 1. Noon Sakinah rules (highest priority)
            if (currentChar == NOON) {
                String rule = detectNoonSakinahRule(arabicText, i);
                if ("iqlaab".equals(rule)) {
                    applyColorSpan(spannable, i, iqlaabColor);
                    continue;
                } else if ("ikhfaa".equals(rule)) {
                    applyColorSpan(spannable, i, ikhfaaColor);
                    continue;
                }
            }

            // 2. Meem Sakinah rules
            if (currentChar == MEEM) {
                String rule = detectMeemSakinahRule(arabicText, i);
                if ("idghaam".equals(rule)) {
                    applyColorSpan(spannable, i, ghunnahColor);
                    continue;
                } else if ("ikhfaa".equals(rule)) {
                    applyColorSpan(spannable, i, ikhfaaColor);
                    continue;
                }
            }

            // 3. Lam in Allah (check before other rules)
            if (currentChar == LAM && isLaamInAllah(arabicText, i)) {
                applyColorSpan(spannable, i, laamAllahColor);
                continue;
            }

            // 4. Qalqalah
            if (isQalqalahLetter(currentChar) && hasSukun(arabicText, i)) {
                applyColorSpan(spannable, i, qalqalahColor);
                continue;
            }

            // 5. Madd
            String maddType = detectMaddType(arabicText, i, currentChar);
            if (maddType != null) {
                applyColorSpan(spannable, i, maddColor);
                continue;
            }

            // 6. Heavy Letters (lowest priority)
            if (isHeavyLetter(currentChar)) {
                applyColorSpan(spannable, i, heavyLetterColor);
            }
        }

        return spannable;
    }

    /**
     * Detects Noon Sakinah rules (Iqlaab, Ikhfaa, Idhaar, Idghaam).
     * Only Iqlaab and Ikhfaa are colored.
     *
     * @param text Full text
     * @param pos Position of Noon
     * @return "iqlaab", "ikhfaa", or null
     */
    private static String detectNoonSakinahRule(String text, int pos) {
        // Check if Noon has Sukun or is followed by Tanween
        if (!hasSukun(text, pos) && !hasTanween(text, pos)) {
            return null;
        }

        // Get next letter position (skip diacritics)
        int nextPos = getNextLetterPosition(text, pos);
        if (nextPos == -1) {
            return null;
        }

        char nextLetter = text.charAt(nextPos);

        // Iqlaab: Noon Sakinah + Ba
        if (nextLetter == BA) {
            return "iqlaab";
        }

        // Ikhfaa: Noon Sakinah + one of 15 concealment letters
        if (isInArray(nextLetter, IKHFAA_LETTERS)) {
            return "ikhfaa";
        }

        // Idhaar and Idghaam are not colored
        return null;
    }

    /**
     * Detects Meem Sakinah rules (Idhaar Shafawi, Idghaam Shafawi, Ikhfaa Shafawi).
     *
     * @param text Full text
     * @param pos Position of Meem
     * @return "idghaam", "ikhfaa", or null
     */
    private static String detectMeemSakinahRule(String text, int pos) {
        // Check if Meem has Sukun
        if (!hasSukun(text, pos)) {
            return null;
        }

        // Get next letter position
        int nextPos = getNextLetterPosition(text, pos);
        if (nextPos == -1) {
            return null;
        }

        char nextLetter = text.charAt(nextPos);

        // Idghaam Shafawi: Meem + Meem (with Ghunnah)
        if (nextLetter == MEEM) {
            return "idghaam";
        }

        // Ikhfaa Shafawi: Meem + Ba
        if (nextLetter == BA) {
            return "ikhfaa";
        }

        // Idhaar Shafawi: no color
        return null;
    }

    /**
     * Detects if letter is part of Madd (prolongation).
     * Madd occurs with long vowels: Alef, Waw (with Damma before), Ya (with Kasra before).
     *
     * @param text Full text
     * @param pos Position of letter
     * @param currentChar Current character
     * @return Madd type or null
     */
    private static String detectMaddType(String text, int pos, char currentChar) {
        // Alef is always a Madd letter (unless it's Alef with Hamza)
        if (currentChar == ALEF) {
            return "madd_alef";
        }

        // Waw as Madd: preceded by Damma
        if (currentChar == WAW) {
            if (pos > 0 && hasDiacriticBefore(text, pos, DAMMA)) {
                return "madd_waw";
            }
        }

        // Ya as Madd: preceded by Kasra
        if (currentChar == YA) {
            if (pos > 0 && hasDiacriticBefore(text, pos, KASRA)) {
                return "madd_ya";
            }
        }

        return null;
    }

    /**
     * Checks if the Lam is part of the word "Allah" (لله).
     *
     * @param text Full text
     * @param pos Position of first Lam
     * @return true if this is Lam in Allah
     */
    private static boolean isLaamInAllah(String text, int pos) {
        // Pattern: ل ل ه (Lam Lam Heh)
        if (pos + 1 < text.length() && text.charAt(pos + 1) == LAM) {
            // Find next letter after second Lam (skip diacritics)
            int nextPos = getNextLetterPosition(text, pos + 1);
            if (nextPos != -1 && text.charAt(nextPos) == HEH) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if character is a Qalqalah letter.
     */
    private static boolean isQalqalahLetter(char ch) {
        return isInArray(ch, QALQALAH_LETTERS);
    }

    /**
     * Checks if character is a Heavy letter (Tafkheem).
     */
    private static boolean isHeavyLetter(char ch) {
        return isInArray(ch, HEAVY_LETTERS);
    }

    /**
     * Checks if the letter at position has Sukun.
     */
    private static boolean hasSukun(String text, int pos) {
        if (pos + 1 < text.length()) {
            char next = text.charAt(pos + 1);
            return next == SUKUN;
        }
        return false;
    }

    /**
     * Checks if the letter at position is followed by Tanween.
     */
    private static boolean hasTanween(String text, int pos) {
        if (pos + 1 < text.length()) {
            char next = text.charAt(pos + 1);
            return next == TANWEEN_FATH || next == TANWEEN_DAMM || next == TANWEEN_KASR;
        }
        return false;
    }

    /**
     * Checks if there's a specific diacritic before the current position.
     */
    private static boolean hasDiacriticBefore(String text, int pos, char diacritic) {
        // Check previous character
        if (pos > 0) {
            char prev = text.charAt(pos - 1);
            if (prev == diacritic) {
                return true;
            }
        }

        // Check two positions back (in case there's a letter between)
        if (pos > 1) {
            char prevPrev = text.charAt(pos - 2);
            if (prevPrev == diacritic) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if character is a diacritic mark.
     */
    private static boolean isDiacritic(char ch) {
        return (ch >= '\u064B' && ch <= '\u0652') ||  // Main diacritics
               ch == SHADDA ||
               ch == SUPERSCRIPT_ALEF;
    }

    /**
     * Gets the position of the next letter, skipping diacritics.
     *
     * @param text Full text
     * @param currentPos Current position
     * @return Position of next letter, or -1 if not found
     */
    private static int getNextLetterPosition(String text, int currentPos) {
        for (int i = currentPos + 1; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (!isDiacritic(ch) && ch != ' ') {
                return i;
            }
        }
        return -1;
    }

    /**
     * Applies a color span to a character and its following diacritics.
     *
     * @param spannable SpannableString to modify
     * @param pos Position of character
     * @param color Color to apply
     */
    private static void applyColorSpan(SpannableString spannable, int pos, int color) {
        // Find end of character (including diacritics)
        int end = pos + 1;
        while (end < spannable.length() && isDiacritic(spannable.charAt(end))) {
            end++;
        }

        // Apply span
        spannable.setSpan(
            new ForegroundColorSpan(color),
            pos,
            end,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        );
    }

    /**
     * Checks if a character is in an array.
     */
    private static boolean isInArray(char ch, char[] array) {
        for (char c : array) {
            if (c == ch) {
                return true;
            }
        }
        return false;
    }
}
