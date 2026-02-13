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
            SettingsDialogFragment dialog = SettingsDialogFragment.newInstance(() -> {
                // Recreate activity to apply theme change
                recreate();
            });
            dialog.show(getSupportFragmentManager(), "SettingsDialog");
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

    private void applyTheme() {
        SettingsManager settingsManager = new SettingsManager(this);
        if (settingsManager.isDarkTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
