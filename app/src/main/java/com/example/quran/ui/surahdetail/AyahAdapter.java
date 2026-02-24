package com.example.quran.ui.surahdetail;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quran.R;
import com.example.quran.data.model.Ayah;
import com.example.quran.ui.comments.CommentDialogFragment;
import com.example.quran.utils.ArabicNumeralConverter;
import com.example.quran.utils.AyahTextFormatter;
import com.example.quran.utils.AyahViewHelper;
import com.example.quran.utils.BookmarkManager;
import com.example.quran.utils.CommentManager;
import com.example.quran.utils.SettingsManager;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for displaying Ayahs (verses).
 */
public class AyahAdapter extends RecyclerView.Adapter<AyahAdapter.AyahViewHolder> {

    private List<Ayah> ayahs = new ArrayList<>();
    private float fontSizeMultiplier;
    private Typeface arabicTypeface;
    private Context context;
    private SettingsManager settingsManager;
    private BookmarkManager bookmarkManager;
    private CommentManager commentManager;
    private String surahName = "";
    private OnCreateImageClickListener createImageListener;

    /**
     * Interface for handling create image button clicks.
     */
    public interface OnCreateImageClickListener {
        void onCreateImageClick(Ayah ayah);
    }

    public AyahAdapter(float fontSizeMultiplier, Context context) {
        this.fontSizeMultiplier = fontSizeMultiplier;
        this.context = context;
        this.arabicTypeface = ResourcesCompat.getFont(context, R.font.uthmantaha);
        this.settingsManager = new SettingsManager(context);
        this.bookmarkManager = new BookmarkManager(context);
        this.commentManager = new CommentManager(context);
    }

    public void setSurahName(String surahName) {
        this.surahName = surahName;
    }

    public void setOnCreateImageClickListener(OnCreateImageClickListener listener) {
        this.createImageListener = listener;
    }

    @NonNull
    @Override
    public AyahViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ayah, parent, false);
        return new AyahViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AyahViewHolder holder, int position) {
        Ayah ayah = ayahs.get(position);
        holder.bind(ayah);
    }

    @Override
    public int getItemCount() {
        return ayahs.size();
    }

    public void setAyahs(List<Ayah> ayahs) {
        this.ayahs = ayahs;
        notifyDataSetChanged();
    }

    class AyahViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAyahArabic;
        private TextView tvAyahTranslation;
        private TextView tvBismillah;
        private ImageView ivShare;
        private ImageView ivCreateImage;
        private ImageView ivComment;
        private ImageView ivBookmark;

        public AyahViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAyahArabic = itemView.findViewById(R.id.tvAyahArabic);
            tvAyahTranslation = itemView.findViewById(R.id.tvAyahTranslation);
            tvBismillah = itemView.findViewById(R.id.tvBismillah);
            ivShare = itemView.findViewById(R.id.ivShare);
            ivCreateImage = itemView.findViewById(R.id.ivCreateImage);
            ivComment = itemView.findViewById(R.id.ivComment);
            ivBookmark = itemView.findViewById(R.id.ivBookmark);
        }

        public void bind(Ayah ayah) {
            // Format and style ayah text with number and Tajweed
            CharSequence formattedText = AyahTextFormatter.formatAndStyleAyah(
                ayah.getTextArabic(), ayah.getAyahNumber(), context, settingsManager);
            tvAyahArabic.setText(formattedText);
            tvAyahTranslation.setText(ayah.getTextTranslation());

            // Show Bismillah for first ayah of all surahs except Surah 1 and 9
            AyahViewHelper.setBismillahVisibility(tvBismillah, ayah);

            // Apply font sizes
            AyahViewHelper.applyFontSizes(fontSizeMultiplier,
                tvAyahArabic, tvAyahTranslation, tvBismillah);

            // Apply Uthman Taha Naskh font to Arabic text
            AyahViewHelper.applyArabicTypeface(arabicTypeface, tvAyahArabic, tvBismillah);

            // Handle share click
            ivShare.setOnClickListener(v -> {
                // Format share text: Arabic + Translation + Ayah address + App info
                String shareText = String.format(
                        "%s\n\n%s\n\n%s (%d)\n\nShared from %s\n%s",
                        ayah.getTextArabic(),
                        ayah.getTextTranslation(),
                        surahName,
                        ayah.getAyahNumber(),
                        context.getString(R.string.app_name),
                        "https://github.com/Roohollah419/Quran"
                );

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_ayah)));
            });

            // Handle create image click
            ivCreateImage.setOnClickListener(v -> {
                if (createImageListener != null) {
                    createImageListener.onCreateImageClick(ayah);
                }
            });

            // Set comment icon based on current state
            updateCommentIcon(ayah.getSurahNumber(), ayah.getAyahNumber());

            // Handle comment click
            ivComment.setOnClickListener(v -> {
                if (context instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) context;
                    String existingComment = commentManager.getComment(ayah.getSurahNumber(), ayah.getAyahNumber());
                    CommentDialogFragment dialog = CommentDialogFragment.newInstance(
                            ayah.getSurahNumber(),
                            ayah.getAyahNumber(),
                            surahName,
                            existingComment,
                            (surahNum, ayahNum, commentText) -> {
                                // Update icon after comment is saved or deleted
                                updateCommentIcon(ayah.getSurahNumber(), ayah.getAyahNumber());
                            },
                            () -> {}
                    );
                    dialog.show(activity.getSupportFragmentManager(), "CommentDialog");
                }
            });

            // Set bookmark icon based on current state
            updateBookmarkIcon(ayah.getSurahNumber(), ayah.getAyahNumber());

            // Handle bookmark click
            ivBookmark.setOnClickListener(v -> {
                boolean isBookmarked = bookmarkManager.toggleBookmark(ayah.getSurahNumber(), ayah.getAyahNumber());
                updateBookmarkIcon(ayah.getSurahNumber(), ayah.getAyahNumber());
            });
        }

        private void updateCommentIcon(int surahNumber, int ayahNumber) {
            boolean hasComment = commentManager.hasComment(surahNumber, ayahNumber);
            AyahViewHelper.updateCommentIcon(ivComment, hasComment);
        }

        private void updateBookmarkIcon(int surahNumber, int ayahNumber) {
            boolean isBookmarked = bookmarkManager.isBookmarked(surahNumber, ayahNumber);
            AyahViewHelper.updateBookmarkIcon(ivBookmark, isBookmarked);
        }
    }
}
