package com.example.quran.ui.base;

import androidx.lifecycle.ViewModel;

import com.example.quran.data.repository.QuranRepository;

/**
 * Base ViewModel class that provides common functionality.
 * All ViewModels should extend this class.
 */
public abstract class BaseViewModel extends ViewModel {

    protected final QuranRepository repository;

    public BaseViewModel(QuranRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Cleanup resources if needed
    }
}
