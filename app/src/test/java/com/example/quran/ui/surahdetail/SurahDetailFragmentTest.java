package com.example.quran.ui.surahdetail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class SurahDetailFragmentTest {

    private SurahDetailFragment fragment;

    @Before
    public void setup() {
        fragment = new SurahDetailFragment();
    }

    @Test
    public void testFragmentCreation() {
        assertNotNull(fragment);
    }

    @Test
    public void testFragmentIsNotNull() {
        SurahDetailFragment testFragment = new SurahDetailFragment();
        assertNotNull(testFragment);
    }

    @Test
    public void testMultipleFragmentInstances() {
        SurahDetailFragment fragment1 = new SurahDetailFragment();
        SurahDetailFragment fragment2 = new SurahDetailFragment();

        assertNotNull(fragment1);
        assertNotNull(fragment2);
        assertNotSame(fragment1, fragment2);
    }
}
