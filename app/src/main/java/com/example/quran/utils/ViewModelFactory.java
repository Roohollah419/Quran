package com.example.quran.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.home.HomeViewModel;
import com.example.quran.ui.surahdetail.SurahDetailViewModel;
import com.example.quran.ui.surahlist.SurahListViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final QuranRepository repository;

    public ViewModelFactory(QuranRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(repository);
        } else if (modelClass.isAssignableFrom(SurahListViewModel.class)) {
            return (T) new SurahListViewModel(repository);
        } else if (modelClass.isAssignableFrom(SurahDetailViewModel.class)) {
            return (T) new SurahDetailViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
