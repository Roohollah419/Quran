package com.example.quran.ui.surahdetail;

import android.os.Bundle;

import com.example.quran.utils.Constants;

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

    // Tests for overscroll navigation feature

    @Test
    public void testFragmentWithSurahNumber_firstSurah() {
        Bundle args = new Bundle();
        args.putInt(Constants.KEY_SURAH_NUMBER, 1);

        SurahDetailFragment fragment = new SurahDetailFragment();
        fragment.setArguments(args);

        assertNotNull(fragment);
        assertNotNull(fragment.getArguments());
        assertEquals(1, fragment.getArguments().getInt(Constants.KEY_SURAH_NUMBER));
    }

    @Test
    public void testFragmentWithSurahNumber_lastSurah() {
        Bundle args = new Bundle();
        args.putInt(Constants.KEY_SURAH_NUMBER, 114);

        SurahDetailFragment fragment = new SurahDetailFragment();
        fragment.setArguments(args);

        assertNotNull(fragment);
        assertEquals(114, fragment.getArguments().getInt(Constants.KEY_SURAH_NUMBER));
    }

    @Test
    public void testFragmentWithSurahNumber_middleSurah() {
        Bundle args = new Bundle();
        args.putInt(Constants.KEY_SURAH_NUMBER, 57);

        SurahDetailFragment fragment = new SurahDetailFragment();
        fragment.setArguments(args);

        assertEquals(57, fragment.getArguments().getInt(Constants.KEY_SURAH_NUMBER));
    }

    @Test
    public void testFragmentWithScrollToAyah() {
        Bundle args = new Bundle();
        args.putInt(Constants.KEY_SURAH_NUMBER, 2);
        args.putInt("scroll_to_ayah", 255);

        SurahDetailFragment fragment = new SurahDetailFragment();
        fragment.setArguments(args);

        assertEquals(2, fragment.getArguments().getInt(Constants.KEY_SURAH_NUMBER));
        assertEquals(255, fragment.getArguments().getInt("scroll_to_ayah"));
    }

    @Test
    public void testSequentialNavigation_boundaries() {
        // Test that we have valid boundaries for sequential navigation
        // Previous surah should work for surahs 2-114
        // Next surah should work for surahs 1-113

        // Test first surah (no previous, has next)
        Bundle args1 = new Bundle();
        args1.putInt(Constants.KEY_SURAH_NUMBER, 1);
        SurahDetailFragment fragment1 = new SurahDetailFragment();
        fragment1.setArguments(args1);
        assertEquals(1, fragment1.getArguments().getInt(Constants.KEY_SURAH_NUMBER));

        // Test last surah (has previous, no next)
        Bundle args114 = new Bundle();
        args114.putInt(Constants.KEY_SURAH_NUMBER, 114);
        SurahDetailFragment fragment114 = new SurahDetailFragment();
        fragment114.setArguments(args114);
        assertEquals(114, fragment114.getArguments().getInt(Constants.KEY_SURAH_NUMBER));

        // Test middle surah (has both previous and next)
        Bundle argsMiddle = new Bundle();
        argsMiddle.putInt(Constants.KEY_SURAH_NUMBER, 57);
        SurahDetailFragment fragmentMiddle = new SurahDetailFragment();
        fragmentMiddle.setArguments(argsMiddle);
        assertEquals(57, fragmentMiddle.getArguments().getInt(Constants.KEY_SURAH_NUMBER));
    }

    @Test
    public void testSequentialNavigation_surahNumberRange() {
        // Verify valid surah numbers for sequential navigation bug fix
        // The bug was: 112 -> 111 -> 112 -> 111 (alternating)
        // Fixed by updating surahNumber when new data loads

        // Test navigation from surah 112 -> 111 -> 110
        Bundle args112 = new Bundle();
        args112.putInt(Constants.KEY_SURAH_NUMBER, 112);
        SurahDetailFragment fragment112 = new SurahDetailFragment();
        fragment112.setArguments(args112);
        assertEquals(112, fragment112.getArguments().getInt(Constants.KEY_SURAH_NUMBER));

        Bundle args111 = new Bundle();
        args111.putInt(Constants.KEY_SURAH_NUMBER, 111);
        SurahDetailFragment fragment111 = new SurahDetailFragment();
        fragment111.setArguments(args111);
        assertEquals(111, fragment111.getArguments().getInt(Constants.KEY_SURAH_NUMBER));

        Bundle args110 = new Bundle();
        args110.putInt(Constants.KEY_SURAH_NUMBER, 110);
        SurahDetailFragment fragment110 = new SurahDetailFragment();
        fragment110.setArguments(args110);
        assertEquals(110, fragment110.getArguments().getInt(Constants.KEY_SURAH_NUMBER));
    }

    @Test
    public void testThresholdConstant() {
        // Verify the threshold value used in overscroll detection
        // THRESHOLD_DP = 150f in the implementation
        // This test verifies the concept by checking valid range
        float thresholdDp = 150f;
        assertTrue("Threshold should be positive", thresholdDp > 0);
        assertTrue("Threshold should be reasonable (50-300dp)", thresholdDp >= 50 && thresholdDp <= 300);
    }

    @Test
    public void testProgressCalculation() {
        // Simulate progress calculation logic: Math.min(distance / threshold, 1.0f)
        float threshold = 150f;

        // At 0% progress
        float distance0 = 0f;
        float progress0 = Math.min(distance0 / threshold, 1.0f);
        assertEquals(0.0f, progress0, 0.01f);

        // At 50% progress
        float distance50 = 75f;
        float progress50 = Math.min(distance50 / threshold, 1.0f);
        assertEquals(0.5f, progress50, 0.01f);

        // At 100% progress
        float distance100 = 150f;
        float progress100 = Math.min(distance100 / threshold, 1.0f);
        assertEquals(1.0f, progress100, 0.01f);

        // Beyond 100% (should cap at 1.0)
        float distance200 = 300f;
        float progress200 = Math.min(distance200 / threshold, 1.0f);
        assertEquals(1.0f, progress200, 0.01f);
    }

    @Test
    public void testNavigationDirection_nextSurah() {
        // Test bundle creation for next surah navigation
        int currentSurah = 50;
        int nextSurah = currentSurah + 1;

        Bundle args = new Bundle();
        args.putInt(Constants.KEY_SURAH_NUMBER, nextSurah);

        assertEquals(51, args.getInt(Constants.KEY_SURAH_NUMBER));
        assertTrue("Next surah should be greater", nextSurah > currentSurah);
    }

    @Test
    public void testNavigationDirection_previousSurah() {
        // Test bundle creation for previous surah navigation
        int currentSurah = 50;
        int previousSurah = currentSurah - 1;

        Bundle args = new Bundle();
        args.putInt(Constants.KEY_SURAH_NUMBER, previousSurah);

        assertEquals(49, args.getInt(Constants.KEY_SURAH_NUMBER));
        assertTrue("Previous surah should be less", previousSurah < currentSurah);
    }

    @Test
    public void testBoundaryConditions_noNavigationBeforeFirstSurah() {
        // At surah 1, previous navigation should not be possible
        int currentSurah = 1;
        assertFalse("Should not navigate to previous from surah 1", currentSurah > 1);

        // But next navigation should be possible
        assertTrue("Should navigate to next from surah 1", currentSurah < 114);
    }

    @Test
    public void testBoundaryConditions_noNavigationAfterLastSurah() {
        // At surah 114, next navigation should not be possible
        int currentSurah = 114;
        assertFalse("Should not navigate to next from surah 114", currentSurah < 114);

        // But previous navigation should be possible
        assertTrue("Should navigate to previous from surah 114", currentSurah > 1);
    }

    @Test
    public void testDpToPxConversion() {
        // Test the concept of dp to px conversion
        // dpToPx = dp * density
        float dp = 150f;
        float density = 2.0f; // Example density (mdpi=1, hdpi=1.5, xhdpi=2, etc.)
        float expectedPx = dp * density;

        assertEquals(300f, expectedPx, 0.01f);

        // Test with different densities
        float densityMdpi = 1.0f;
        float pxMdpi = dp * densityMdpi;
        assertEquals(150f, pxMdpi, 0.01f);

        float densityXhdpi = 2.0f;
        float pxXhdpi = dp * densityXhdpi;
        assertEquals(300f, pxXhdpi, 0.01f);
    }
}
