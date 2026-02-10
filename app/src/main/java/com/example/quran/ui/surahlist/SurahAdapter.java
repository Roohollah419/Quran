package com.example.quran.ui.surahlist;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quran.R;
import com.example.quran.data.model.Surah;
import com.example.quran.utils.SettingsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for displaying list of Surahs.
 */
public class SurahAdapter extends RecyclerView.Adapter<SurahAdapter.SurahViewHolder> {

    private List<Surah> surahs = new ArrayList<>();
    private OnSurahClickListener listener;
    private float fontSizeMultiplier;
    private Typeface arabicTypeface;
    private SettingsManager settingsManager;
    private Context context;

    public interface OnSurahClickListener {
        void onSurahClick(Surah surah);
    }

    public SurahAdapter(OnSurahClickListener listener, float fontSizeMultiplier, Context context) {
        this.listener = listener;
        this.fontSizeMultiplier = fontSizeMultiplier;
        this.context = context;
        this.arabicTypeface = ResourcesCompat.getFont(context, R.font.uthmantaha);
        this.settingsManager = new SettingsManager(context);
    }

    @NonNull
    @Override
    public SurahViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_surah, parent, false);
        return new SurahViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SurahViewHolder holder, int position) {
        Surah surah = surahs.get(position);
        holder.bind(surah);
    }

    @Override
    public int getItemCount() {
        return surahs.size();
    }

    public void setSurahs(List<Surah> surahs) {
        this.surahs = surahs;
        notifyDataSetChanged();
    }

    class SurahViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layoutSurahItem;
        private TextView tvSurahName;
        private TextView tvAyahCount;
        private TextView tvNumber;
        private TextView tvRevelationType;

        public SurahViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutSurahItem = itemView.findViewById(R.id.layoutSurahItem);
            tvSurahName = itemView.findViewById(R.id.tvSurahName);
            tvAyahCount = itemView.findViewById(R.id.tvAyahCount);
            tvNumber = itemView.findViewById(R.id.tvSurahNumber);
            tvRevelationType = itemView.findViewById(R.id.tvRevelationType);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onSurahClick(surahs.get(position));
                }
            });
        }

        public void bind(Surah surah) {
            boolean isArabic = settingsManager.isArabicLanguage();

            // Set layout direction based on language
            if (isArabic) {
                layoutSurahItem.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                layoutSurahItem.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

            // Set Surah name
            if (isArabic) {
                tvSurahName.setText(surah.getNameArabic());
                if (arabicTypeface != null) {
                    tvSurahName.setTypeface(arabicTypeface);
                }
            } else {
                tvSurahName.setText(surah.getNameEnglish());
                tvSurahName.setTypeface(Typeface.DEFAULT);
            }

            // Set ayah count
            String ayahCount = String.valueOf(surah.getTotalAyahs());
            if (isArabic) {
                tvAyahCount.setText(convertToArabicNumerals(ayahCount));
            } else {
                tvAyahCount.setText(ayahCount);
            }

            // Set surah number
            String surahNumber = String.valueOf(surah.getNumber());
            if (isArabic) {
                tvNumber.setText(convertToArabicNumerals(surahNumber));
            } else {
                tvNumber.setText(surahNumber);
            }

            // Set revelation type
            String revelationType = surah.getRevelationType();
            if (isArabic) {
                if (revelationType.equalsIgnoreCase("Meccan")) {
                    tvRevelationType.setText(context.getString(R.string.meccan_ar));
                } else {
                    tvRevelationType.setText(context.getString(R.string.medinan_ar));
                }
                if (arabicTypeface != null) {
                    tvRevelationType.setTypeface(arabicTypeface);
                }
            } else {
                tvRevelationType.setText(revelationType);
                tvRevelationType.setTypeface(Typeface.DEFAULT);
            }

            // Apply font size
            tvSurahName.setTextSize(18 * fontSizeMultiplier);
            tvAyahCount.setTextSize(16 * fontSizeMultiplier);
            tvNumber.setTextSize(16 * fontSizeMultiplier);
            tvRevelationType.setTextSize(16 * fontSizeMultiplier);
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
