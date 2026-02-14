package com.example.quran;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.quran.ui.base.BaseActivity;
import com.example.quran.ui.settings.SettingsDialogFragment;
import com.example.quran.utils.SettingsManager;

/**
 * Main Activity - Entry point of the application.
 * Hosts the Navigation Component and manages fragment navigation.
 */
public class MainActivity extends BaseActivity {

    private NavController navController;
    private ImageButton btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before onCreate
        applyTheme();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setupUI() {
        setContentView(R.layout.activity_main);

        // Setup Settings button FIRST
        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            showSettingsDialog();
        });

        // Setup Navigation Component
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Listen for navigation changes to update settings icon color
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.surahListFragment) {
                    // White icon for Surah list page (green header background)
                    btnSettings.setImageTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(this, android.R.color.white)));
                } else {
                    // Default primary color for other pages
                    btnSettings.setImageTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(this, R.color.primary)));
                }
            });
        }
    }

    @Override
    protected void observeData() {
        // No data to observe in MainActivity
    }

    private void showSettingsDialog() {
        // Check if dialog is already showing
        SettingsDialogFragment existingDialog = (SettingsDialogFragment)
                getSupportFragmentManager().findFragmentByTag("SettingsDialog");
        if (existingDialog != null && existingDialog.isVisible()) {
            return; // Dialog already showing, don't create another
        }

        SettingsDialogFragment dialog = SettingsDialogFragment.newInstance(() -> {
            // Mark that settings dialog should reopen after recreation
            getIntent().putExtra("REOPEN_SETTINGS", true);
            // Recreate activity to apply changes immediately
            recreate();
        });
        dialog.show(getSupportFragmentManager(), "SettingsDialog");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reopen settings dialog if it was open before recreation
        if (getIntent().getBooleanExtra("REOPEN_SETTINGS", false)) {
            getIntent().removeExtra("REOPEN_SETTINGS");
            // Post to ensure activity is fully ready
            btnSettings.post(() -> {
                // Dismiss any existing dialog fragments first
                SettingsDialogFragment existingDialog = (SettingsDialogFragment)
                        getSupportFragmentManager().findFragmentByTag("SettingsDialog");
                if (existingDialog != null) {
                    existingDialog.dismissAllowingStateLoss();
                }
                // Show new dialog
                showSettingsDialog();
            });
        }
    }

    private void applyTheme() {
        SettingsManager settingsManager = new SettingsManager(this);
        if (settingsManager.isDarkTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
