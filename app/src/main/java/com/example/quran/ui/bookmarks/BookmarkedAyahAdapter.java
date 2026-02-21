package com.example.quran.ui.bookmarks;

import android.content.Context;
import android.graphics.Typeface;
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
import com.example.quran.utils.BookmarkManager;
import com.example.quran.utils.SettingsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecyclerView Adapter for displaying bookmarked Ayahs with their addresses.
 */
public class BookmarkedAyahAdapter extends RecyclerView.Adapter<BookmarkedAyahAdapter.BookmarkedAyahViewHolder> {

    private List<Ayah> ayahs = new ArrayList<>();
    private Map<Integer, String> surahNamesEnglish = new HashMap<>();
    private Map<Integer, String> surahNamesArabic = new HashMap<>();
    private float fontSizeMultiplier;
    private Typeface arabicTypeface;
    private Context context;
    private SettingsManager settingsManager;
    private BookmarkManager bookmarkManager;
    private OnBookmarkRemovedListener bookmarkRemovedListener;

    public interface OnBookmarkRemovedListener {
        void onBookmarkRemoved();
    }

    public interface OnAyahClickListener {
        void onAyahClick(int surahNumber, int ayahNumber);
    }

    private OnAyahClickListener ayahClickListener;

    public BookmarkedAyahAdapter(float fontSizeMultiplier, Context context, OnBookmarkRemovedListener listener, OnAyahClickListener clickListener) {
        this.fontSizeMultiplier = fontSizeMultiplier;
        this.context = context;
        this.arabicTypeface = ResourcesCompat.getFont(context, R.font.uthmantaha);
        this.settingsManager = new SettingsManager(context);
        this.bookmarkManager = new BookmarkManager(context);
        this.bookmarkRemovedListener = listener;
        this.ayahClickListener = clickListener;
    }

    @NonNull
    @Override
    public BookmarkedAyahViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmarked_ayah, parent, false);
        return new BookmarkedAyahViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkedAyahViewHolder holder, int position) {
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

    class BookmarkedAyahViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAyahArabic;
        private TextView tvAyahTranslation;
        private TextView tvBismillah;
        private TextView tvAyahAddress;
        private ImageView ivBookmark;

        public BookmarkedAyahViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAyahArabic = itemView.findViewById(R.id.tvAyahArabic);
            tvAyahTranslation = itemView.findViewById(R.id.tvAyahTranslation);
            tvBismillah = itemView.findViewById(R.id.tvBismillah);
            tvAyahAddress = itemView.findViewById(R.id.tvAyahAddress);
            ivBookmark = itemView.findViewById(R.id.ivBookmark);
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

            // Concatenate ayah text with number in parentheses
            String ayahTextWithNumber = ayah.getTextArabic() + " (" + ayahNumber + ")";
            tvAyahArabic.setText(ayahTextWithNumber);
            tvAyahTranslation.setText(ayah.getTextTranslation());

            // Show Bismillah for first ayah of all surahs except Surah 1 and 9
            if (ayah.getAyahNumber() == 1 && ayah.getSurahNumber() != 1 && ayah.getSurahNumber() != 9) {
                tvBismillah.setVisibility(View.VISIBLE);
            } else {
                tvBismillah.setVisibility(View.GONE);
            }

            // Set Ayah address
            boolean isArabic = settingsManager.isArabicLanguage();
            String surahName;
            String ayahNumberDisplay;

            if (isArabic) {
                surahName = surahNamesArabic.get(ayah.getSurahNumber());
                if (surahName == null) {
                    surahName = "السورة " + convertToArabicNumerals(String.valueOf(ayah.getSurahNumber()));
                }
                ayahNumberDisplay = convertToArabicNumerals(String.valueOf(ayah.getAyahNumber()));
            } else {
                surahName = surahNamesEnglish.get(ayah.getSurahNumber());
                if (surahName == null) {
                    surahName = "Surah " + ayah.getSurahNumber();
                }
                ayahNumberDisplay = String.valueOf(ayah.getAyahNumber());
            }

            // Format: "Surah Name (Ayah Number)" or "اسم السورة (رقم الآية)"
            tvAyahAddress.setText(String.format("%s (%s)", surahName, ayahNumberDisplay));

            // Apply font size
            tvAyahArabic.setTextSize(24 * fontSizeMultiplier);
            tvAyahTranslation.setTextSize(16 * fontSizeMultiplier);
            tvBismillah.setTextSize(24 * fontSizeMultiplier);
            tvAyahAddress.setTextSize(14 * fontSizeMultiplier);

            // Apply Uthman Taha Naskh font to Arabic text
            if (arabicTypeface != null) {
                tvAyahArabic.setTypeface(arabicTypeface);
                tvBismillah.setTypeface(arabicTypeface);
            }

            // Set bookmark icon (always filled since these are bookmarked ayahs)
            updateBookmarkIcon(ayah.getSurahNumber(), ayah.getAyahNumber());

            // Handle bookmark click
            ivBookmark.setOnClickListener(v -> {
                bookmarkManager.toggleBookmark(ayah.getSurahNumber(), ayah.getAyahNumber());
                updateBookmarkIcon(ayah.getSurahNumber(), ayah.getAyahNumber());

                // Notify listener that bookmark was removed
                if (bookmarkRemovedListener != null) {
                    bookmarkRemovedListener.onBookmarkRemoved();
                }
            });
        }

        private void updateBookmarkIcon(int surahNumber, int ayahNumber) {
            boolean isBookmarked = bookmarkManager.isBookmarked(surahNumber, ayahNumber);
            if (isBookmarked) {
                ivBookmark.setImageResource(R.drawable.ic_bookmark_filled);
            } else {
                ivBookmark.setImageResource(R.drawable.ic_bookmark_outline);
            }
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
