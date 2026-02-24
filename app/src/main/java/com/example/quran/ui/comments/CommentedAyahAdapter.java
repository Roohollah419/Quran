package com.example.quran.ui.comments;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quran.R;
import com.example.quran.data.model.Ayah;
import com.example.quran.utils.CommentManager;
import com.example.quran.utils.SettingsManager;
import com.example.quran.utils.SurahFontHelper;
import com.example.quran.utils.TajweedHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecyclerView Adapter for displaying commented Ayahs with their comment text.
 */
public class CommentedAyahAdapter extends RecyclerView.Adapter<CommentedAyahAdapter.CommentedAyahViewHolder> {

    private List<Ayah> ayahs = new ArrayList<>();
    private Map<Integer, String> surahNamesEnglish = new HashMap<>();
    private Map<Integer, String> surahNamesArabic = new HashMap<>();
    private Map<String, String> commentsMap = new HashMap<>();
    private float fontSizeMultiplier;
    private Typeface arabicTypeface;
    private Context context;
    private SettingsManager settingsManager;
    private CommentManager commentManager;
    private OnCommentRemovedListener commentRemovedListener;

    public interface OnCommentRemovedListener {
        void onCommentRemoved();
    }

    public interface OnAyahClickListener {
        void onAyahClick(int surahNumber, int ayahNumber);
    }

    private OnAyahClickListener ayahClickListener;

    public CommentedAyahAdapter(float fontSizeMultiplier, Context context, OnCommentRemovedListener listener, OnAyahClickListener clickListener) {
        this.fontSizeMultiplier = fontSizeMultiplier;
        this.context = context;
        this.arabicTypeface = ResourcesCompat.getFont(context, R.font.uthmantaha);
        this.settingsManager = new SettingsManager(context);
        this.commentManager = new CommentManager(context);
        this.commentRemovedListener = listener;
        this.ayahClickListener = clickListener;
    }

    @NonNull
    @Override
    public CommentedAyahViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commented_ayah, parent, false);
        return new CommentedAyahViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentedAyahViewHolder holder, int position) {
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

    public void setSurahNamesEnglish(Map<Integer, String> surahNames) {
        this.surahNamesEnglish = surahNames;
        notifyDataSetChanged();
    }

    public void setSurahNamesArabic(Map<Integer, String> surahNames) {
        this.surahNamesArabic = surahNames;
        notifyDataSetChanged();
    }

    public void setCommentsMap(Map<String, String> comments) {
        this.commentsMap = comments;
        notifyDataSetChanged();
    }

    class CommentedAyahViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAyahArabic;
        private TextView tvAyahTranslation;
        private TextView tvBismillah;
        private TextView tvAyahAddress;
        private TextView tvCommentText;
        private ImageView ivEdit;
        private ImageView ivDelete;

        public CommentedAyahViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAyahArabic = itemView.findViewById(R.id.tvAyahArabic);
            tvAyahTranslation = itemView.findViewById(R.id.tvAyahTranslation);
            tvBismillah = itemView.findViewById(R.id.tvBismillah);
            tvAyahAddress = itemView.findViewById(R.id.tvAyahAddress);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }

        public void bind(Ayah ayah) {
            // Set click listener for the entire item
            itemView.setOnClickListener(v -> {
                if (ayahClickListener != null) {
                    ayahClickListener.onAyahClick(ayah.getSurahNumber(), ayah.getAyahNumber());
                }
            });

            // Convert ayah number to Arabic numerals
            String ayahNumber = convertToArabicNumerals(String.valueOf(ayah.getAyahNumber()));

            // Concatenate ayah text with number in Quranic ornamental brackets
            String ayahTextWithNumber = ayah.getTextArabic() + " ﴿" + ayahNumber + "﴾";

            if (settingsManager.isTajweedEnabled()) {
                CharSequence styledText = TajweedHelper.applyTajweed(
                    ayahTextWithNumber,
                    context.getColor(R.color.tajweed_ghunnah),
                    context.getColor(R.color.tajweed_iqlaab),
                    context.getColor(R.color.tajweed_ikhfaa),
                    context.getColor(R.color.tajweed_qalqalah),
                    context.getColor(R.color.tajweed_madd),
                    context.getColor(R.color.tajweed_heavy),
                    context.getColor(R.color.tajweed_laam_allah)
                );
                tvAyahArabic.setText(styledText);
            } else {
                tvAyahArabic.setText(ayahTextWithNumber);
            }
            tvAyahTranslation.setText(ayah.getTextTranslation());

            // Show Bismillah for first ayah of all surahs except Surah 1 and 9
            if (ayah.getAyahNumber() == 1 && ayah.getSurahNumber() != 1 && ayah.getSurahNumber() != 9) {
                tvBismillah.setVisibility(View.VISIBLE);
            } else {
                tvBismillah.setVisibility(View.GONE);
            }

            // Set Ayah address (only surah name, ayah number is shown in the ayah text)
            boolean isArabic = settingsManager.isArabicLanguage();
            final String surahName; // Plain text for dialog
            String surahNameDisplay; // Display text (font glyph for Arabic)

            if (isArabic) {
                // Plain text for dialog
                String tempSurahName = surahNamesArabic.get(ayah.getSurahNumber());
                if (tempSurahName == null) {
                    tempSurahName = "السورة " + convertToArabicNumerals(String.valueOf(ayah.getSurahNumber()));
                }
                surahName = tempSurahName;
                // Custom calligraphy font for display
                surahNameDisplay = SurahFontHelper.getCharacter(ayah.getSurahNumber());
                tvAyahAddress.setTypeface(SurahFontHelper.getTypeface(context));
                tvAyahAddress.setTextSize(14 * fontSizeMultiplier * 1.5f);
            } else {
                String tempSurahName = surahNamesEnglish.get(ayah.getSurahNumber());
                if (tempSurahName == null) {
                    tempSurahName = "Surah " + ayah.getSurahNumber();
                }
                surahName = tempSurahName;
                surahNameDisplay = tempSurahName;
                tvAyahAddress.setTypeface(Typeface.DEFAULT);
                tvAyahAddress.setTextSize(14 * fontSizeMultiplier);
            }

            // Show only surah name
            tvAyahAddress.setText(surahNameDisplay);

            // Set comment text
            String commentKey = ayah.getSurahNumber() + ":" + ayah.getAyahNumber();
            String comment = commentsMap.get(commentKey);
            if (comment != null) {
                tvCommentText.setText(comment);
            } else {
                tvCommentText.setText("");
            }

            // Apply font size
            tvAyahArabic.setTextSize(24 * fontSizeMultiplier);
            tvAyahTranslation.setTextSize(16 * fontSizeMultiplier);
            tvBismillah.setTextSize(24 * fontSizeMultiplier);
            // tvAyahAddress font size is set above based on language
            tvCommentText.setTextSize(14 * fontSizeMultiplier);

            // Apply Uthman Taha Naskh font to Arabic text
            if (arabicTypeface != null) {
                tvAyahArabic.setTypeface(arabicTypeface);
                tvBismillah.setTypeface(arabicTypeface);
            }

            // Handle edit click
            ivEdit.setOnClickListener(v -> {
                if (context instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) context;
                    CommentDialogFragment dialog = CommentDialogFragment.newInstance(
                            ayah.getSurahNumber(),
                            ayah.getAyahNumber(),
                            surahName,
                            comment,
                            (surahNum, ayahNum, commentText) -> {
                                if (commentRemovedListener != null) {
                                    commentRemovedListener.onCommentRemoved();
                                }
                            },
                            () -> {}
                    );
                    dialog.show(activity.getSupportFragmentManager(), "CommentDialog");
                }
            });

            // Handle delete click
            ivDelete.setOnClickListener(v -> {
                commentManager.removeComment(ayah.getSurahNumber(), ayah.getAyahNumber());

                // Notify listener that comment was removed
                if (commentRemovedListener != null) {
                    commentRemovedListener.onCommentRemoved();
                }
            });
        }

        private String convertToArabicNumerals(String number) {
            char[] arabicNumerals = {'٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'};
            StringBuilder result = new StringBuilder();
            for (char c : number.toCharArray()) {
                if (Character.isDigit(c)) {
                    result.append(arabicNumerals[c - '0']);
                } else {
                    result.append(c);
                }
            }
            return result.toString();
        }
    }
}
