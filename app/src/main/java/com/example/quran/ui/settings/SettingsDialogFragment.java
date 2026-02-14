package com.example.quran.ui.settings;

import android.app.Dialog;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.example.quran.R;
import com.example.quran.utils.SettingsManager;

/**
 * Dialog fragment for app settings (theme and font size).
 */
public class SettingsDialogFragment extends DialogFragment {

    private SettingsManager settingsManager;
    private Button btnThemeLight, btnThemeDark;
    private Button btnLanguageEnglish, btnLanguageArabic;
    private Button btnFontSmall, btnFontMedium, btnFontLarge, btnFontXLarge;
    private TextView tvVersion;

    private OnSettingsChangedListener listener;

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
        selectFontSizeButton(settingsManager.getFontSize());
    }

    private void setupListeners() {
        // Theme button listeners - apply immediately
        btnThemeLight.setOnClickListener(v -> {
            selectThemeButton(btnThemeLight);
            settingsManager.setTheme(SettingsManager.THEME_LIGHT);
            applyThemeChange(false);
        });
        btnThemeDark.setOnClickListener(v -> {
            selectThemeButton(btnThemeDark);
            settingsManager.setTheme(SettingsManager.THEME_DARK);
            applyThemeChange(true);
        });

        // Language button listeners - apply immediately
        btnLanguageEnglish.setOnClickListener(v -> {
            selectLanguageButton(btnLanguageEnglish);
            applyLanguageChange(SettingsManager.LANGUAGE_ENGLISH);
        });
        btnLanguageArabic.setOnClickListener(v -> {
            selectLanguageButton(btnLanguageArabic);
            applyLanguageChange(SettingsManager.LANGUAGE_ARABIC);
        });

        // Font size button listeners - apply immediately
        btnFontSmall.setOnClickListener(v -> {
            selectFontSizeButton(0);
            applyFontSizeChange(0);
        });
        btnFontMedium.setOnClickListener(v -> {
            selectFontSizeButton(1);
            applyFontSizeChange(1);
        });
        btnFontLarge.setOnClickListener(v -> {
            selectFontSizeButton(2);
            applyFontSizeChange(2);
        });
        btnFontXLarge.setOnClickListener(v -> {
            selectFontSizeButton(3);
            applyFontSizeChange(3);
        });
    }

    private void applyThemeChange(boolean isDarkTheme) {
        // Save theme immediately
        String theme = isDarkTheme ? SettingsManager.THEME_DARK : SettingsManager.THEME_LIGHT;
        settingsManager.setTheme(theme);

        // Apply and recreate immediately
        if (listener != null) {
            listener.onSettingsChanged();
        }
    }

    private void applyLanguageChange(String language) {
        // Save language immediately
        settingsManager.setLanguage(language);

        // Apply and recreate immediately
        if (listener != null) {
            listener.onSettingsChanged();
        }
    }

    private void applyFontSizeChange(int fontSize) {
        // Save font size immediately
        settingsManager.setFontSize(fontSize);

        // Apply and recreate immediately
        if (listener != null) {
            listener.onSettingsChanged();
        }
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
}
