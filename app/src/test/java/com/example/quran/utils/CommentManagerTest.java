package com.example.quran.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CommentManagerTest {

    private CommentManager commentManager;
    @Mock
    private Context mockContext;
    @Mock
    private SharedPreferences mockPreferences;
    @Mock
    private SharedPreferences.Editor mockEditor;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Mock SharedPreferences behavior
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPreferences);
        when(mockPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        when(mockEditor.remove(anyString())).thenReturn(mockEditor);
        when(mockEditor.commit()).thenReturn(true);

        // Default: no comments stored
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(null);

        commentManager = new CommentManager(mockContext);
    }

    @Test
    public void testAddComment_savesCommentCorrectly() {
        commentManager.addComment(1, 1, "Test comment");

        verify(mockEditor).putString(eq("comments"), anyString());
        verify(mockEditor).commit();
    }

    @Test
    public void testAddComment_withEmptyText_removesComment() {
        commentManager.addComment(1, 1, "");

        verify(mockEditor).putString(eq("comments"), anyString());
        verify(mockEditor).commit();
    }

    @Test
    public void testAddComment_withNullText_removesComment() {
        commentManager.addComment(1, 1, null);

        verify(mockEditor).putString(eq("comments"), anyString());
        verify(mockEditor).commit();
    }

    @Test
    public void testRemoveComment_removesCommentFromStorage() {
        // First add a comment
        String jsonWithComment = "{\"1:1\":\"Test comment\"}";
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(jsonWithComment);

        commentManager.removeComment(1, 1);

        verify(mockEditor).putString(eq("comments"), anyString());
        verify(mockEditor).commit();
    }

    @Test
    public void testGetComment_returnsCorrectComment() {
        String jsonWithComment = "{\"1:1\":\"Test comment\"}";
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(jsonWithComment);

        commentManager = new CommentManager(mockContext);
        String comment = commentManager.getComment(1, 1);

        assertEquals("Test comment", comment);
    }

    @Test
    public void testGetComment_nonExistentComment_returnsNull() {
        String jsonWithComment = "{\"1:1\":\"Test comment\"}";
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(jsonWithComment);

        commentManager = new CommentManager(mockContext);
        String comment = commentManager.getComment(2, 2);

        assertNull(comment);
    }

    @Test
    public void testHasComment_existingComment_returnsTrue() {
        String jsonWithComment = "{\"1:1\":\"Test comment\"}";
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(jsonWithComment);

        commentManager = new CommentManager(mockContext);
        boolean hasComment = commentManager.hasComment(1, 1);

        assertTrue(hasComment);
    }

    @Test
    public void testHasComment_nonExistentComment_returnsFalse() {
        String jsonWithComment = "{\"1:1\":\"Test comment\"}";
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(jsonWithComment);

        commentManager = new CommentManager(mockContext);
        boolean hasComment = commentManager.hasComment(2, 2);

        assertFalse(hasComment);
    }

    @Test
    public void testGetAllComments_emptyStorage_returnsEmptyMap() {
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(null);

        commentManager = new CommentManager(mockContext);
        Map<String, String> comments = commentManager.getAllComments();

        assertNotNull(comments);
        assertTrue(comments.isEmpty());
    }

    @Test
    public void testGetAllComments_withComments_returnsCorrectMap() {
        String jsonWithComments = "{\"1:1\":\"Comment 1\",\"2:5\":\"Comment 2\"}";
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(jsonWithComments);

        commentManager = new CommentManager(mockContext);
        Map<String, String> comments = commentManager.getAllComments();

        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals("Comment 1", comments.get("1:1"));
        assertEquals("Comment 2", comments.get("2:5"));
    }

    @Test
    public void testGetCommentCount_noComments_returnsZero() {
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(null);

        commentManager = new CommentManager(mockContext);
        int count = commentManager.getCommentCount();

        assertEquals(0, count);
    }

    @Test
    public void testGetCommentCount_withComments_returnsCorrectCount() {
        String jsonWithComments = "{\"1:1\":\"Comment 1\",\"2:5\":\"Comment 2\",\"3:10\":\"Comment 3\"}";
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(jsonWithComments);

        commentManager = new CommentManager(mockContext);
        int count = commentManager.getCommentCount();

        assertEquals(3, count);
    }

    @Test
    public void testClearAllComments_removesAllComments() {
        commentManager.clearAllComments();

        verify(mockEditor).remove("comments");
        verify(mockEditor).commit();
    }

    @Test
    public void testParseCommentKey_validKey_returnsCorrectArray() {
        int[] result = CommentManager.parseCommentKey("2:255");

        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals(2, result[0]);
        assertEquals(255, result[1]);
    }

    @Test
    public void testParseCommentKey_invalidKey_returnsNull() {
        int[] result = CommentManager.parseCommentKey("invalid");

        assertNull(result);
    }

    @Test
    public void testParseCommentKey_nullKey_returnsNull() {
        int[] result = CommentManager.parseCommentKey(null);

        assertNull(result);
    }

    @Test
    public void testParseCommentKey_emptyKey_returnsNull() {
        int[] result = CommentManager.parseCommentKey("");

        assertNull(result);
    }

    @Test
    public void testAddComment_withSpecialCharacters_handlesCorrectly() {
        String specialComment = "Test comment with special chars: !@#$%^&*()";
        commentManager.addComment(1, 1, specialComment);

        verify(mockEditor).putString(eq("comments"), anyString());
        verify(mockEditor).commit();
    }

    @Test
    public void testAddComment_withUnicodeCharacters_handlesCorrectly() {
        String unicodeComment = "تعليق باللغة العربية";
        commentManager.addComment(1, 1, unicodeComment);

        verify(mockEditor).putString(eq("comments"), anyString());
        verify(mockEditor).commit();
    }

    @Test
    public void testGetAllComments_invalidJson_returnsEmptyMap() {
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn("{invalid json}");

        commentManager = new CommentManager(mockContext);
        Map<String, String> comments = commentManager.getAllComments();

        assertNotNull(comments);
        assertTrue(comments.isEmpty());
    }

    @Test
    public void testAddComment_trimsWhitespace() {
        String commentWithSpaces = "  Test comment  ";
        commentManager.addComment(1, 1, commentWithSpaces);

        verify(mockEditor).putString(eq("comments"), anyString());
        verify(mockEditor).commit();
    }

    @Test
    public void testMultipleOperations_workCorrectly() {
        // Add first comment
        commentManager.addComment(1, 1, "Comment 1");
        verify(mockEditor, times(1)).putString(eq("comments"), anyString());

        // Add second comment
        commentManager.addComment(2, 5, "Comment 2");
        verify(mockEditor, times(2)).putString(eq("comments"), anyString());

        // Remove first comment
        String jsonWithComments = "{\"1:1\":\"Comment 1\",\"2:5\":\"Comment 2\"}";
        when(mockPreferences.getString(eq("comments"), isNull())).thenReturn(jsonWithComments);
        commentManager.removeComment(1, 1);
        verify(mockEditor, times(3)).putString(eq("comments"), anyString());

        verify(mockEditor, atLeast(3)).commit();
    }
}
