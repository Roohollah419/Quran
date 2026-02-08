package com.example.quran.ui.surahdetail;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quran.R;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseFragment;
import com.example.quran.utils.Constants;
import com.example.quran.utils.SettingsManager;
import com.example.quran.utils.ViewModelFactory;

/**
 * Fragment displaying details of a specific Surah with its Ayahs.
 */
public class SurahDetailFragment extends BaseFragment {

    private SurahDetailViewModel viewModel;
    private SettingsManager settingsManager;
    private TextView tvSurahName;
    private TextView tvSurahInfo;
    private RecyclerView recyclerView;
    private AyahAdapter adapter;

    private int surahNumber;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_surah_detail, container, false);
    }

    @Override
    protected void setupUI(View view) {
        tvSurahName = view.findViewById(R.id.tvSurahName);
        tvSurahInfo = view.findViewById(R.id.tvSurahInfo);
        recyclerView = view.findViewById(R.id.recyclerViewAyahs);

        // Setup SettingsManager
        settingsManager = new SettingsManager(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AyahAdapter(settingsManager.getFontSizeMultiplier());
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
                tvSurahName.setText(surah.getNameArabic() + " - " + surah.getNameEnglish());
                tvSurahInfo.setText(getString(R.string.surah_info,
                        surah.getTranslation(),
                        surah.getTotalAyahs(),
                        surah.getRevelationType()));
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
        tvSurahName.setTextSize(24 * multiplier);
        tvSurahInfo.setTextSize(14 * multiplier);

        // Apply Naskh font to Arabic text
        tvSurahName.setTypeface(Typeface.SERIF);
    }
}
