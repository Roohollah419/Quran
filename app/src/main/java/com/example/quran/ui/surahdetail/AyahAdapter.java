package com.example.quran.ui.surahdetail;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quran.R;
import com.example.quran.data.model.Ayah;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for displaying Ayahs (verses).
 */
public class AyahAdapter extends RecyclerView.Adapter<AyahAdapter.AyahViewHolder> {

    private List<Ayah> ayahs = new ArrayList<>();
    private float fontSizeMultiplier;

    public AyahAdapter(float fontSizeMultiplier) {
        this.fontSizeMultiplier = fontSizeMultiplier;
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
        private TextView tvAyahNumber;
        private TextView tvAyahArabic;
        private TextView tvAyahTranslation;

        public AyahViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAyahNumber = itemView.findViewById(R.id.tvAyahNumber);
            tvAyahArabic = itemView.findViewById(R.id.tvAyahArabic);
            tvAyahTranslation = itemView.findViewById(R.id.tvAyahTranslation);
        }

        public void bind(Ayah ayah) {
            tvAyahNumber.setText(String.valueOf(ayah.getAyahNumber()));
            tvAyahArabic.setText(ayah.getTextArabic());
            tvAyahTranslation.setText(ayah.getTextTranslation());

            // Apply font size
            tvAyahNumber.setTextSize(14 * fontSizeMultiplier);
            tvAyahArabic.setTextSize(20 * fontSizeMultiplier);
            tvAyahTranslation.setTextSize(16 * fontSizeMultiplier);

            // Apply Naskh font to Arabic text
            tvAyahArabic.setTypeface(Typeface.SERIF);
        }
    }
}
