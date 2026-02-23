package com.example.quran.ui.surahdetail;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quran.R;
import com.example.quran.data.repository.QuranRepository;
import com.example.quran.ui.base.BaseFragment;
import com.example.quran.utils.Constants;
import com.example.quran.utils.SettingsManager;
import com.example.quran.utils.SurahFontHelper;
import com.example.quran.utils.ViewModelFactory;

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

    // Overscroll navigation fields
    private TextView tvNextSurahIndicator;
    private TextView tvPreviousSurahIndicator;
    private float overscrollDistance = 0f;
    private float initialTouchY = 0f;
    private boolean isOverscrolling = false;
    private boolean hasTriggeredHaptic = false;
    private static final float THRESHOLD_DP = 150f; // 150dp threshold for navigation

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_surah_detail, container, false);
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
                    adapter.setSurahName(surah.getNameArabic());
                    // Show surah number and ayah count in Arabic numerals
                    tvSurahNumber.setText(convertToArabicNumerals(String.valueOf(surah.getNumber())));
                    tvSurahInfo.setText(convertToArabicNumerals(String.valueOf(surah.getTotalAyahs())));
                } else {
                    tvSurahName.setText(surah.getNameEnglish());
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
                break;

            case MotionEvent.ACTION_MOVE:
                float currentY = event.getY();
                float deltaY = currentY - initialTouchY;

                // Check if at top or bottom
                boolean isAtTop = !recyclerView.canScrollVertically(-1);
                boolean isAtBottom = !recyclerView.canScrollVertically(1);

                // Handle overscroll at bottom (pull down for next surah)
                if (isAtBottom && deltaY < 0) {
                    isOverscrolling = true;
                    overscrollDistance = Math.abs(deltaY);
                    updateNextSurahIndicator(overscrollDistance);
                }
                // Handle overscroll at top (pull up for previous surah)
                else if (isAtTop && deltaY > 0) {
                    isOverscrolling = true;
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
        boolean isAtTop = !recyclerView.canScrollVertically(-1);
        boolean isAtBottom = !recyclerView.canScrollVertically(1);

        // Reset RecyclerView position immediately before navigation
        recyclerView.setTranslationY(0f);

        if (isAtBottom && surahNumber < 114) {
            // Navigate to next surah with slide up animation
            navigateToNextSurah(surahNumber + 1);
        } else if (isAtTop && surahNumber > 1) {
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
}
