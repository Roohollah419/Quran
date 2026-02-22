package com.example.quran.ui.comments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quran.R;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseFragment;
import com.example.quran.utils.Constants;
import com.example.quran.utils.SettingsManager;
import com.example.quran.utils.ViewModelFactory;

/**
 * Fragment displaying commented Ayahs with their comment text.
 */
public class CommentsFragment extends BaseFragment {

    private CommentsViewModel viewModel;
    private SettingsManager settingsManager;
    private RecyclerView recyclerView;
    private TextView tvEmptyComments;
    private CommentedAyahAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    protected void setupUI(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewComments);
        tvEmptyComments = view.findViewById(R.id.tvEmptyComments);

        // Setup SettingsManager
        settingsManager = new SettingsManager(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CommentedAyahAdapter(settingsManager.getFontSizeMultiplier(), requireContext(),
            () -> {
                // Reload comments when one is removed
                viewModel.loadComments();
            },
            (surahNumber, ayahNumber) -> {
                // Navigate to Surah detail with scroll position
                Bundle args = new Bundle();
                args.putInt(Constants.KEY_SURAH_NUMBER, surahNumber);
                args.putInt("scroll_to_ayah", ayahNumber);
                Navigation.findNavController(requireView()).navigate(R.id.action_commentsFragment_to_surahDetailFragment, args);
            });
        recyclerView.setAdapter(adapter);

        // Setup ViewModel
        QuranRepository repository = new QuranRepository(requireContext());
        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(CommentsViewModel.class);

        // Load comments
        viewModel.loadComments();
    }

    @Override
    protected void observeData() {
        viewModel.getCommentedAyahs().observe(getViewLifecycleOwner(), ayahs -> {
            if (ayahs != null) {
                if (ayahs.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    tvEmptyComments.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvEmptyComments.setVisibility(View.GONE);
                    adapter.setAyahs(ayahs);
                }
            }
        });

        viewModel.getSurahNamesEnglish().observe(getViewLifecycleOwner(), surahNames -> {
            if (surahNames != null) {
                adapter.setSurahNamesEnglish(surahNames);
            }
        });

        viewModel.getSurahNamesArabic().observe(getViewLifecycleOwner(), surahNames -> {
            if (surahNames != null) {
                adapter.setSurahNamesArabic(surahNames);
            }
        });

        viewModel.getCommentsMap().observe(getViewLifecycleOwner(), comments -> {
            if (comments != null) {
                adapter.setCommentsMap(comments);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload comments when fragment becomes visible again
        if (viewModel != null) {
            viewModel.loadComments();
        }
    }
}
