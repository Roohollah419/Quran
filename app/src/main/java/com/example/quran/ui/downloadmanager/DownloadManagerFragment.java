package com.example.quran.ui.downloadmanager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quran.R;
import com.example.quran.data.model.Recitation;
import com.example.quran.data.model.Surah;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.utils.Constants;
import com.example.quran.utils.ViewModelFactory;
import com.google.android.material.tabs.TabLayout;

/**
 * Dialog fragment for downloading and managing Quran audio recitations.
 */
public class DownloadManagerFragment extends DialogFragment
        implements RecitationDownloadAdapter.OnRecitationActionListener {

    private DownloadManagerViewModel viewModel;
    private RecitationDownloadAdapter adapter;
    private TabLayout tabLayoutReciters;
    private RecyclerView recyclerViewRecitations;
    private TextView tvDownloadedCount;
    private TextView tvStorageUsed;
    private Button btnDeleteAll;
    private ImageButton btnClose;

    public static DownloadManagerFragment newInstance() {
        return new DownloadManagerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_download_manager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tabLayoutReciters = view.findViewById(R.id.tabLayoutReciters);
        recyclerViewRecitations = view.findViewById(R.id.recyclerViewRecitations);
        tvDownloadedCount = view.findViewById(R.id.tvDownloadedCount);
        tvStorageUsed = view.findViewById(R.id.tvStorageUsed);
        btnDeleteAll = view.findViewById(R.id.btnDeleteAll);
        btnClose = view.findViewById(R.id.btnClose);

        // Initialize ViewModel
        ViewModelFactory factory = new ViewModelFactory(new QuranRepository(requireContext()));
        viewModel = new ViewModelProvider(this, factory).get(DownloadManagerViewModel.class);

        // Setup RecyclerView
        adapter = new RecitationDownloadAdapter(requireContext(), this);
        recyclerViewRecitations.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewRecitations.setAdapter(adapter);

        // Setup TabLayout
        setupTabLayout();

        // Setup listeners
        btnClose.setOnClickListener(v -> dismiss());
        btnDeleteAll.setOnClickListener(v -> confirmDeleteAll());

        // Observe ViewModel
        observeViewModel();
    }

    private void setupTabLayout() {
        tabLayoutReciters.addTab(tabLayoutReciters.newTab().setText(R.string.reciter_alafasy));
        tabLayoutReciters.addTab(tabLayoutReciters.newTab().setText(R.string.reciter_minshawi));

        tabLayoutReciters.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String reciter = tab.getPosition() == 0
                        ? Constants.RECITER_ALAFASY
                        : Constants.RECITER_MINSHAWI;
                viewModel.setSelectedReciter(reciter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void observeViewModel() {
        // Observe recitations
        viewModel.getCurrentRecitations().observe(getViewLifecycleOwner(), recitations -> {
            if (recitations != null) {
                adapter.setRecitations(recitations);
            }
        });

        // Observe surahs
        viewModel.getAllSurahs().observe(getViewLifecycleOwner(), surahs -> {
            if (surahs != null) {
                adapter.setSurahs(surahs);
            }
        });

        // Observe downloaded count
        viewModel.getDownloadedCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                String text = getString(R.string.downloaded_count, count, Constants.TOTAL_SURAHS);
                tvDownloadedCount.setText(text);
            }
        });

        // Observe storage used
        viewModel.getStorageUsed().observe(getViewLifecycleOwner(), storage -> {
            if (storage != null) {
                String text = getString(R.string.storage_used, storage);
                tvStorageUsed.setText(text);
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDownloadClick(Recitation recitation, Surah surah) {
        viewModel.downloadRecitation(recitation, surah);
        Toast.makeText(requireContext(), R.string.download_started, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(Recitation recitation, Surah surah) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_delete)
                .setMessage(R.string.confirm_delete_single_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    viewModel.deleteRecitation(recitation);
                    Toast.makeText(requireContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public void onCancelClick(Recitation recitation) {
        viewModel.cancelDownload(recitation);
        Toast.makeText(requireContext(), R.string.download_cancelled, Toast.LENGTH_SHORT).show();
    }

    private void confirmDeleteAll() {
        String reciterName = viewModel.getSelectedReciter().getValue();
        String displayName = reciterName != null && reciterName.equals(Constants.RECITER_ALAFASY)
                ? getString(R.string.reciter_alafasy)
                : getString(R.string.reciter_minshawi);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_delete)
                .setMessage(getString(R.string.confirm_delete_all_message, displayName))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    viewModel.deleteAllRecitations();
                    Toast.makeText(requireContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
        }
    }
}
