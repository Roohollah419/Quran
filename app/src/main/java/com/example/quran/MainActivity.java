package com.example.quran;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.quran.ui.base.BaseActivity;
import com.example.quran.ui.downloadmanager.DownloadManagerFragment;
import com.example.quran.ui.settings.SettingsDialogFragment;
import com.example.quran.utils.SettingsManager;

/**
 * Main Activity - Entry point of the application.
 * Hosts the Navigation Component and manages fragment navigation.
 */
public class MainActivity extends BaseActivity {

    private NavController navController;
    private ImageButton btnSettings;
    private ImageButton btnBookmarks;
    private ImageButton btnComments;
    private ImageButton btnAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before onCreate
        applyTheme();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setupUI() {
        setContentView(R.layout.activity_main);

        // Setup Bookmarks button
        btnBookmarks = findViewById(R.id.btnBookmarks);
        btnBookmarks.setOnClickListener(v -> {
            navigateToBookmarks();
        });

        // Setup Comments button
        btnComments = findViewById(R.id.btnComments);
        btnComments.setOnClickListener(v -> {
            navigateToComments();
        });

        // Setup Audio button
        btnAudio = findViewById(R.id.btnAudio);
        btnAudio.setOnClickListener(v -> {
            showDownloadManagerDialog();
        });

        // Setup Settings button
        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            showSettingsDialog();
        });

        // Setup Navigation Component
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Listen for navigation changes to update icon colors
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                // Always use primary color since toolbar has background color now
                btnSettings.setImageTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.primary)));
                btnBookmarks.setImageTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.primary)));
                btnComments.setImageTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.primary)));
                btnAudio.setImageTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.primary)));
            });
        }
    }

    @Override
    protected void observeData() {
        // No data to observe in MainActivity
    }

    private void navigateToBookmarks() {
        if (navController != null) {
            int currentDestination = navController.getCurrentDestination().getId();
            if (currentDestination == R.id.homeFragment) {
                navController.navigate(R.id.action_homeFragment_to_bookmarksFragment);
            } else if (currentDestination == R.id.surahListFragment) {
                navController.navigate(R.id.action_surahListFragment_to_bookmarksFragment);
            } else if (currentDestination == R.id.surahDetailFragment) {
                navController.navigate(R.id.action_surahDetailFragment_to_bookmarksFragment);
            } else if (currentDestination == R.id.commentsFragment) {
                navController.navigate(R.id.action_commentsFragment_to_bookmarksFragment);
            }
        }
    }

    private void navigateToComments() {
        if (navController != null) {
            int currentDestination = navController.getCurrentDestination().getId();
            if (currentDestination == R.id.homeFragment) {
                navController.navigate(R.id.action_homeFragment_to_commentsFragment);
            } else if (currentDestination == R.id.surahListFragment) {
                navController.navigate(R.id.action_surahListFragment_to_commentsFragment);
            } else if (currentDestination == R.id.surahDetailFragment) {
                navController.navigate(R.id.action_surahDetailFragment_to_commentsFragment);
            } else if (currentDestination == R.id.bookmarksFragment) {
                navController.navigate(R.id.action_bookmarksFragment_to_commentsFragment);
            }
        }
    }

    private void showDownloadManagerDialog() {
        // Check if dialog is already showing
        DownloadManagerFragment existingDialog = (DownloadManagerFragment)
                getSupportFragmentManager().findFragmentByTag("DownloadManagerDialog");
        if (existingDialog != null && existingDialog.isVisible()) {
            return; // Dialog already showing, don't create another
        }

        DownloadManagerFragment dialog = DownloadManagerFragment.newInstance();
        dialog.show(getSupportFragmentManager(), "DownloadManagerDialog");
    }

    private void showSettingsDialog() {
        // Check if dialog is already showing
        SettingsDialogFragment existingDialog = (SettingsDialogFragment)
                getSupportFragmentManager().findFragmentByTag("SettingsDialog");
        if (existingDialog != null && existingDialog.isVisible()) {
            return; // Dialog already showing, don't create another
        }

        // Hide settings button when dialog is shown
        btnSettings.setVisibility(android.view.View.GONE);

        SettingsDialogFragment dialog = SettingsDialogFragment.newInstance(
            () -> {
                // Mark that settings dialog should reopen after recreation
                getIntent().putExtra("REOPEN_SETTINGS", true);
                // Recreate activity to apply changes immediately
                recreate();
            },
            () -> {
                // Show settings button again when dialog is dismissed
                btnSettings.setVisibility(android.view.View.VISIBLE);
            }
        );

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
