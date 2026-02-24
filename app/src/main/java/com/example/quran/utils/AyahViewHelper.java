package com.example.quran.utils;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quran.R;
import com.example.quran.data.model.Ayah;

/**
 * Utility class for common UI binding logic for Ayah views.
 * <p>
 * Provides helper methods for:
 * - Bismillah visibility based on Quranic rules
 * - Font size application
 * - Arabic typeface application
 * - Bookmark and comment icon updates
 * </p>
 */
public class AyahViewHelper {

    /**
     * Show or hide Bismillah based on Quranic rules.
     * <p>
     * Bismillah appears at the start of all Surahs except:
     * - Surah 1 (Al-Fatiha) - Bismillah is part of the first verse
     * - Surah 9 (At-Tawbah) - Does not begin with Bismillah
     * </p>
     *
     * @param bismillahView The View containing Bismillah text
     * @param ayah          The Ayah to check
     */
    public static void setBismillahVisibility(View bismillahView, Ayah ayah) {
        if (ayah.getAyahNumber() == 1 &&
            ayah.getSurahNumber() != 1 &&
            ayah.getSurahNumber() != 9) {
            bismillahView.setVisibility(View.VISIBLE);
        } else {
            bismillahView.setVisibility(View.GONE);
        }
    }

    /**
     * Apply font size multiplier to TextViews.
     * <p>
     * Base font sizes:
     * - Arabic text: 24sp
     * - Translation text: 16sp
     * - Bismillah text: 24sp
     * </p>
     *
     * @param multiplier Font size multiplier (e.g., 1.0 for medium, 1.25 for large)
     * @param textViews  TextViews to apply font sizes to (in order: Arabic, Translation, Bismillah)
     */
    public static void applyFontSizes(float multiplier, TextView... textViews) {
        float[] baseSizes = {24f, 16f, 24f}; // Arabic, Translation, Bismillah
        for (int i = 0; i < Math.min(textViews.length, baseSizes.length); i++) {
            if (textViews[i] != null) {
                textViews[i].setTextSize(baseSizes[i] * multiplier);
            }
        }
    }

    /**
     * Apply Arabic typeface to TextViews.
     * <p>
     * Typically used for Arabic text and Bismillah views.
     * </p>
     *
     * @param typeface  The Arabic typeface to apply (e.g., Uthman Taha)
     * @param textViews TextViews to apply the typeface to
     */
    public static void applyArabicTypeface(Typeface typeface, TextView... textViews) {
        if (typeface != null) {
            for (TextView tv : textViews) {
                if (tv != null) {
                    tv.setTypeface(typeface);
                }
            }
        }
    }

    /**
     * Update bookmark icon based on bookmark state.
     *
     * @param iconView     The ImageView containing the bookmark icon
     * @param isBookmarked True if the ayah is bookmarked, false otherwise
     */
    public static void updateBookmarkIcon(ImageView iconView, boolean isBookmarked) {
        if (iconView != null) {
            iconView.setImageResource(isBookmarked
                ? R.drawable.ic_bookmark_filled
                : R.drawable.ic_bookmark_outline);
        }
    }

    /**
     * Update comment icon based on comment existence.
     *
     * @param iconView   The ImageView containing the comment icon
     * @param hasComment True if the ayah has a comment, false otherwise
     */
    public static void updateCommentIcon(ImageView iconView, boolean hasComment) {
        if (iconView != null) {
            iconView.setImageResource(hasComment
                ? R.drawable.ic_comment_filled
                : R.drawable.ic_comment_outline);
        }
    }
}
