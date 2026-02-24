package com.example.quran.utils;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quran.R;
import com.example.quran.data.model.Ayah;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AyahViewHelper utility class.
 */
public class AyahViewHelperTest {

    @Mock
    private View mockBismillahView;

    @Mock
    private TextView mockTextView1;

    @Mock
    private TextView mockTextView2;

    @Mock
    private TextView mockTextView3;

    @Mock
    private ImageView mockImageView;

    @Mock
    private Typeface mockTypeface;

    @Mock
    private Ayah mockAyah;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ========== Bismillah Visibility Tests ==========

    @Test
    public void testSetBismillahVisibility_surah1Ayah1_hidden() {
        // Surah 1 (Al-Fatiha), Ayah 1 - Bismillah is part of first verse, don't show separately
        when(mockAyah.getSurahNumber()).thenReturn(1);
        when(mockAyah.getAyahNumber()).thenReturn(1);

        AyahViewHelper.setBismillahVisibility(mockBismillahView, mockAyah);

        verify(mockBismillahView).setVisibility(View.GONE);
    }

    @Test
    public void testSetBismillahVisibility_surah9Ayah1_hidden() {
        // Surah 9 (At-Tawbah), Ayah 1 - Does not begin with Bismillah
        when(mockAyah.getSurahNumber()).thenReturn(9);
        when(mockAyah.getAyahNumber()).thenReturn(1);

        AyahViewHelper.setBismillahVisibility(mockBismillahView, mockAyah);

        verify(mockBismillahView).setVisibility(View.GONE);
    }

    @Test
    public void testSetBismillahVisibility_surah2Ayah1_visible() {
        // Surah 2 (Al-Baqarah), Ayah 1 - Should show Bismillah
        when(mockAyah.getSurahNumber()).thenReturn(2);
        when(mockAyah.getAyahNumber()).thenReturn(1);

        AyahViewHelper.setBismillahVisibility(mockBismillahView, mockAyah);

        verify(mockBismillahView).setVisibility(View.VISIBLE);
    }

    @Test
    public void testSetBismillahVisibility_surah114Ayah1_visible() {
        // Surah 114 (An-Nas), Ayah 1 - Should show Bismillah
        when(mockAyah.getSurahNumber()).thenReturn(114);
        when(mockAyah.getAyahNumber()).thenReturn(1);

        AyahViewHelper.setBismillahVisibility(mockBismillahView, mockAyah);

        verify(mockBismillahView).setVisibility(View.VISIBLE);
    }

    @Test
    public void testSetBismillahVisibility_surah2Ayah2_hidden() {
        // Surah 2, Ayah 2 - Not first ayah, don't show Bismillah
        when(mockAyah.getSurahNumber()).thenReturn(2);
        when(mockAyah.getAyahNumber()).thenReturn(2);

        AyahViewHelper.setBismillahVisibility(mockBismillahView, mockAyah);

        verify(mockBismillahView).setVisibility(View.GONE);
    }

    @Test
    public void testSetBismillahVisibility_surah2Ayah286_hidden() {
        // Surah 2, Ayah 286 (last ayah) - Not first ayah, don't show Bismillah
        when(mockAyah.getSurahNumber()).thenReturn(2);
        when(mockAyah.getAyahNumber()).thenReturn(286);

        AyahViewHelper.setBismillahVisibility(mockBismillahView, mockAyah);

        verify(mockBismillahView).setVisibility(View.GONE);
    }

    @Test
    public void testSetBismillahVisibility_allSurahs() {
        // Test all 114 Surahs for Ayah 1
        for (int surahNum = 1; surahNum <= 114; surahNum++) {
            View view = org.mockito.Mockito.mock(View.class);
            Ayah ayah = org.mockito.Mockito.mock(Ayah.class);
            when(ayah.getSurahNumber()).thenReturn(surahNum);
            when(ayah.getAyahNumber()).thenReturn(1);

            AyahViewHelper.setBismillahVisibility(view, ayah);

            if (surahNum == 1 || surahNum == 9) {
                verify(view).setVisibility(View.GONE);
            } else {
                verify(view).setVisibility(View.VISIBLE);
            }
        }
    }

    // ========== Font Size Tests ==========

    @Test
    public void testApplyFontSizes_normalMultiplier() {
        float multiplier = 1.0f;

        AyahViewHelper.applyFontSizes(multiplier, mockTextView1, mockTextView2, mockTextView3);

        verify(mockTextView1).setTextSize(24f); // Arabic text
        verify(mockTextView2).setTextSize(16f); // Translation text
        verify(mockTextView3).setTextSize(24f); // Bismillah text
    }

    @Test
    public void testApplyFontSizes_largeMultiplier() {
        float multiplier = 1.5f;

        AyahViewHelper.applyFontSizes(multiplier, mockTextView1, mockTextView2, mockTextView3);

        verify(mockTextView1).setTextSize(36f); // 24 * 1.5
        verify(mockTextView2).setTextSize(24f); // 16 * 1.5
        verify(mockTextView3).setTextSize(36f); // 24 * 1.5
    }

    @Test
    public void testApplyFontSizes_smallMultiplier() {
        float multiplier = 0.75f;

        AyahViewHelper.applyFontSizes(multiplier, mockTextView1, mockTextView2, mockTextView3);

        verify(mockTextView1).setTextSize(18f); // 24 * 0.75
        verify(mockTextView2).setTextSize(12f); // 16 * 0.75
        verify(mockTextView3).setTextSize(18f); // 24 * 0.75
    }

    @Test
    public void testApplyFontSizes_twoTextViews() {
        float multiplier = 1.0f;

        AyahViewHelper.applyFontSizes(multiplier, mockTextView1, mockTextView2);

        verify(mockTextView1).setTextSize(24f);
        verify(mockTextView2).setTextSize(16f);
    }

    @Test
    public void testApplyFontSizes_oneTextView() {
        float multiplier = 1.0f;

        AyahViewHelper.applyFontSizes(multiplier, mockTextView1);

        verify(mockTextView1).setTextSize(24f);
    }

    @Test
    public void testApplyFontSizes_nullTextView() {
        float multiplier = 1.0f;

        // Should not crash with null TextView
        AyahViewHelper.applyFontSizes(multiplier, null, mockTextView2, mockTextView3);

        verify(mockTextView2).setTextSize(16f);
        verify(mockTextView3).setTextSize(24f);
    }

    @Test
    public void testApplyFontSizes_emptyArray() {
        float multiplier = 1.0f;

        // Should not crash with empty array
        AyahViewHelper.applyFontSizes(multiplier);

        // No verifications needed, just ensure no crash
    }

    // ========== Typeface Tests ==========

    @Test
    public void testApplyArabicTypeface_singleTextView() {
        AyahViewHelper.applyArabicTypeface(mockTypeface, mockTextView1);

        verify(mockTextView1).setTypeface(mockTypeface);
    }

    @Test
    public void testApplyArabicTypeface_multipleTextViews() {
        AyahViewHelper.applyArabicTypeface(mockTypeface, mockTextView1, mockTextView2, mockTextView3);

        verify(mockTextView1).setTypeface(mockTypeface);
        verify(mockTextView2).setTypeface(mockTypeface);
        verify(mockTextView3).setTypeface(mockTypeface);
    }

    @Test
    public void testApplyArabicTypeface_nullTypeface() {
        AyahViewHelper.applyArabicTypeface(null, mockTextView1, mockTextView2);

        // Should not call setTypeface when typeface is null
        verify(mockTextView1, never()).setTypeface(null);
        verify(mockTextView2, never()).setTypeface(null);
    }

    @Test
    public void testApplyArabicTypeface_nullTextView() {
        // Should not crash with null TextView
        AyahViewHelper.applyArabicTypeface(mockTypeface, null, mockTextView2, mockTextView3);

        verify(mockTextView2).setTypeface(mockTypeface);
        verify(mockTextView3).setTypeface(mockTypeface);
    }

    @Test
    public void testApplyArabicTypeface_emptyArray() {
        // Should not crash with empty array
        AyahViewHelper.applyArabicTypeface(mockTypeface);

        // No verifications needed, just ensure no crash
    }

    // ========== Bookmark Icon Tests ==========

    @Test
    public void testUpdateBookmarkIcon_bookmarked() {
        AyahViewHelper.updateBookmarkIcon(mockImageView, true);

        verify(mockImageView).setImageResource(R.drawable.ic_bookmark_filled);
    }

    @Test
    public void testUpdateBookmarkIcon_notBookmarked() {
        AyahViewHelper.updateBookmarkIcon(mockImageView, false);

        verify(mockImageView).setImageResource(R.drawable.ic_bookmark_outline);
    }

    @Test
    public void testUpdateBookmarkIcon_nullImageView() {
        // Should not crash with null ImageView
        AyahViewHelper.updateBookmarkIcon(null, true);
        AyahViewHelper.updateBookmarkIcon(null, false);

        // No verifications needed, just ensure no crash
    }

    @Test
    public void testUpdateBookmarkIcon_toggle() {
        // Test toggling bookmark state
        AyahViewHelper.updateBookmarkIcon(mockImageView, true);
        verify(mockImageView).setImageResource(R.drawable.ic_bookmark_filled);

        AyahViewHelper.updateBookmarkIcon(mockImageView, false);
        verify(mockImageView).setImageResource(R.drawable.ic_bookmark_outline);
    }

    // ========== Comment Icon Tests ==========

    @Test
    public void testUpdateCommentIcon_hasComment() {
        AyahViewHelper.updateCommentIcon(mockImageView, true);

        verify(mockImageView).setImageResource(R.drawable.ic_comment_filled);
    }

    @Test
    public void testUpdateCommentIcon_noComment() {
        AyahViewHelper.updateCommentIcon(mockImageView, false);

        verify(mockImageView).setImageResource(R.drawable.ic_comment_outline);
    }

    @Test
    public void testUpdateCommentIcon_nullImageView() {
        // Should not crash with null ImageView
        AyahViewHelper.updateCommentIcon(null, true);
        AyahViewHelper.updateCommentIcon(null, false);

        // No verifications needed, just ensure no crash
    }

    @Test
    public void testUpdateCommentIcon_toggle() {
        // Test toggling comment state
        AyahViewHelper.updateCommentIcon(mockImageView, true);
        verify(mockImageView).setImageResource(R.drawable.ic_comment_filled);

        AyahViewHelper.updateCommentIcon(mockImageView, false);
        verify(mockImageView).setImageResource(R.drawable.ic_comment_outline);
    }

    // ========== Integration Tests ==========

    @Test
    public void testCombinedUsage_typicalAyahDisplay() {
        // Test typical usage: setting up an ayah view
        when(mockAyah.getSurahNumber()).thenReturn(2);
        when(mockAyah.getAyahNumber()).thenReturn(1);
        float fontMultiplier = 1.25f;

        // Set Bismillah visibility
        AyahViewHelper.setBismillahVisibility(mockBismillahView, mockAyah);
        verify(mockBismillahView).setVisibility(View.VISIBLE);

        // Apply font sizes
        AyahViewHelper.applyFontSizes(fontMultiplier, mockTextView1, mockTextView2, mockTextView3);
        verify(mockTextView1).setTextSize(30f); // 24 * 1.25
        verify(mockTextView2).setTextSize(20f); // 16 * 1.25
        verify(mockTextView3).setTextSize(30f); // 24 * 1.25

        // Apply typeface
        AyahViewHelper.applyArabicTypeface(mockTypeface, mockTextView1, mockTextView3);
        verify(mockTextView1).setTypeface(mockTypeface);
        verify(mockTextView3).setTypeface(mockTypeface);

        // Update icons
        AyahViewHelper.updateBookmarkIcon(mockImageView, false);
        verify(mockImageView).setImageResource(R.drawable.ic_bookmark_outline);
    }
}
