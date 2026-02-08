package com.example.quran;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.quran.ui.base.BaseActivity;
import com.example.quran.utils.SettingsManager;

/**
 * Main Activity - Entry point of the application.
 * Hosts the Navigation Component and manages fragment navigation.
 */
public class MainActivity extends BaseActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before onCreate
        applyTheme();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setupUI() {
        setContentView(R.layout.activity_main);

        // Setup Navigation Component
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController);
        }
    }

    @Override
    protected void observeData() {
        // No data to observe in MainActivity
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
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
