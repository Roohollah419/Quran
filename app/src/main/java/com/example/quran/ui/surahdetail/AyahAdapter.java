package com.example.quran.ui.surahdetail;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quran.R;
import com.example.quran.data.model.Ayah;
import com.example.quran.utils.SettingsManager;

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

    public AyahAdapter(float fontSizeMultiplier, Context context) {
        this.fontSizeMultiplier = fontSizeMultiplier;
        this.context = context;
        this.arabicTypeface = ResourcesCompat.getFont(context, R.font.uthmantaha);
        this.settingsManager = new SettingsManager(context);
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

        public AyahViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAyahArabic = itemView.findViewById(R.id.tvAyahArabic);
            tvAyahTranslation = itemView.findViewById(R.id.tvAyahTranslation);
            tvBismillah = itemView.findViewById(R.id.tvBismillah);
        }

        public void bind(Ayah ayah) {
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

            // Apply font size
            tvAyahArabic.setTextSize(24 * fontSizeMultiplier);
            tvAyahTranslation.setTextSize(16 * fontSizeMultiplier);
            tvBismillah.setTextSize(24 * fontSizeMultiplier);

            // Apply Uthman Taha Naskh font to Arabic text
            if (arabicTypeface != null) {
                tvAyahArabic.setTypeface(arabicTypeface);
                tvBismillah.setTypeface(arabicTypeface);
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
