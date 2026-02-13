package com.example.quran.ui.surahlist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class SurahListFragmentTest {

    private SurahListFragment fragment;

    @Before
    public void setup() {
        fragment = new SurahListFragment();
    }

    @Test
    public void testFragmentCreation() {
        assertNotNull(fragment);
    }

    @Test
    public void testFragmentIsNotNull() {
        SurahListFragment testFragment = new SurahListFragment();
        assertNotNull(testFragment);
    }

    @Test
    public void testMultipleFragmentInstances() {
        SurahListFragment fragment1 = new SurahListFragment();
        SurahListFragment fragment2 = new SurahListFragment();

        assertNotNull(fragment1);
        assertNotNull(fragment2);
        assertNotSame(fragment1, fragment2);
    }
}
