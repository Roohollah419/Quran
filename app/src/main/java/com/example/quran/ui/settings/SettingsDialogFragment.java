package com.example.quran.ui.settings;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.quran.R;
import com.example.quran.utils.SettingsManager;
import com.google.android.material.button.MaterialButton;

/**
 * Dialog fragment for app settings (theme and font size).
 */
public class SettingsDialogFragment extends DialogFragment {

    private SettingsManager settingsManager;
    private Button btnThemeLight, btnThemeDark;
    private Button btnLanguageEnglish, btnLanguageArabic;
    private Button btnFontSmall, btnFontMedium, btnFontLarge, btnFontXLarge;
    private TextView tvVersion;
    private MaterialButton btnApply, btnCancel;

    private OnSettingsChangedListener listener;
    private int selectedFontSize = 1; // Default Medium

    public interface OnSettingsChangedListener {
        void onSettingsChanged();
    }

    public static SettingsDialogFragment newInstance(OnSettingsChangedListener listener) {
        SettingsDialogFragment fragment = new SettingsDialogFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
        settingsManager = new SettingsManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        btnThemeLight = view.findViewById(R.id.btnThemeLight);
        btnThemeDark = view.findViewById(R.id.btnThemeDark);
        btnLanguageEnglish = view.findViewById(R.id.btnLanguageEnglish);
        btnLanguageArabic = view.findViewById(R.id.btnLanguageArabic);
        btnFontSmall = view.findViewById(R.id.btnFontSmall);
        btnFontMedium = view.findViewById(R.id.btnFontMedium);
        btnFontLarge = view.findViewById(R.id.btnFontLarge);
        btnFontXLarge = view.findViewById(R.id.btnFontXLarge);
        tvVersion = view.findViewById(R.id.tvVersion);
        btnApply = view.findViewById(R.id.btnApply);
        btnCancel = view.findViewById(R.id.btnCancel);

        // Set version
        setVersionText();

        // Load current settings
        loadCurrentSettings();

        // Setup listeners
        setupListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
        }
    }

    private void loadCurrentSettings() {
        // Load theme
        if (settingsManager.isDarkTheme()) {
            selectThemeButton(btnThemeDark);
        } else {
            selectThemeButton(btnThemeLight);
        }

        // Load language
        if (settingsManager.isArabicLanguage()) {
            selectLanguageButton(btnLanguageArabic);
        } else {
            selectLanguageButton(btnLanguageEnglish);
        }

        // Load font size
        selectedFontSize = settingsManager.getFontSize();
        selectFontSizeButton(selectedFontSize);
    }

    private void setupListeners() {
        // Theme button listeners
        btnThemeLight.setOnClickListener(v -> selectThemeButton(btnThemeLight));
        btnThemeDark.setOnClickListener(v -> selectThemeButton(btnThemeDark));

        // Language button listeners
        btnLanguageEnglish.setOnClickListener(v -> selectLanguageButton(btnLanguageEnglish));
        btnLanguageArabic.setOnClickListener(v -> selectLanguageButton(btnLanguageArabic));

        // Font size button listeners
        btnFontSmall.setOnClickListener(v -> {
            selectedFontSize = 0;
            selectFontSizeButton(0);
        });
        btnFontMedium.setOnClickListener(v -> {
            selectedFontSize = 1;
            selectFontSizeButton(1);
        });
        btnFontLarge.setOnClickListener(v -> {
            selectedFontSize = 2;
            selectFontSizeButton(2);
        });
        btnFontXLarge.setOnClickListener(v -> {
            selectedFontSize = 3;
            selectFontSizeButton(3);
        });

        // Apply button
        btnApply.setOnClickListener(v -> {
            saveSettings();
            if (listener != null) {
                listener.onSettingsChanged();
            }
            dismiss();
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void selectThemeButton(Button selectedButton) {
        btnThemeLight.setSelected(selectedButton == btnThemeLight);
        btnThemeDark.setSelected(selectedButton == btnThemeDark);
    }

    private void selectLanguageButton(Button selectedButton) {
        btnLanguageEnglish.setSelected(selectedButton == btnLanguageEnglish);
        btnLanguageArabic.setSelected(selectedButton == btnLanguageArabic);
    }

    private void selectFontSizeButton(int fontSize) {
        btnFontSmall.setSelected(fontSize == 0);
        btnFontMedium.setSelected(fontSize == 1);
        btnFontLarge.setSelected(fontSize == 2);
        btnFontXLarge.setSelected(fontSize == 3);
    }

    private void setVersionText() {
        try {
            PackageInfo packageInfo = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0);
            String version = packageInfo.versionName;
            tvVersion.setText(getString(R.string.version_format, version));
        } catch (PackageManager.NameNotFoundException e) {
            tvVersion.setText(getString(R.string.version_format, "1.0"));
        }
    }

    private void saveSettings() {
        // Save theme
        String theme = btnThemeDark.isSelected() ? SettingsManager.THEME_DARK : SettingsManager.THEME_LIGHT;
        settingsManager.setTheme(theme);

        // Save language
        String language = btnLanguageArabic.isSelected() ? SettingsManager.LANGUAGE_ARABIC : SettingsManager.LANGUAGE_ENGLISH;
        settingsManager.setLanguage(language);

        // Save font size
        settingsManager.setFontSize(selectedFontSize);
    }
}
