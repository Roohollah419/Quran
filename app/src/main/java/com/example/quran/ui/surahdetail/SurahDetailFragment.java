package com.example.quran.ui.surahdetail;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quran.R;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseFragment;
import com.example.quran.utils.Constants;
import com.example.quran.utils.SettingsManager;
import com.example.quran.utils.ViewModelFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Fragment displaying details of a specific Surah with its Ayahs.
 */
public class SurahDetailFragment extends BaseFragment {

    private SurahDetailViewModel viewModel;
    private SettingsManager settingsManager;
    private TextView tvSurahName;
    private TextView tvSurahNumber;
    private TextView tvSurahInfo;
    private ImageView ivRevelationType;
    private RecyclerView recyclerView;
    private AyahAdapter adapter;
    private Typeface arabicTypeface;

    private int surahNumber;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_surah_detail, container, false);
    }

    @Override
    protected void setupUI(View view) {
        tvSurahName = view.findViewById(R.id.tvSurahName);
        tvSurahNumber = view.findViewById(R.id.tvSurahNumber);
        tvSurahInfo = view.findViewById(R.id.tvSurahInfo);
        ivRevelationType = view.findViewById(R.id.ivRevelationType);
        recyclerView = view.findViewById(R.id.recyclerViewAyahs);

        // Setup SettingsManager
        settingsManager = new SettingsManager(requireContext());

        // Load Arabic font
        arabicTypeface = ResourcesCompat.getFont(requireContext(), R.font.uthmantaha);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AyahAdapter(settingsManager.getFontSizeMultiplier(), requireContext());
        recyclerView.setAdapter(adapter);

        // Apply font size to header views
        applyFontSize();

        // Get arguments
        if (getArguments() != null) {
            surahNumber = getArguments().getInt(Constants.KEY_SURAH_NUMBER, 1);
        }

        // Setup ViewModel
        QuranRepository repository = new QuranRepository(requireContext());
        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(SurahDetailViewModel.class);

        // Load Surah data
        viewModel.loadSurah(surahNumber);
    }

    @Override
    protected void observeData() {
        viewModel.getSurah().observe(getViewLifecycleOwner(), surah -> {
            if (surah != null) {
                boolean isArabic = settingsManager.isArabicLanguage();

                // Set surah name based on language
                if (isArabic) {
                    tvSurahName.setText(surah.getNameArabic());
                    if (arabicTypeface != null) {
                        tvSurahName.setTypeface(arabicTypeface);
                    }
                    // Show surah number and ayah count in Arabic numerals
                    tvSurahNumber.setText(convertToArabicNumerals(String.valueOf(surah.getNumber())));
                    tvSurahInfo.setText(convertToArabicNumerals(String.valueOf(surah.getTotalAyahs())));
                } else {
                    tvSurahName.setText(surah.getNameEnglish());
                    tvSurahName.setTypeface(Typeface.DEFAULT_BOLD);
                    // Show surah number and ayah count in English numerals
                    tvSurahNumber.setText(String.valueOf(surah.getNumber()));
                    tvSurahInfo.setText(String.valueOf(surah.getTotalAyahs()));
                }

                // Load revelation type icon
                String revelationType = surah.getRevelationType();
                try {
                    InputStream inputStream;
                    if (revelationType.equalsIgnoreCase("Meccan")) {
                        inputStream = requireContext().getAssets().open("mecca.png");
                    } else {
                        inputStream = requireContext().getAssets().open("madina.png");
                    }
                    Drawable drawable = Drawable.createFromStream(inputStream, null);
                    ivRevelationType.setImageDrawable(drawable);
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        viewModel.getAyahs().observe(getViewLifecycleOwner(), ayahs -> {
            if (ayahs != null) {
                adapter.setAyahs(ayahs);
            }
        });
    }

    private void applyFontSize() {
        float multiplier = settingsManager.getFontSizeMultiplier();
        tvSurahName.setTextSize(18 * multiplier);
        tvSurahNumber.setTextSize(16 * multiplier);
        tvSurahInfo.setTextSize(16 * multiplier);
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
