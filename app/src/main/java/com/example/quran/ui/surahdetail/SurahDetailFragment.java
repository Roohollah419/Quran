package com.example.quran.ui.surahdetail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quran.R;
import com.example.quran.data.model.Ayah;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseFragment;
import com.example.quran.ui.imageeditor.ImageEditorActivity;
import com.example.quran.utils.Constants;
import com.example.quran.utils.PermissionHelper;
import com.example.quran.utils.SettingsManager;
import com.example.quran.utils.SurahFontHelper;
import com.example.quran.utils.ViewModelFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Fragment displaying details of a specific Surah with its Ayahs.
 */
public class SurahDetailFragment extends BaseFragment {

    private SurahDetailViewModel viewModel;
    private SettingsManager settingsManager;
    private TextView tvSurahName;
    private TextView tvSurahNumber;
    private TextView tvSurahInfo;
    private ImageView ivRevelationType;
    private RecyclerView recyclerView;
    private AyahAdapter adapter;
    private Typeface arabicTypeface;

    private int surahNumber;
    private int scrollToAyah = -1;

    // Image creation fields
    private Uri tempPhotoUri;
    private Ayah currentAyahForImage;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<String> storagePermissionLauncher;
    private String currentSurahName = "";

    // Overscroll navigation fields
    private TextView tvNextSurahIndicator;
    private TextView tvPreviousSurahIndicator;
    private float overscrollDistance = 0f;
    private float initialTouchY = 0f;
    private boolean isOverscrolling = false;
    private boolean hasTriggeredHaptic = false;
    private static final float THRESHOLD_DP = 150f; // 150dp threshold for navigation
    private static final int OVERSCROLL_NONE = 0;
    private static final int OVERSCROLL_NEXT = 1;
    private static final int OVERSCROLL_PREVIOUS = 2;
    private int overscrollDirection = OVERSCROLL_NONE;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Register activity result launchers before view creation
        registerActivityResultLaunchers();
        return inflater.inflate(R.layout.fragment_surah_detail, container, false);
    }

    private void registerActivityResultLaunchers() {
        // Camera permission launcher
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted, launch camera
                        launchCameraIntent();
                    } else {
                        // Permission denied
                        Toast.makeText(requireContext(), R.string.camera_permission_required, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Storage permission launcher
        storagePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted, launch gallery
                        launchGalleryIntent();
                    } else {
                        // Permission denied
                        Toast.makeText(requireContext(), R.string.storage_permission_required, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            launchImageEditor(imageUri);
                        }
                    }
                }
        );

        // Camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && tempPhotoUri != null) {
                        launchImageEditor(tempPhotoUri);
                    }
                }
        );
    }

    @Override
    protected void setupUI(View view) {
        tvSurahName = view.findViewById(R.id.tvSurahName);
        tvSurahNumber = view.findViewById(R.id.tvSurahNumber);
        tvSurahInfo = view.findViewById(R.id.tvSurahInfo);
        ivRevelationType = view.findViewById(R.id.ivRevelationType);
        recyclerView = view.findViewById(R.id.recyclerViewAyahs);
        tvNextSurahIndicator = view.findViewById(R.id.tvNextSurahIndicator);
        tvPreviousSurahIndicator = view.findViewById(R.id.tvPreviousSurahIndicator);

        // Setup SettingsManager
        settingsManager = new SettingsManager(requireContext());

        // Load Arabic font
        arabicTypeface = ResourcesCompat.getFont(requireContext(), R.font.uthmantaha);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AyahAdapter(settingsManager.getFontSizeMultiplier(), requireContext());
        recyclerView.setAdapter(adapter);

        // Set create image callback
        adapter.setOnCreateImageClickListener(ayah -> {
            currentAyahForImage = ayah;
            showImageSourceDialog();
        });

        // Setup overscroll navigation
        setupOverscrollNavigation();

        // Apply font size to header views
        applyFontSize();

        // Get arguments
        if (getArguments() != null) {
            surahNumber = getArguments().getInt(Constants.KEY_SURAH_NUMBER, 1);
            scrollToAyah = getArguments().getInt("scroll_to_ayah", -1);
        }

        // Setup ViewModel
        QuranRepository repository = new QuranRepository(requireContext());
        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(SurahDetailViewModel.class);

        // Load Surah data
        viewModel.loadSurah(surahNumber);
    }

    @Override
    protected void observeData() {
        viewModel.getSurah().observe(getViewLifecycleOwner(), surah -> {
            if (surah != null) {
                // Update the current surah number to the loaded surah
                surahNumber = surah.getNumber();

                boolean isArabic = settingsManager.isArabicLanguage();

                // Set surah name based on language
                if (isArabic) {
                    // Use custom calligraphy font for display
                    tvSurahName.setText(SurahFontHelper.getCharacter(surah.getNumber()));
                    tvSurahName.setTypeface(SurahFontHelper.getTypeface(requireContext()));
                    // Increase font size for calligraphy
                    tvSurahName.setTextSize(18 * settingsManager.getFontSizeMultiplier() * 1.5f);
                    // Keep plain Arabic text for sharing
                    currentSurahName = surah.getNameArabic();
                    adapter.setSurahName(surah.getNameArabic());
                    // Show surah number and ayah count in Arabic numerals
                    tvSurahNumber.setText(convertToArabicNumerals(String.valueOf(surah.getNumber())));
                    tvSurahInfo.setText(convertToArabicNumerals(String.valueOf(surah.getTotalAyahs())));
                } else {
                    tvSurahName.setText(surah.getNameEnglish());
                    currentSurahName = surah.getNameEnglish();
                    adapter.setSurahName(surah.getNameEnglish());
                    tvSurahName.setTypeface(Typeface.DEFAULT_BOLD);
                    tvSurahName.setTextSize(18 * settingsManager.getFontSizeMultiplier());
                    // Show surah number and ayah count in English numerals
                    tvSurahNumber.setText(String.valueOf(surah.getNumber()));
                    tvSurahInfo.setText(String.valueOf(surah.getTotalAyahs()));
                }

                // Load revelation type icon
                String revelationType = surah.getRevelationType();
                try {
                    InputStream inputStream;
                    if (revelationType.equalsIgnoreCase("Meccan")) {
                        inputStream = requireContext().getAssets().open("mecca.png");
                    } else {
                        inputStream = requireContext().getAssets().open("madina.png");
                    }
                    Drawable drawable = Drawable.createFromStream(inputStream, null);
                    ivRevelationType.setImageDrawable(drawable);
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        viewModel.getAyahs().observe(getViewLifecycleOwner(), ayahs -> {
            if (ayahs != null) {
                adapter.setAyahs(ayahs);

                // Scroll to specific Ayah if requested
                if (scrollToAyah > 0) {
                    // Find the position of the Ayah in the list
                    for (int i = 0; i < ayahs.size(); i++) {
                        if (ayahs.get(i).getAyahNumber() == scrollToAyah) {
                            final int position = i;
                            // Post to ensure RecyclerView is laid out
                            recyclerView.post(() -> {
                                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                if (layoutManager != null) {
                                    layoutManager.scrollToPositionWithOffset(position, 0);
                                }
                            });
                            scrollToAyah = -1; // Reset so it doesn't scroll again
                            break;
                        }
                    }
                }
            }
        });
    }

    private void applyFontSize() {
        float multiplier = settingsManager.getFontSizeMultiplier();
        // tvSurahName font size is set in observeData based on language
        tvSurahNumber.setTextSize(16 * multiplier);
        tvSurahInfo.setTextSize(16 * multiplier);
    }

    private String convertToArabicNumerals(String number) {
        char[] arabicNumerals = {'٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'};
        StringBuilder result = new StringBuilder();
        for (char c : number.toCharArray()) {
            if (Character.isDigit(c)) {
                result.append(arabicNumerals[c - '0']);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private void setupOverscrollNavigation() {
        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                handleTouchEvent(e);
                return false; // Don't intercept, just observe
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Check if we need to navigate
                    if (isOverscrolling && overscrollDistance >= dpToPx(THRESHOLD_DP)) {
                        performNavigation();
                    }
                    // Reset overscroll state
                    resetOverscroll();
                }
            }
        });
    }

    private void handleTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialTouchY = event.getY();
                overscrollDistance = 0f;
                hasTriggeredHaptic = false;
                overscrollDirection = OVERSCROLL_NONE;
                break;

            case MotionEvent.ACTION_MOVE:
                float currentY = event.getY();
                float deltaY = currentY - initialTouchY;

                // Check if at top or bottom
                boolean isAtTop = !recyclerView.canScrollVertically(-1);
                boolean isAtBottom = !recyclerView.canScrollVertically(1);

                // Handle overscroll at bottom (pull down for next surah)
                if (isAtBottom && deltaY < 0 && surahNumber < 114) {
                    isOverscrolling = true;
                    overscrollDirection = OVERSCROLL_NEXT;
                    overscrollDistance = Math.abs(deltaY);
                    updateNextSurahIndicator(overscrollDistance);
                }
                // Handle overscroll at top (pull up for previous surah)
                else if (isAtTop && deltaY > 0 && surahNumber > 1) {
                    isOverscrolling = true;
                    overscrollDirection = OVERSCROLL_PREVIOUS;
                    overscrollDistance = Math.abs(deltaY);
                    updatePreviousSurahIndicator(overscrollDistance);
                }
                else {
                    resetOverscroll();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isOverscrolling && overscrollDistance >= dpToPx(THRESHOLD_DP)) {
                    performNavigation();
                }
                resetOverscroll();
                break;
        }
    }

    private void updateNextSurahIndicator(float distance) {
        float threshold = dpToPx(THRESHOLD_DP);
        float progress = Math.min(distance / threshold, 1.0f);

        // Limit translation to threshold distance
        float translationDistance = Math.min(distance, threshold);

        tvNextSurahIndicator.setVisibility(View.VISIBLE);

        // Move RecyclerView up to reveal indicator at bottom
        recyclerView.setTranslationY(-translationDistance);

        if (progress >= 1.0f) {
            tvNextSurahIndicator.setText("✓ Release for Next Surah");
            triggerHapticFeedback();
        } else {
            int percentage = (int) (progress * 100);
            tvNextSurahIndicator.setText(String.format("↓ Pull for Next Surah (%d%%)", percentage));
        }

        // Hide previous indicator
        tvPreviousSurahIndicator.setVisibility(View.GONE);
    }

    private void updatePreviousSurahIndicator(float distance) {
        float threshold = dpToPx(THRESHOLD_DP);
        float progress = Math.min(distance / threshold, 1.0f);

        // Limit translation to threshold distance
        float translationDistance = Math.min(distance, threshold);

        tvPreviousSurahIndicator.setVisibility(View.VISIBLE);

        // Move RecyclerView down to reveal indicator at top
        recyclerView.setTranslationY(translationDistance);

        if (progress >= 1.0f) {
            tvPreviousSurahIndicator.setText("✓ Release for Previous Surah");
            triggerHapticFeedback();
        } else {
            int percentage = (int) (progress * 100);
            tvPreviousSurahIndicator.setText(String.format("↑ Pull for Previous Surah (%d%%)", percentage));
        }

        // Hide next indicator
        tvNextSurahIndicator.setVisibility(View.GONE);
    }

    private void performNavigation() {
        // Reset RecyclerView position immediately before navigation
        recyclerView.setTranslationY(0f);

        // Use the stored overscroll direction instead of rechecking scroll state
        // This prevents race conditions where the RecyclerView state changes between detection and navigation
        if (overscrollDirection == OVERSCROLL_NEXT && surahNumber < 114) {
            // Navigate to next surah with slide up animation
            navigateToNextSurah(surahNumber + 1);
        } else if (overscrollDirection == OVERSCROLL_PREVIOUS && surahNumber > 1) {
            // Navigate to previous surah with slide down animation
            navigateToPreviousSurah(surahNumber - 1);
        }
    }

    private void navigateToNextSurah(int newSurahNumber) {
        Bundle args = new Bundle();
        args.putInt(Constants.KEY_SURAH_NUMBER, newSurahNumber);

        // Use slide up animation for next surah
        Navigation.findNavController(requireView())
                .navigate(R.id.action_surahDetailFragment_to_nextSurah, args);
    }

    private void navigateToPreviousSurah(int newSurahNumber) {
        Bundle args = new Bundle();
        args.putInt(Constants.KEY_SURAH_NUMBER, newSurahNumber);

        // Use slide down animation for previous surah
        Navigation.findNavController(requireView())
                .navigate(R.id.action_surahDetailFragment_to_previousSurah, args);
    }

    private void resetOverscroll() {
        isOverscrolling = false;
        overscrollDistance = 0f;
        hasTriggeredHaptic = false;
        overscrollDirection = OVERSCROLL_NONE;

        // Animate RecyclerView back to original position
        recyclerView.animate()
                .translationY(0f)
                .setDuration(200)
                .start();

        // Hide indicators
        tvNextSurahIndicator.setVisibility(View.GONE);
        tvPreviousSurahIndicator.setVisibility(View.GONE);
    }

    private void triggerHapticFeedback() {
        if (!hasTriggeredHaptic) {
            hasTriggeredHaptic = true;
            Vibrator vibrator = (Vibrator) requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(50);
                }
            }
        }
    }

    private float dpToPx(float dp) {
        return dp * requireContext().getResources().getDisplayMetrics().density;
    }

    // Image creation methods

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.select_image_source);
        builder.setItems(new String[]{
                getString(R.string.camera),
                getString(R.string.gallery)
        }, (dialog, which) -> {
            if (which == 0) {
                launchCamera();
            } else {
                launchGallery();
            }
        });
        builder.show();
    }

    private void launchCamera() {
        // Check camera permission
        if (!PermissionHelper.checkCameraPermission(requireActivity())) {
            // Request permission using modern API
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA);
            return;
        }

        // Permission already granted, launch camera
        launchCameraIntent();
    }

    private void launchCameraIntent() {
        try {
            // Create temp file for photo
            File photoFile = File.createTempFile(
                    "quran_photo_",
                    ".jpg",
                    requireContext().getExternalCacheDir()
            );

            tempPhotoUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    photoFile
            );

            cameraLauncher.launch(tempPhotoUri);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to create temp file", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchGallery() {
        // Check storage permission
        if (!PermissionHelper.checkStoragePermission(requireActivity())) {
            // Request permission using modern API based on Android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                storagePermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                storagePermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            return;
        }

        // Permission already granted, launch gallery
        launchGalleryIntent();
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void launchImageEditor(Uri imageUri) {
        if (currentAyahForImage == null) {
            return;
        }

        Intent intent = new Intent(requireContext(), ImageEditorActivity.class);
        intent.putExtra(ImageEditorActivity.EXTRA_IMAGE_URI, imageUri.toString());
        intent.putExtra(ImageEditorActivity.EXTRA_ARABIC_TEXT, currentAyahForImage.getTextArabic());
        intent.putExtra(ImageEditorActivity.EXTRA_SURAH_NAME, currentSurahName);
        intent.putExtra(ImageEditorActivity.EXTRA_AYAH_NUMBER, currentAyahForImage.getAyahNumber());
        startActivity(intent);
    }

}
