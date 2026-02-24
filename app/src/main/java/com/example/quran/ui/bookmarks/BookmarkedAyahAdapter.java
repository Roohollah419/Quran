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
import com.example.quran.utils.ArabicNumeralConverter;
import com.example.quran.utils.AyahTextFormatter;
import com.example.quran.utils.AyahViewHelper;
import com.example.quran.utils.BookmarkManager;
import com.example.quran.utils.SettingsManager;
import com.example.quran.utils.SurahFontHelper;

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

            // Format and style ayah text with number and Tajweed
            CharSequence formattedText = AyahTextFormatter.formatAndStyleAyah(
                ayah.getTextArabic(), ayah.getAyahNumber(), context, settingsManager);
            tvAyahArabic.setText(formattedText);
            tvAyahTranslation.setText(ayah.getTextTranslation());

            // Show Bismillah for first ayah of all surahs except Surah 1 and 9
            AyahViewHelper.setBismillahVisibility(tvBismillah, ayah);

            // Set Ayah address (only surah name, ayah number is shown in the ayah text)
            boolean isArabic = settingsManager.isArabicLanguage();
            String surahName;

            if (isArabic) {
                // Use custom calligraphy font for Arabic surah names
                surahName = SurahFontHelper.getCharacter(ayah.getSurahNumber());
                tvAyahAddress.setTypeface(SurahFontHelper.getTypeface(context));
                tvAyahAddress.setTextSize(14 * fontSizeMultiplier * 1.5f);
            } else {
                surahName = surahNamesEnglish.get(ayah.getSurahNumber());
                if (surahName == null) {
                    surahName = "Surah " + ayah.getSurahNumber();
                }
                tvAyahAddress.setTypeface(Typeface.DEFAULT);
                tvAyahAddress.setTextSize(14 * fontSizeMultiplier);
            }

            // Show only surah name
            tvAyahAddress.setText(surahName);

            // Apply font sizes
            AyahViewHelper.applyFontSizes(fontSizeMultiplier,
                tvAyahArabic, tvAyahTranslation, tvBismillah);
            // tvAyahAddress font size is set above based on language

            // Apply Uthman Taha Naskh font to Arabic text
            AyahViewHelper.applyArabicTypeface(arabicTypeface, tvAyahArabic, tvBismillah);

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
            AyahViewHelper.updateBookmarkIcon(ivBookmark, isBookmarked);
        }
    }
}
