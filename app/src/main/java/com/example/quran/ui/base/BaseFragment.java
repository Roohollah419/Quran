package com.example.quran.ui.base;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Base Fragment class that can be extended by all fragments in the app.
 * Provides common functionality and lifecycle management.
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI(view);
        observeData();
    }

    /**
     * Setup UI components and views.
     * Called during onViewCreated.
     *
     * @param view The root view of the fragment
     */
    protected abstract void setupUI(View view);

    /**
     * Observe LiveData and other data sources.
     * Called during onViewCreated after setupUI.
     */
    protected abstract void observeData();
}
