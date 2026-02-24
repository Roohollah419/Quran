package com.example.quran.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for ArabicNumeralConverter utility class.
 */
public class ArabicNumeralConverterTest {

    @Test
    public void testConvertString_singleDigit() {
        assertEquals("٠", ArabicNumeralConverter.convert("0"));
        assertEquals("١", ArabicNumeralConverter.convert("1"));
        assertEquals("٢", ArabicNumeralConverter.convert("2"));
        assertEquals("٣", ArabicNumeralConverter.convert("3"));
        assertEquals("٤", ArabicNumeralConverter.convert("4"));
        assertEquals("٥", ArabicNumeralConverter.convert("5"));
        assertEquals("٦", ArabicNumeralConverter.convert("6"));
        assertEquals("٧", ArabicNumeralConverter.convert("7"));
        assertEquals("٨", ArabicNumeralConverter.convert("8"));
        assertEquals("٩", ArabicNumeralConverter.convert("9"));
    }

    @Test
    public void testConvertString_multipleDigits() {
        assertEquals("١٢٣", ArabicNumeralConverter.convert("123"));
        assertEquals("٤٥٦", ArabicNumeralConverter.convert("456"));
        assertEquals("٧٨٩", ArabicNumeralConverter.convert("789"));
        assertEquals("٩٨٧٦٥٤٣٢١٠", ArabicNumeralConverter.convert("9876543210"));
    }

    @Test
    public void testConvertString_withNonDigits() {
        assertEquals("١:٢٣", ArabicNumeralConverter.convert("1:23"));
        assertEquals("Surah ١١٤", ArabicNumeralConverter.convert("Surah 114"));
        assertEquals("Ayah ١", ArabicNumeralConverter.convert("Ayah 1"));
        assertEquals("٢٠٢٦-٠٢-٢٤", ArabicNumeralConverter.convert("2026-02-24"));
    }

    @Test
    public void testConvertString_emptyString() {
        assertEquals("", ArabicNumeralConverter.convert(""));
    }

    @Test
    public void testConvertString_nullString() {
        assertEquals("", ArabicNumeralConverter.convert((String) null));
    }

    @Test
    public void testConvertString_noDigits() {
        assertEquals("abc", ArabicNumeralConverter.convert("abc"));
        assertEquals("Hello World", ArabicNumeralConverter.convert("Hello World"));
        assertEquals("السلام عليكم", ArabicNumeralConverter.convert("السلام عليكم"));
    }

    @Test
    public void testConvertInt_positiveNumbers() {
        assertEquals("٠", ArabicNumeralConverter.convert(0));
        assertEquals("١", ArabicNumeralConverter.convert(1));
        assertEquals("٩", ArabicNumeralConverter.convert(9));
        assertEquals("١٠", ArabicNumeralConverter.convert(10));
        assertEquals("٩٩", ArabicNumeralConverter.convert(99));
        assertEquals("١٠٠", ArabicNumeralConverter.convert(100));
        assertEquals("١١٤", ArabicNumeralConverter.convert(114)); // Total number of Surahs
        assertEquals("٦٢٣٦", ArabicNumeralConverter.convert(6236)); // Total number of Ayahs
    }

    @Test
    public void testConvertInt_negativeNumbers() {
        assertEquals("-١", ArabicNumeralConverter.convert(-1));
        assertEquals("-٩٩", ArabicNumeralConverter.convert(-99));
        assertEquals("-١٢٣", ArabicNumeralConverter.convert(-123));
    }

    @Test
    public void testConvertInt_largeNumbers() {
        assertEquals("١٢٣٤٥٦٧٨٩٠", ArabicNumeralConverter.convert(1234567890));
        assertEquals("٩٩٩٩٩٩٩٩٩٩", ArabicNumeralConverter.convert("9999999999"));
    }

    @Test
    public void testConvert_resultNotNull() {
        assertNotNull(ArabicNumeralConverter.convert("123"));
        assertNotNull(ArabicNumeralConverter.convert(123));
        assertNotNull(ArabicNumeralConverter.convert((String) null));
    }

    @Test
    public void testConvert_quranAyahNumbers() {
        // Test common Quran ayah numbers
        assertEquals("١", ArabicNumeralConverter.convert(1));
        assertEquals("٢٨٦", ArabicNumeralConverter.convert(286)); // Ayahs in Surah Al-Baqarah
        assertEquals("١٧٦", ArabicNumeralConverter.convert(176)); // Ayahs in Surah An-Nisa
        assertEquals("١٢٠", ArabicNumeralConverter.convert(120)); // Ayahs in Surah Al-Imran
    }

    @Test
    public void testConvert_quranSurahNumbers() {
        // Test Surah numbers (1-114)
        assertEquals("١", ArabicNumeralConverter.convert(1)); // Al-Fatiha
        assertEquals("٢", ArabicNumeralConverter.convert(2)); // Al-Baqarah
        assertEquals("٩", ArabicNumeralConverter.convert(9)); // At-Tawbah
        assertEquals("١١٤", ArabicNumeralConverter.convert(114)); // An-Nas
    }

    @Test
    public void testConvert_withSpaces() {
        assertEquals("١ ٢ ٣", ArabicNumeralConverter.convert("1 2 3"));
        assertEquals(" ١٢٣ ", ArabicNumeralConverter.convert(" 123 "));
    }

    @Test
    public void testConvert_consistency() {
        // Ensure converting the same input always produces the same output
        String input = "123";
        String result1 = ArabicNumeralConverter.convert(input);
        String result2 = ArabicNumeralConverter.convert(input);
        assertEquals(result1, result2);

        int inputInt = 123;
        String result3 = ArabicNumeralConverter.convert(inputInt);
        String result4 = ArabicNumeralConverter.convert(inputInt);
        assertEquals(result3, result4);
    }

    @Test
    public void testConvert_roundTrip() {
        // Test that converting int to string and then converting matches direct int conversion
        int value = 123;
        String fromInt = ArabicNumeralConverter.convert(value);
        String fromString = ArabicNumeralConverter.convert(String.valueOf(value));
        assertEquals(fromInt, fromString);
    }
}
