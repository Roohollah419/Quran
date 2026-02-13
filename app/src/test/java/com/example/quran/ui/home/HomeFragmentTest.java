package com.example.quran.ui.home;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class HomeFragmentTest {

    private HomeFragment fragment;

    @Before
    public void setup() {
        fragment = new HomeFragment();
    }

    @Test
    public void testFragmentCreation() {
        assertNotNull(fragment);
    }

    @Test
    public void testFragmentIsNotNull() {
        HomeFragment testFragment = new HomeFragment();
        assertNotNull(testFragment);
    }

    @Test
    public void testMultipleFragmentInstances() {
        HomeFragment fragment1 = new HomeFragment();
        HomeFragment fragment2 = new HomeFragment();

        assertNotNull(fragment1);
        assertNotNull(fragment2);
        assertNotSame(fragment1, fragment2);
    }
}
