package com.example.quran.ui.comments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.quran.R;
import com.example.quran.utils.CommentManager;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Dialog fragment for adding/editing comments on Ayahs.
 */
public class CommentDialogFragment extends DialogFragment {

    private static final String ARG_SURAH_NUMBER = "surah_number";
    private static final String ARG_AYAH_NUMBER = "ayah_number";
    private static final String ARG_EXISTING_COMMENT = "existing_comment";
    private static final String ARG_SURAH_NAME = "surah_name";

    private int surahNumber;
    private int ayahNumber;
    private String existingComment;
    private String surahName;

    private CommentManager commentManager;
    private TextView tvTitle;
    private TextView tvAyahInfo;
    private TextInputEditText etComment;
    private Button btnSave;
    private Button btnDelete;
    private ImageButton btnClose;

    private OnCommentSavedListener commentSavedListener;
    private OnDismissListener dismissListener;

    public interface OnCommentSavedListener {
        void onCommentSaved(int surahNumber, int ayahNumber, String commentText);
    }

    public interface OnCommentDeletedListener {
        void onCommentDeleted(int surahNumber, int ayahNumber);
    }

    public interface OnDismissListener {
        void onDialogDismissed();
    }

    public static CommentDialogFragment newInstance(
            int surahNumber,
            int ayahNumber,
            String surahName,
            String existingComment,
            OnCommentSavedListener saveListener,
            OnDismissListener dismissListener) {
        CommentDialogFragment fragment = new CommentDialogFragment();
        fragment.commentSavedListener = saveListener;
        fragment.dismissListener = dismissListener;

        Bundle args = new Bundle();
        args.putInt(ARG_SURAH_NUMBER, surahNumber);
        args.putInt(ARG_AYAH_NUMBER, ayahNumber);
        args.putString(ARG_SURAH_NAME, surahName);
        if (existingComment != null) {
            args.putString(ARG_EXISTING_COMMENT, existingComment);
        }
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
        commentManager = new CommentManager(requireContext());

        if (getArguments() != null) {
            surahNumber = getArguments().getInt(ARG_SURAH_NUMBER);
            ayahNumber = getArguments().getInt(ARG_AYAH_NUMBER);
            surahName = getArguments().getString(ARG_SURAH_NAME, "");
            existingComment = getArguments().getString(ARG_EXISTING_COMMENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tvTitle = view.findViewById(R.id.tvTitle);
        tvAyahInfo = view.findViewById(R.id.tvAyahInfo);
        etComment = view.findViewById(R.id.etComment);
        btnSave = view.findViewById(R.id.btnSave);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnClose = view.findViewById(R.id.btnClose);

        // Set dialog title
        boolean isEditing = !TextUtils.isEmpty(existingComment);
        tvTitle.setText(isEditing ? R.string.edit_comment : R.string.add_comment);

        // Set ayah info
        String ayahInfo = surahName + " - Ayah " + ayahNumber;
        tvAyahInfo.setText(ayahInfo);

        // Pre-fill existing comment if editing
        if (isEditing) {
            etComment.setText(existingComment);
            etComment.setSelection(existingComment.length()); // Move cursor to end
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }

        // Setup listeners
        setupListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );

            // Add slide up animation
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            // Remove elevation/shadow
            dialog.getWindow().setElevation(0);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onDialogDismissed();
        }
    }

    private void setupListeners() {
        btnClose.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> saveComment());

        btnDelete.setOnClickListener(v -> deleteComment());
    }

    private void saveComment() {
        String commentText = etComment.getText() != null ? etComment.getText().toString().trim() : "";

        if (commentText.isEmpty()) {
            Toast.makeText(requireContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save comment
        commentManager.addComment(surahNumber, ayahNumber, commentText);

        // Notify listener
        if (commentSavedListener != null) {
            commentSavedListener.onCommentSaved(surahNumber, ayahNumber, commentText);
        }

        Toast.makeText(requireContext(), R.string.comment_saved, Toast.LENGTH_SHORT).show();
        dismiss();
    }

    private void deleteComment() {
        // Delete comment
        commentManager.removeComment(surahNumber, ayahNumber);

        // Notify listener
        if (commentSavedListener != null) {
            commentSavedListener.onCommentSaved(surahNumber, ayahNumber, null);
        }

        Toast.makeText(requireContext(), R.string.comment_deleted, Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
