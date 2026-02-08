package com.example.quran.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.quran.R;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseFragment;
import com.example.quran.ui.settings.SettingsDialogFragment;
import com.example.quran.utils.Constants;
import com.example.quran.utils.SettingsManager;
import com.example.quran.utils.ViewModelFactory;

/**
 * Home Fragment - Landing screen of the app.
 */
public class HomeFragment extends BaseFragment {

    private HomeViewModel viewModel;
    private SettingsManager settingsManager;
    private TextView tvRandomAyahArabic;
    private View btnSkip;
    private View btnFeelingLucky;
    private ImageButton btnSettings;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    protected void setupUI(View view) {
        tvRandomAyahArabic = view.findViewById(R.id.tvRandomAyahArabic);
        btnSkip = view.findViewById(R.id.btnSkip);
        btnFeelingLucky = view.findViewById(R.id.btnFeelingLucky);
        btnSettings = view.findViewById(R.id.btnSettings);

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

        // Settings button
        btnSettings.setOnClickListener(v -> {
            SettingsDialogFragment dialog = SettingsDialogFragment.newInstance(() -> {
                // Recreate activity to apply theme change
                requireActivity().recreate();
            });
            dialog.show(getParentFragmentManager(), "SettingsDialog");
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
                tvRandomAyahArabic.setText(ayah.getTextArabic());
            }
        });
    }
}
