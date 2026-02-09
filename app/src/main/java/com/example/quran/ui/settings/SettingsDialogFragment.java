package com.example.quran.ui.settings;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.SeekBar;
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
    private RadioButton rbLight, rbDark;
    private SeekBar seekBarFontSize;
    private TextView tvFontSizeValue, tvVersion;
    private MaterialButton btnApply, btnCancel;

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
        rbLight = view.findViewById(R.id.rbLight);
        rbDark = view.findViewById(R.id.rbDark);
        seekBarFontSize = view.findViewById(R.id.seekBarFontSize);
        tvFontSizeValue = view.findViewById(R.id.tvFontSizeValue);
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
            rbDark.setChecked(true);
        } else {
            rbLight.setChecked(true);
        }

        // Load font size
        int fontSize = settingsManager.getFontSize();
        seekBarFontSize.setProgress(fontSize);
        updateFontSizeLabel(fontSize);
    }

    private void setupListeners() {
        // Font size SeekBar listener
        seekBarFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateFontSizeLabel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
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

    private void updateFontSizeLabel(int fontSize) {
        String label = SettingsManager.getFontSizeLabel(fontSize);
        tvFontSizeValue.setText(label);
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
        String theme = rbDark.isChecked() ? SettingsManager.THEME_DARK : SettingsManager.THEME_LIGHT;
        settingsManager.setTheme(theme);

        // Save font size
        int fontSize = seekBarFontSize.getProgress();
        settingsManager.setFontSize(fontSize);
    }
}
