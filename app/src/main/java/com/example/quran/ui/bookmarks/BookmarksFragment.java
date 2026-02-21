package com.example.quran.ui.bookmarks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quran.R;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseFragment;
import com.example.quran.ui.surahdetail.AyahAdapter;
import com.example.quran.utils.SettingsManager;
import com.example.quran.utils.ViewModelFactory;

/**
 * Fragment displaying bookmarked Ayahs.
 */
public class BookmarksFragment extends BaseFragment {

    private BookmarksViewModel viewModel;
    private SettingsManager settingsManager;
    private RecyclerView recyclerView;
    private TextView tvEmptyBookmarks;
    private AyahAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookmarks, container, false);
    }

    @Override
    protected void setupUI(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewBookmarks);
        tvEmptyBookmarks = view.findViewById(R.id.tvEmptyBookmarks);

        // Setup SettingsManager
        settingsManager = new SettingsManager(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AyahAdapter(settingsManager.getFontSizeMultiplier(), requireContext());
        recyclerView.setAdapter(adapter);

        // Setup ViewModel
        QuranRepository repository = new QuranRepository(requireContext());
        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(BookmarksViewModel.class);

        // Load bookmarks
        viewModel.loadBookmarks();
    }

    @Override
    protected void observeData() {
        viewModel.getBookmarkedAyahs().observe(getViewLifecycleOwner(), ayahs -> {
            if (ayahs != null) {
                if (ayahs.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    tvEmptyBookmarks.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvEmptyBookmarks.setVisibility(View.GONE);
                    adapter.setAyahs(ayahs);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload bookmarks when fragment becomes visible again
        if (viewModel != null) {
            viewModel.loadBookmarks();
        }
    }
}
