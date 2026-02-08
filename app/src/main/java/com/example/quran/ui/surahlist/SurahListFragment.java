package com.example.quran.ui.surahlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quran.R;
import com.example.quran.data.model.Surah;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseFragment;
import com.example.quran.utils.Constants;
import com.example.quran.utils.SettingsManager;
import com.example.quran.utils.ViewModelFactory;

/**
 * Fragment displaying list of all Surahs.
 */
public class SurahListFragment extends BaseFragment implements SurahAdapter.OnSurahClickListener {

    private SurahListViewModel viewModel;
    private RecyclerView recyclerView;
    private SurahAdapter adapter;
    private SettingsManager settingsManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_surah_list, container, false);
    }

    @Override
    protected void setupUI(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewSurahs);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Setup SettingsManager
        settingsManager = new SettingsManager(requireContext());

        adapter = new SurahAdapter(this, settingsManager.getFontSizeMultiplier());
        recyclerView.setAdapter(adapter);

        // Setup ViewModel
        QuranRepository repository = new QuranRepository(requireContext());
        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(SurahListViewModel.class);
    }

    @Override
    protected void observeData() {
        viewModel.getAllSurahs().observe(getViewLifecycleOwner(), surahs -> {
            if (surahs != null) {
                adapter.setSurahs(surahs);
            }
        });
    }

    @Override
    public void onSurahClick(Surah surah) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_SURAH_NUMBER, surah.getNumber());
        bundle.putString(Constants.KEY_SURAH_NAME, surah.getNameArabic());
        Navigation.findNavController(requireView()).navigate(R.id.action_surahListFragment_to_surahDetailFragment, bundle);
    }
}
