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
import com.example.quran.utils.SettingsManager;
import com.example.quran.utils.ViewModelFactory;

/**
 * Home Fragment - Landing screen of the app.
 */
public class HomeFragment extends BaseFragment {

    private HomeViewModel viewModel;
    private SettingsManager settingsManager;
    private TextView tvAppTitle;
    private TextView tvWelcome;
    private TextView tvSurahCount;
    private View btnViewSurahs;
    private ImageButton btnSettings;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    protected void setupUI(View view) {
        tvAppTitle = view.findViewById(R.id.tvAppTitle);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvSurahCount = view.findViewById(R.id.tvSurahCount);
        btnViewSurahs = view.findViewById(R.id.btnViewSurahs);
        btnSettings = view.findViewById(R.id.btnSettings);

        // Setup SettingsManager
        settingsManager = new SettingsManager(requireContext());

        // Apply font size
        applyFontSize();

        // Setup ViewModel
        QuranRepository repository = new QuranRepository(requireContext());
        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        // Navigate to Surah List
        btnViewSurahs.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_surahListFragment)
        );

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
        tvAppTitle.setTextSize(36 * multiplier);
        tvWelcome.setTextSize(20 * multiplier);
        tvSurahCount.setTextSize(18 * multiplier);
    }

    @Override
    protected void observeData() {
        viewModel.getAllSurahs().observe(getViewLifecycleOwner(), surahs -> {
            if (surahs != null) {
                tvSurahCount.setText(getString(R.string.total_surahs, surahs.size()));
            }
        });
    }
}
