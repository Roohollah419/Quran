package com.example.quran.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Manager for handling Ayah bookmarks using SharedPreferences.
 */
public class BookmarkManager {
    private static final String PREFS_NAME = "QuranBookmarks";
    private static final String KEY_BOOKMARKS = "bookmarks";

    private final SharedPreferences prefs;

    public BookmarkManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Add a bookmark for an Ayah.
     * @param surahNumber The Surah number (1-114)
     * @param ayahNumber The Ayah number within the Surah
     */
    public void addBookmark(int surahNumber, int ayahNumber) {
        Set<String> bookmarks = getBookmarks();
        bookmarks.add(createBookmarkKey(surahNumber, ayahNumber));
        saveBookmarks(bookmarks);
    }

    /**
     * Remove a bookmark for an Ayah.
     * @param surahNumber The Surah number (1-114)
     * @param ayahNumber The Ayah number within the Surah
     */
    public void removeBookmark(int surahNumber, int ayahNumber) {
        Set<String> bookmarks = getBookmarks();
        bookmarks.remove(createBookmarkKey(surahNumber, ayahNumber));
        saveBookmarks(bookmarks);
    }

    /**
     * Check if an Ayah is bookmarked.
     * @param surahNumber The Surah number (1-114)
     * @param ayahNumber The Ayah number within the Surah
     * @return true if bookmarked, false otherwise
     */
    public boolean isBookmarked(int surahNumber, int ayahNumber) {
        Set<String> bookmarks = getBookmarks();
        return bookmarks.contains(createBookmarkKey(surahNumber, ayahNumber));
    }

    /**
     * Toggle bookmark state for an Ayah.
     * @param surahNumber The Surah number (1-114)
     * @param ayahNumber The Ayah number within the Surah
     * @return true if now bookmarked, false if now unbookmarked
     */
    public boolean toggleBookmark(int surahNumber, int ayahNumber) {
        if (isBookmarked(surahNumber, ayahNumber)) {
            removeBookmark(surahNumber, ayahNumber);
            return false;
        } else {
            addBookmark(surahNumber, ayahNumber);
            return true;
        }
    }

    /**
     * Get all bookmarks.
     * @return Set of bookmark keys in format "surahNumber:ayahNumber"
     */
    public Set<String> getBookmarks() {
        return new HashSet<>(prefs.getStringSet(KEY_BOOKMARKS, new HashSet<>()));
    }

    /**
     * Get the total count of bookmarks.
     * @return Number of bookmarks
     */
    public int getBookmarkCount() {
        return getBookmarks().size();
    }

    /**
     * Clear all bookmarks.
     */
    public void clearAllBookmarks() {
        prefs.edit().remove(KEY_BOOKMARKS).apply();
    }

    private void saveBookmarks(Set<String> bookmarks) {
        prefs.edit().putStringSet(KEY_BOOKMARKS, bookmarks).apply();
    }

    private String createBookmarkKey(int surahNumber, int ayahNumber) {
        return surahNumber + ":" + ayahNumber;
    }

    /**
     * Parse a bookmark key to get Surah and Ayah numbers.
     * @param bookmarkKey The bookmark key in format "surahNumber:ayahNumber"
     * @return int array with [surahNumber, ayahNumber], or null if invalid
     */
    public static int[] parseBookmarkKey(String bookmarkKey) {
        try {
            String[] parts = bookmarkKey.split(":");
            if (parts.length == 2) {
                int surahNumber = Integer.parseInt(parts[0]);
                int ayahNumber = Integer.parseInt(parts[1]);
                return new int[]{surahNumber, ayahNumber};
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
