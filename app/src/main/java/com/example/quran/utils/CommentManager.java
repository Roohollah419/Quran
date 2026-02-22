package com.example.quran.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager for handling Ayah comments using SharedPreferences with JSON serialization.
 */
public class CommentManager {
    private static final String PREFS_NAME = "QuranComments";
    private static final String KEY_COMMENTS = "comments";

    private final SharedPreferences prefs;
    private final Gson gson;

    public CommentManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    /**
     * Add or update a comment for an Ayah.
     * @param surahNumber The Surah number (1-114)
     * @param ayahNumber The Ayah number within the Surah
     * @param commentText The comment text
     */
    public void addComment(int surahNumber, int ayahNumber, String commentText) {
        if (commentText == null || commentText.trim().isEmpty()) {
            removeComment(surahNumber, ayahNumber);
            return;
        }

        Map<String, String> comments = getAllComments();
        comments.put(createCommentKey(surahNumber, ayahNumber), commentText.trim());
        saveComments(comments);
    }

    /**
     * Remove a comment for an Ayah.
     * @param surahNumber The Surah number (1-114)
     * @param ayahNumber The Ayah number within the Surah
     */
    public void removeComment(int surahNumber, int ayahNumber) {
        Map<String, String> comments = getAllComments();
        comments.remove(createCommentKey(surahNumber, ayahNumber));
        saveComments(comments);
    }

    /**
     * Get the comment for an Ayah.
     * @param surahNumber The Surah number (1-114)
     * @param ayahNumber The Ayah number within the Surah
     * @return The comment text, or null if no comment exists
     */
    public String getComment(int surahNumber, int ayahNumber) {
        Map<String, String> comments = getAllComments();
        return comments.get(createCommentKey(surahNumber, ayahNumber));
    }

    /**
     * Check if an Ayah has a comment.
     * @param surahNumber The Surah number (1-114)
     * @param ayahNumber The Ayah number within the Surah
     * @return true if comment exists, false otherwise
     */
    public boolean hasComment(int surahNumber, int ayahNumber) {
        Map<String, String> comments = getAllComments();
        return comments.containsKey(createCommentKey(surahNumber, ayahNumber));
    }

    /**
     * Get all comments.
     * @return Map of comment keys to comment text in format "surahNumber:ayahNumber" -> "comment text"
     */
    public Map<String, String> getAllComments() {
        String json = prefs.getString(KEY_COMMENTS, null);
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }

        try {
            Type type = new TypeToken<HashMap<String, String>>(){}.getType();
            Map<String, String> comments = gson.fromJson(json, type);
            return comments != null ? comments : new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * Get the total count of comments.
     * @return Number of comments
     */
    public int getCommentCount() {
        return getAllComments().size();
    }

    /**
     * Clear all comments.
     */
    public void clearAllComments() {
        prefs.edit().remove(KEY_COMMENTS).commit();
    }

    private void saveComments(Map<String, String> comments) {
        String json = gson.toJson(comments);
        prefs.edit().putString(KEY_COMMENTS, json).commit();
    }

    private String createCommentKey(int surahNumber, int ayahNumber) {
        return surahNumber + ":" + ayahNumber;
    }

    /**
     * Parse a comment key to get Surah and Ayah numbers.
     * @param commentKey The comment key in format "surahNumber:ayahNumber"
     * @return int array with [surahNumber, ayahNumber], or null if invalid
     */
    public static int[] parseCommentKey(String commentKey) {
        if (commentKey == null || commentKey.isEmpty()) {
            return null;
        }
        try {
            String[] parts = commentKey.split(":");
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
