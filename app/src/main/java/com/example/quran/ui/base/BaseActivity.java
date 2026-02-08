package com.example.quran.ui.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Base Activity class that can be extended by all activities in the app.
 * Provides common functionality and lifecycle management.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
        observeData();
    }

    /**
     * Setup UI components, layouts, and views.
     * Called during onCreate.
     */
    protected abstract void setupUI();

    /**
     * Observe LiveData and other data sources.
     * Called during onCreate after setupUI.
     */
    protected abstract void observeData();
}
