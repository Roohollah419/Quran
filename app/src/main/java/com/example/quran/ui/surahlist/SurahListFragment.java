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
    private TextView tvHeaderSurahName;
    private TextView tvHeaderAyahCount;
    private TextView tvHeaderSurahNumber;
    private TextView tvHeaderRevelationType;

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

        // Get header TextViews
        tvHeaderSurahName = headerView.findViewById(R.id.tvHeaderSurahName);
        tvHeaderAyahCount = headerView.findViewById(R.id.tvHeaderAyahCount);
        tvHeaderSurahNumber = headerView.findViewById(R.id.tvHeaderSurahNumber);
        tvHeaderRevelationType = headerView.findViewById(R.id.tvHeaderRevelationType);

        // Setup ViewModel FIRST
        QuranRepository repository = new QuranRepository(requireContext());
        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(SurahListViewModel.class);

        // Update header based on language (after ViewModel is set)
        updateHeader();

        // Setup header click listeners
        setupHeaderClickListeners();

        adapter = new SurahAdapter(this, settingsManager.getFontSizeMultiplier(), requireContext());
        recyclerView.setAdapter(adapter);
    }

    private void setupHeaderClickListeners() {
        tvHeaderSurahName.setOnClickListener(v -> viewModel.sortBy(SurahListViewModel.SortField.NAME));
        tvHeaderSurahNumber.setOnClickListener(v -> viewModel.sortBy(SurahListViewModel.SortField.NUMBER));
        tvHeaderAyahCount.setOnClickListener(v -> viewModel.sortBy(SurahListViewModel.SortField.AYAH_COUNT));
        tvHeaderRevelationType.setOnClickListener(v -> viewModel.sortBy(SurahListViewModel.SortField.REVELATION_TYPE));
    }

    private void updateHeader() {
        boolean isArabic = settingsManager.isArabicLanguage();
        Typeface arabicTypeface = ResourcesCompat.getFont(requireContext(), R.font.uthmantaha);

        // Set layout direction based on language
        if (isArabic) {
            headerView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            tvHeaderSurahName.setTypeface(arabicTypeface);
            tvHeaderAyahCount.setTypeface(arabicTypeface);
            tvHeaderSurahNumber.setTypeface(arabicTypeface);
            tvHeaderRevelationType.setTypeface(arabicTypeface);
        } else {
            headerView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            tvHeaderSurahName.setTypeface(Typeface.DEFAULT_BOLD);
            tvHeaderAyahCount.setTypeface(Typeface.DEFAULT_BOLD);
            tvHeaderSurahNumber.setTypeface(Typeface.DEFAULT_BOLD);
            tvHeaderRevelationType.setTypeface(Typeface.DEFAULT_BOLD);
        }

        updateHeaderLabels();
    }

    private void updateHeaderLabels() {
        boolean isArabic = settingsManager.isArabicLanguage();
        SurahListViewModel.SortField currentField = viewModel.getCurrentSortField().getValue();
        Boolean isAscending = viewModel.getIsAscending().getValue();

        String arrow = "";
        if (isAscending != null) {
            arrow = isAscending ? " ▲" : " ▼";
        }

        // Surah Name
        String nameText = isArabic ? getString(R.string.header_surah_name_ar) : getString(R.string.header_surah_name);
        if (currentField == SurahListViewModel.SortField.NAME) {
            nameText += arrow;
        }
        tvHeaderSurahName.setText(nameText);

        // Surah Number
        String numberText = isArabic ? getString(R.string.header_surah_number_ar) : getString(R.string.header_surah_number);
        if (currentField == SurahListViewModel.SortField.NUMBER) {
            numberText += arrow;
        }
        tvHeaderSurahNumber.setText(numberText);

        // Ayah Count
        String countText = isArabic ? getString(R.string.header_ayah_count_ar) : getString(R.string.header_ayah_count);
        if (currentField == SurahListViewModel.SortField.AYAH_COUNT) {
            countText += arrow;
        }
        tvHeaderAyahCount.setText(countText);

        // Revelation Type
        String typeText = isArabic ? getString(R.string.header_revelation_type_ar) : getString(R.string.header_revelation_type);
        if (currentField == SurahListViewModel.SortField.REVELATION_TYPE) {
            typeText += arrow;
        }
        tvHeaderRevelationType.setText(typeText);
    }

    @Override
    protected void observeData() {
        viewModel.getSortedSurahs().observe(getViewLifecycleOwner(), surahs -> {
            if (surahs != null) {
                adapter.setSurahs(surahs);
            }
        });

        viewModel.getCurrentSortField().observe(getViewLifecycleOwner(), field -> updateHeaderLabels());
        viewModel.getIsAscending().observe(getViewLifecycleOwner(), ascending -> updateHeaderLabels());
    }

    @Override
    public void onSurahClick(Surah surah) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_SURAH_NUMBER, surah.getNumber());
        bundle.putString(Constants.KEY_SURAH_NAME, surah.getNameArabic());
        Navigation.findNavController(requireView()).navigate(R.id.action_surahListFragment_to_surahDetailFragment, bundle);
    }
}
