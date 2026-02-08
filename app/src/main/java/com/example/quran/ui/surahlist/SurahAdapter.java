package com.example.quran.ui.surahlist;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quran.R;
import com.example.quran.data.model.Surah;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for displaying list of Surahs.
 */
public class SurahAdapter extends RecyclerView.Adapter<SurahAdapter.SurahViewHolder> {

    private List<Surah> surahs = new ArrayList<>();
    private OnSurahClickListener listener;
    private float fontSizeMultiplier;

    public interface OnSurahClickListener {
        void onSurahClick(Surah surah);
    }

    public SurahAdapter(OnSurahClickListener listener, float fontSizeMultiplier) {
        this.listener = listener;
        this.fontSizeMultiplier = fontSizeMultiplier;
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
        private TextView tvNumber;
        private TextView tvNameArabic;
        private TextView tvNameEnglish;
        private TextView tvTranslation;
        private TextView tvAyahCount;
        private TextView tvRevelationType;

        public SurahViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvSurahNumber);
            tvNameArabic = itemView.findViewById(R.id.tvSurahNameArabic);
            tvNameEnglish = itemView.findViewById(R.id.tvSurahNameEnglish);
            tvTranslation = itemView.findViewById(R.id.tvSurahTranslation);
            tvAyahCount = itemView.findViewById(R.id.tvAyahCount);
            tvRevelationType = itemView.findViewById(R.id.tvRevelationType);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onSurahClick(surahs.get(position));
                }
            });
        }

        public void bind(Surah surah) {
            tvNumber.setText(String.valueOf(surah.getNumber()));
            tvNameArabic.setText(surah.getNameArabic());
            tvNameEnglish.setText(surah.getNameEnglish());
            tvTranslation.setText(surah.getTranslation());
            tvAyahCount.setText(itemView.getContext().getString(R.string.ayah_count, surah.getTotalAyahs()));
            tvRevelationType.setText(surah.getRevelationType());

            // Apply font size
            tvNumber.setTextSize(18 * fontSizeMultiplier);
            tvNameArabic.setTextSize(18 * fontSizeMultiplier);
            tvNameEnglish.setTextSize(14 * fontSizeMultiplier);
            tvTranslation.setTextSize(12 * fontSizeMultiplier);
            tvAyahCount.setTextSize(12 * fontSizeMultiplier);
            tvRevelationType.setTextSize(12 * fontSizeMultiplier);

            // Apply Naskh font to Arabic text
            tvNameArabic.setTypeface(Typeface.SERIF);
        }
    }
}
