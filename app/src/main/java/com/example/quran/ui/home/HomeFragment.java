package com.example.quran.ui.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.quran.R;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseFragment;
import com.example.quran.utils.ArabicNumeralConverter;
import com.example.quran.utils.AyahTextFormatter;
import com.example.quran.utils.Constants;
import com.example.quran.utils.SettingsManager;
import com.example.quran.utils.SurahFontHelper;
import com.example.quran.utils.ViewModelFactory;

/**
 * Home Fragment - Landing screen of the app.
 */
public class HomeFragment extends BaseFragment {

    private HomeViewModel viewModel;
    private SettingsManager settingsManager;
    private TextView tvRandomAyahArabic;
    private TextView tvAyahInfo;
    private View btnSkip;
    private View btnFeelingLucky;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    protected void setupUI(View view) {
        tvRandomAyahArabic = view.findViewById(R.id.tvRandomAyahArabic);
        tvAyahInfo = view.findViewById(R.id.tvAyahInfo);
        btnSkip = view.findViewById(R.id.btnSkip);
        btnFeelingLucky = view.findViewById(R.id.btnFeelingLucky);

        // Setup SettingsManager
        settingsManager = new SettingsManager(requireContext());

        // Apply font size
        applyFontSize();

        // Setup ViewModel
        QuranRepository repository = new QuranRepository(requireContext());
        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        // Skip button - Navigate to Surah List
        btnSkip.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_surahListFragment)
        );

        // I'm Feeling Lucky button - Navigate to random Surah
        btnFeelingLucky.setOnClickListener(v -> {
            viewModel.getRandomSurahNumber(surahNumber -> {
                requireActivity().runOnUiThread(() -> {
                    Bundle args = new Bundle();
                    args.putInt(Constants.KEY_SURAH_NUMBER, surahNumber);
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_surahDetailFragment, args);
                });
            });
        });
    }

    private void applyFontSize() {
        float multiplier = settingsManager.getFontSizeMultiplier();
        tvRandomAyahArabic.setTextSize(24 * multiplier);
    }

    @Override
    protected void observeData() {
        // Observe random Ayah and display it
        viewModel.getRandomAyah().observe(getViewLifecycleOwner(), ayah -> {
            if (ayah != null) {
                // Apply Tajweed styling if enabled
                CharSequence styledText = AyahTextFormatter.applyTajweedIfEnabled(
                    ayah.getTextArabic(), requireContext(), settingsManager);
                tvRandomAyahArabic.setText(styledText);

                // Get Surah information to display Surah name
                viewModel.getSurahByNumber(ayah.getSurahNumber()).observe(getViewLifecycleOwner(), surah -> {
                    if (surah != null) {
                        // Format: "Surah Name (Ayah Number)" or "اسم السورة (رقم الآية)"
                        boolean isArabic = settingsManager.isArabicLanguage();
                        String surahName;
                        String ayahNumberDisplay;
                        String ayahInfo;

                        Typeface arabicTypeface = ResourcesCompat.getFont(requireContext(), R.font.uthmantaha);

                        if (isArabic) {
                            // Use custom calligraphy font for Arabic surah names
                            surahName = SurahFontHelper.getCharacter(surah.getNumber());
                            ayahNumberDisplay = ArabicNumeralConverter.convert(ayah.getAyahNumber());
                            tvAyahInfo.setTextSize(14 * settingsManager.getFontSizeMultiplier() * 1.5f);
                            tvAyahInfo.setTextDirection(android.view.View.TEXT_DIRECTION_RTL);

                            // Force RTL display: wrap entire string in RLE (Right-to-Left Embedding)
                            // Order in string: surah name, space, brackets with number
                            // Display will be RTL: surah name (right), number (left)
                            String ayahNumberPart = " ﴿" + ayahNumberDisplay + "﴾";
                            String fullText = "\u202B" + surahName + ayahNumberPart + "\u202C";

                            SpannableString spannableString = new SpannableString(fullText);

                            // Apply calligraphy font to surah name (skip RLE character)
                            Typeface surahTypeface = SurahFontHelper.getTypeface(requireContext());
                            spannableString.setSpan(new CustomTypefaceSpan(surahTypeface),
                                1, 1 + surahName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            // Apply Uthman Taha font to ayah number and brackets
                            spannableString.setSpan(new CustomTypefaceSpan(arabicTypeface),
                                1 + surahName.length(), fullText.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            tvAyahInfo.setText(spannableString);
                        } else {
                            surahName = surah.getNameEnglish();
                            // Use Western numerals for English
                            ayahNumberDisplay = String.valueOf(ayah.getAyahNumber());
                            tvAyahInfo.setTextSize(14);
                            tvAyahInfo.setTextDirection(android.view.View.TEXT_DIRECTION_LTR);

                            // LTR format: surah name (default font) then ayah number with brackets (Uthman Taha font)
                            // In LTR context with Western numerals, brackets display as ﴾number﴿
                            String ayahNumberPart = " ﴾" + ayahNumberDisplay + "﴿";
                            String fullText = surahName + ayahNumberPart;

                            SpannableString spannableString = new SpannableString(fullText);

                            // Apply Uthman Taha font to ayah number and brackets
                            spannableString.setSpan(new CustomTypefaceSpan(arabicTypeface),
                                surahName.length(), fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            tvAyahInfo.setText(spannableString);
                        }
                    }
                });
            }
        });
    }

    /**
     * Custom TypefaceSpan that properly applies custom fonts.
     */
    private static class CustomTypefaceSpan extends TypefaceSpan {
        private final Typeface newTypeface;

        public CustomTypefaceSpan(Typeface typeface) {
            super("");
            this.newTypeface = typeface;
        }

        @Override
        public void updateDrawState(android.text.TextPaint paint) {
            applyCustomTypeFace(paint, newTypeface);
        }

        @Override
        public void updateMeasureState(android.text.TextPaint paint) {
            applyCustomTypeFace(paint, newTypeface);
        }

        private void applyCustomTypeFace(android.graphics.Paint paint, Typeface tf) {
            if (tf != null) {
                paint.setTypeface(tf);
            }
        }
    }
}
