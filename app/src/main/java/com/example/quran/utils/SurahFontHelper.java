package com.example.quran.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for managing the custom Surah names font.
 * This font contains beautiful Arabic calligraphy glyphs for all 114 Surah names.
 * Each Surah is mapped to a specific unicode character in the Private Use Area (U+E900 to U+E972).
 */
public class SurahFontHelper {

    private static Typeface surahTypeface;
    private static final Map<Integer, Integer> SURAH_UNICODE_MAP = new HashMap<>();

    static {
        // Initialize the unicode mapping for all 114 Surahs
        // Surah number -> Unicode character (in hexadecimal)
        SURAH_UNICODE_MAP.put(1, 0xe904);
        SURAH_UNICODE_MAP.put(2, 0xe905);
        SURAH_UNICODE_MAP.put(3, 0xe906);
        SURAH_UNICODE_MAP.put(4, 0xe907);
        SURAH_UNICODE_MAP.put(5, 0xe908);
        SURAH_UNICODE_MAP.put(6, 0xe90b);
        SURAH_UNICODE_MAP.put(7, 0xe90c);
        SURAH_UNICODE_MAP.put(8, 0xe90d);
        SURAH_UNICODE_MAP.put(9, 0xe90e);
        SURAH_UNICODE_MAP.put(10, 0xe90f);
        SURAH_UNICODE_MAP.put(11, 0xe910);
        SURAH_UNICODE_MAP.put(12, 0xe911);
        SURAH_UNICODE_MAP.put(13, 0xe912);
        SURAH_UNICODE_MAP.put(14, 0xe913);
        SURAH_UNICODE_MAP.put(15, 0xe914);
        SURAH_UNICODE_MAP.put(16, 0xe915);
        SURAH_UNICODE_MAP.put(17, 0xe916);
        SURAH_UNICODE_MAP.put(18, 0xe917);
        SURAH_UNICODE_MAP.put(19, 0xe918);
        SURAH_UNICODE_MAP.put(20, 0xe919);
        SURAH_UNICODE_MAP.put(21, 0xe91a);
        SURAH_UNICODE_MAP.put(22, 0xe91b);
        SURAH_UNICODE_MAP.put(23, 0xe91c);
        SURAH_UNICODE_MAP.put(24, 0xe91d);
        SURAH_UNICODE_MAP.put(25, 0xe91e);
        SURAH_UNICODE_MAP.put(26, 0xe91f);
        SURAH_UNICODE_MAP.put(27, 0xe920);
        SURAH_UNICODE_MAP.put(28, 0xe921);
        SURAH_UNICODE_MAP.put(29, 0xe922);
        SURAH_UNICODE_MAP.put(30, 0xe923);
        SURAH_UNICODE_MAP.put(31, 0xe924);
        SURAH_UNICODE_MAP.put(32, 0xe925);
        SURAH_UNICODE_MAP.put(33, 0xe926);
        SURAH_UNICODE_MAP.put(34, 0xe92e);
        SURAH_UNICODE_MAP.put(35, 0xe92f);
        SURAH_UNICODE_MAP.put(36, 0xe930);
        SURAH_UNICODE_MAP.put(37, 0xe931);
        SURAH_UNICODE_MAP.put(38, 0xe909);
        SURAH_UNICODE_MAP.put(39, 0xe90a);
        SURAH_UNICODE_MAP.put(40, 0xe927);
        SURAH_UNICODE_MAP.put(41, 0xe928);
        SURAH_UNICODE_MAP.put(42, 0xe929);
        SURAH_UNICODE_MAP.put(43, 0xe92a);
        SURAH_UNICODE_MAP.put(44, 0xe92b);
        SURAH_UNICODE_MAP.put(45, 0xe92c);
        SURAH_UNICODE_MAP.put(46, 0xe92d);
        SURAH_UNICODE_MAP.put(47, 0xe932);
        SURAH_UNICODE_MAP.put(48, 0xe902);
        SURAH_UNICODE_MAP.put(49, 0xe933);
        SURAH_UNICODE_MAP.put(50, 0xe934);
        SURAH_UNICODE_MAP.put(51, 0xe935);
        SURAH_UNICODE_MAP.put(52, 0xe936);
        SURAH_UNICODE_MAP.put(53, 0xe937);
        SURAH_UNICODE_MAP.put(54, 0xe938);
        SURAH_UNICODE_MAP.put(55, 0xe939);
        SURAH_UNICODE_MAP.put(56, 0xe93a);
        SURAH_UNICODE_MAP.put(57, 0xe93b);
        SURAH_UNICODE_MAP.put(58, 0xe93c);
        SURAH_UNICODE_MAP.put(59, 0xe900);
        SURAH_UNICODE_MAP.put(60, 0xe901);
        SURAH_UNICODE_MAP.put(61, 0xe941);
        SURAH_UNICODE_MAP.put(62, 0xe942);
        SURAH_UNICODE_MAP.put(63, 0xe943);
        SURAH_UNICODE_MAP.put(64, 0xe944);
        SURAH_UNICODE_MAP.put(65, 0xe945);
        SURAH_UNICODE_MAP.put(66, 0xe946);
        SURAH_UNICODE_MAP.put(67, 0xe947);
        SURAH_UNICODE_MAP.put(68, 0xe948);
        SURAH_UNICODE_MAP.put(69, 0xe949);
        SURAH_UNICODE_MAP.put(70, 0xe94a);
        SURAH_UNICODE_MAP.put(71, 0xe94b);
        SURAH_UNICODE_MAP.put(72, 0xe94c);
        SURAH_UNICODE_MAP.put(73, 0xe94d);
        SURAH_UNICODE_MAP.put(74, 0xe94e);
        SURAH_UNICODE_MAP.put(75, 0xe94f);
        SURAH_UNICODE_MAP.put(76, 0xe950);
        SURAH_UNICODE_MAP.put(77, 0xe951);
        SURAH_UNICODE_MAP.put(78, 0xe952);
        SURAH_UNICODE_MAP.put(79, 0xe93d);
        SURAH_UNICODE_MAP.put(80, 0xe93e);
        SURAH_UNICODE_MAP.put(81, 0xe93f);
        SURAH_UNICODE_MAP.put(82, 0xe940);
        SURAH_UNICODE_MAP.put(83, 0xe953);
        SURAH_UNICODE_MAP.put(84, 0xe954);
        SURAH_UNICODE_MAP.put(85, 0xe955);
        SURAH_UNICODE_MAP.put(86, 0xe956);
        SURAH_UNICODE_MAP.put(87, 0xe957);
        SURAH_UNICODE_MAP.put(88, 0xe958);
        SURAH_UNICODE_MAP.put(89, 0xe959);
        SURAH_UNICODE_MAP.put(90, 0xe95a);
        SURAH_UNICODE_MAP.put(91, 0xe95b);
        SURAH_UNICODE_MAP.put(92, 0xe95c);
        SURAH_UNICODE_MAP.put(93, 0xe95d);
        SURAH_UNICODE_MAP.put(94, 0xe95e);
        SURAH_UNICODE_MAP.put(95, 0xe95f);
        SURAH_UNICODE_MAP.put(96, 0xe960);
        SURAH_UNICODE_MAP.put(97, 0xe961);
        SURAH_UNICODE_MAP.put(98, 0xe962);
        SURAH_UNICODE_MAP.put(99, 0xe963);
        SURAH_UNICODE_MAP.put(100, 0xe964);
        SURAH_UNICODE_MAP.put(101, 0xe965);
        SURAH_UNICODE_MAP.put(102, 0xe966);
        SURAH_UNICODE_MAP.put(103, 0xe967);
        SURAH_UNICODE_MAP.put(104, 0xe968);
        SURAH_UNICODE_MAP.put(105, 0xe969);
        SURAH_UNICODE_MAP.put(106, 0xe96a);
        SURAH_UNICODE_MAP.put(107, 0xe96b);
        SURAH_UNICODE_MAP.put(108, 0xe96c);
        SURAH_UNICODE_MAP.put(109, 0xe96d);
        SURAH_UNICODE_MAP.put(110, 0xe96e);
        SURAH_UNICODE_MAP.put(111, 0xe96f);
        SURAH_UNICODE_MAP.put(112, 0xe970);
        SURAH_UNICODE_MAP.put(113, 0xe971);
        SURAH_UNICODE_MAP.put(114, 0xe972);
    }

    /**
     * Gets the Typeface for the Surah names font.
     * The font is loaded once and cached for performance.
     *
     * @param context Application context
     * @return Typeface for the custom Surah font
     */
    public static Typeface getTypeface(Context context) {
        if (surahTypeface == null) {
            try {
                surahTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/surah_names.ttf");
            } catch (Exception e) {
                e.printStackTrace();
                // Fallback to default typeface if font loading fails
                surahTypeface = Typeface.DEFAULT;
            }
        }
        return surahTypeface;
    }

    /**
     * Gets the unicode character for a specific Surah number.
     * Each Surah has a unique calligraphic glyph in the custom font.
     *
     * @param surahNumber Surah number (1-114)
     * @return Unicode character as a String (e.g., "\ue904" for Surah 1)
     */
    public static String getCharacter(int surahNumber) {
        Integer unicode = SURAH_UNICODE_MAP.get(surahNumber);
        if (unicode != null) {
            return String.valueOf((char) unicode.intValue());
        }
        // Fallback: return empty string if surah number is invalid
        return "";
    }

    /**
     * Checks if a Surah number is valid (1-114).
     *
     * @param surahNumber Surah number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidSurahNumber(int surahNumber) {
        return surahNumber >= 1 && surahNumber <= 114;
    }
}
