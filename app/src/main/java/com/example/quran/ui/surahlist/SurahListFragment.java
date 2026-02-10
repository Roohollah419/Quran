package com.example.quran.ui.surahlist;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
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
    private View headerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_surah_list, container, false);
    }

    @Override
    protected void setupUI(View view) {
        headerView = view.findViewById(R.id.headerSurahList);
        recyclerView = view.findViewById(R.id.recyclerViewSurahs);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Setup SettingsManager
        settingsManager = new SettingsManager(requireContext());

        // Update header based on language
        updateHeader();

        adapter = new SurahAdapter(this, settingsManager.getFontSizeMultiplier(), requireContext());
        recyclerView.setAdapter(adapter);

        // Setup ViewModel
        QuranRepository repository = new QuranRepository(requireContext());
        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(SurahListViewModel.class);
    }

    private void updateHeader() {
        boolean isArabic = settingsManager.isArabicLanguage();
        Typeface arabicTypeface = ResourcesCompat.getFont(requireContext(), R.font.uthmantaha);

        // headerView is already the root LinearLayout from the included layout
        TextView tvHeaderSurahName = headerView.findViewById(R.id.tvHeaderSurahName);
        TextView tvHeaderAyahCount = headerView.findViewById(R.id.tvHeaderAyahCount);
        TextView tvHeaderSurahNumber = headerView.findViewById(R.id.tvHeaderSurahNumber);
        TextView tvHeaderRevelationType = headerView.findViewById(R.id.tvHeaderRevelationType);

        // Set layout direction based on language
        if (isArabic) {
            headerView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            tvHeaderSurahName.setText(R.string.header_surah_name_ar);
            tvHeaderAyahCount.setText(R.string.header_ayah_count_ar);
            tvHeaderSurahNumber.setText(R.string.header_surah_number_ar);
            tvHeaderRevelationType.setText(R.string.header_revelation_type_ar);

            tvHeaderSurahName.setTypeface(arabicTypeface);
            tvHeaderAyahCount.setTypeface(arabicTypeface);
            tvHeaderSurahNumber.setTypeface(arabicTypeface);
            tvHeaderRevelationType.setTypeface(arabicTypeface);
        } else {
            headerView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            tvHeaderSurahName.setText(R.string.header_surah_name);
            tvHeaderAyahCount.setText(R.string.header_ayah_count);
            tvHeaderSurahNumber.setText(R.string.header_surah_number);
            tvHeaderRevelationType.setText(R.string.header_revelation_type);

            tvHeaderSurahName.setTypeface(Typeface.DEFAULT_BOLD);
            tvHeaderAyahCount.setTypeface(Typeface.DEFAULT_BOLD);
            tvHeaderSurahNumber.setTypeface(Typeface.DEFAULT_BOLD);
            tvHeaderRevelationType.setTypeface(Typeface.DEFAULT_BOLD);
        }
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
