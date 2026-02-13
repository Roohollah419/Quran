package com.example.quran.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConstantsTest {

    @Test
    public void testDatabaseConstants() {
        assertEquals("quran_database", Constants.DATABASE_NAME);
        assertEquals(1, Constants.DATABASE_VERSION);
    }

    @Test
    public void testBundleKeys() {
        assertEquals("surah_number", Constants.KEY_SURAH_NUMBER);
        assertEquals("surah_name", Constants.KEY_SURAH_NAME);
    }

    @Test
    public void testConstantsNotNull() {
        assertNotNull(Constants.DATABASE_NAME);
        assertNotNull(Constants.KEY_SURAH_NUMBER);
        assertNotNull(Constants.KEY_SURAH_NAME);
    }
}
