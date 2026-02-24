package com.example.quran.ui.imageeditor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quran.R;
import com.example.quran.utils.ImageOverlayHelper;

/**
 * Activity for previewing and positioning text overlay on background image.
 * Users can drag, resize, and rotate the text overlay before saving and sharing.
 */
public class ImageEditorActivity extends AppCompatActivity {

    private static final String TAG = "ImageEditorActivity";
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 3.0f;

    // Intent extras
    public static final String EXTRA_IMAGE_URI = "image_uri";
    public static final String EXTRA_ARABIC_TEXT = "arabic_text";
    public static final String EXTRA_SURAH_NAME = "surah_name";
    public static final String EXTRA_AYAH_NUMBER = "ayah_number";

    // Views
    private ImageView ivBackgroundImage;
    private FrameLayout flTextOverlay;
    private TextView tvArabicPreview;
    private TextView tvSurahInfoPreview;
    private Button btnSaveAndShare;
    private ProgressBar progressBar;

    // Data
    private Uri imageUri;
    private String arabicText;
    private String surahInfo;

    // Touch handling for drag
    private float dX, dY;
    private float lastTouchX, lastTouchY;

    // Scale and rotation handling
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private float rotation = 0f;

    // For rotation gesture detection
    private float lastRotation = 0f;
    private boolean isRotating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);

        // Get intent data
        Intent intent = getIntent();
        String imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI);
        arabicText = intent.getStringExtra(EXTRA_ARABIC_TEXT);
        String surahName = intent.getStringExtra(EXTRA_SURAH_NAME);
        int ayahNumber = intent.getIntExtra(EXTRA_AYAH_NUMBER, 0);

        if (imageUriString == null || arabicText == null || surahName == null) {
            Toast.makeText(this, "Invalid data provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imageUri = Uri.parse(imageUriString);
        surahInfo = surahName + " • Ayah " + ayahNumber;

        // Initialize views
        initViews();

        // Load background image
        loadBackgroundImage();

        // Setup text overlay
        setupTextOverlay();

        // Setup button click listener
        btnSaveAndShare.setOnClickListener(v -> saveAndShareImage());
    }

    private void initViews() {
        ivBackgroundImage = findViewById(R.id.ivBackgroundImage);
        flTextOverlay = findViewById(R.id.flTextOverlay);
        tvArabicPreview = findViewById(R.id.tvArabicPreview);
        tvSurahInfoPreview = findViewById(R.id.tvSurahInfoPreview);
        btnSaveAndShare = findViewById(R.id.btnSaveAndShare);
        progressBar = findViewById(R.id.progressBar);
    }

    private void loadBackgroundImage() {
        try {
            // Load image with proper EXIF orientation handling
            Bitmap bitmap = ImageOverlayHelper.loadImageWithOrientation(this, imageUri);
            if (bitmap != null) {
                ivBackgroundImage.setImageBitmap(bitmap);
            } else {
                throw new Exception("Failed to decode image");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading background image", e);
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupTextOverlay() {
        // Set text content
        tvArabicPreview.setText(arabicText);
        tvSurahInfoPreview.setText(surahInfo);

        // Initialize scale gesture detector
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(MIN_SCALE, Math.min(scaleFactor, MAX_SCALE));

                // Apply scale
                flTextOverlay.setScaleX(scaleFactor);
                flTextOverlay.setScaleY(scaleFactor);
                return true;
            }
        });

        // Setup touch listener for drag, scale, and rotation
        flTextOverlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                // Handle scale gesture
                scaleGestureDetector.onTouchEvent(event);

                int action = event.getActionMasked();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        lastTouchX = event.getRawX();
                        lastTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        // Two fingers down - check for rotation
                        if (event.getPointerCount() == 2) {
                            isRotating = true;
                            lastRotation = getRotationBetweenFingers(event);
                        }
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if (event.getPointerCount() == 2 && isRotating) {
                            // Handle rotation with two fingers
                            float newRotation = getRotationBetweenFingers(event);
                            float rotationDelta = newRotation - lastRotation;

                            // Normalize rotation delta to -180 to 180 range
                            if (rotationDelta > 180) {
                                rotationDelta -= 360;
                            } else if (rotationDelta < -180) {
                                rotationDelta += 360;
                            }

                            rotation += rotationDelta;
                            lastRotation = newRotation;

                            // Apply rotation
                            view.setRotation(rotation);
                        } else if (event.getPointerCount() == 1 && !scaleGestureDetector.isInProgress()) {
                            // Handle drag with single finger
                            float newX = event.getRawX() + dX;
                            float newY = event.getRawY() + dY;

                            // Keep within bounds (loose bounds to account for rotation)
                            View parent = (View) view.getParent();
                            newX = Math.max(-view.getWidth(), Math.min(newX, parent.getWidth()));
                            newY = Math.max(-view.getHeight(), Math.min(newY, parent.getHeight()));

                            view.setX(newX);
                            view.setY(newY);
                        }
                        return true;

                    case MotionEvent.ACTION_POINTER_UP:
                        isRotating = false;
                        return true;

                    case MotionEvent.ACTION_UP:
                        isRotating = false;
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    /**
     * Calculate rotation angle between two touch points.
     */
    private float getRotationBetweenFingers(MotionEvent event) {
        if (event.getPointerCount() < 2) {
            return 0;
        }

        float deltaX = event.getX(1) - event.getX(0);
        float deltaY = event.getY(1) - event.getY(0);
        return (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
    }

    private void saveAndShareImage() {
        // Calculate text position as relative coordinates (0-1)
        View parent = (View) flTextOverlay.getParent();
        float textX = (flTextOverlay.getX() + flTextOverlay.getWidth() / 2f) / parent.getWidth();
        float textY = (flTextOverlay.getY() + flTextOverlay.getHeight() / 2f) / parent.getHeight();

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnSaveAndShare.setEnabled(false);

        // Generate image in background
        new GenerateImageTask(textX, textY, scaleFactor, rotation).execute();
    }

    /**
     * AsyncTask for generating and saving image in background.
     */
    private class GenerateImageTask extends AsyncTask<Void, Void, Uri> {

        private final float textX;
        private final float textY;
        private final float scale;
        private final float rotation;

        public GenerateImageTask(float textX, float textY, float scale, float rotation) {
            this.textX = textX;
            this.textY = textY;
            this.scale = scale;
            this.rotation = rotation;
        }

        @Override
        protected Uri doInBackground(Void... voids) {
            try {
                // Generate overlay image
                Bitmap resultBitmap = ImageOverlayHelper.createOverlayImage(
                        ImageEditorActivity.this,
                        imageUri,
                        arabicText,
                        surahInfo,
                        textX,
                        textY,
                        24, // Text size in DP
                        scale,
                        rotation
                );

                if (resultBitmap == null) {
                    return null;
                }

                // Save to gallery
                String fileName = "Quran_" + System.currentTimeMillis() + ".jpg";
                Uri savedUri = ImageOverlayHelper.saveImageToGallery(
                        ImageEditorActivity.this,
                        resultBitmap,
                        fileName
                );

                // Recycle bitmap to free memory
                resultBitmap.recycle();

                return savedUri;

            } catch (Exception e) {
                Log.e(TAG, "Error generating image", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Uri savedUri) {
            // Hide progress
            progressBar.setVisibility(View.GONE);
            btnSaveAndShare.setEnabled(true);

            if (savedUri == null) {
                Toast.makeText(
                        ImageEditorActivity.this,
                        R.string.image_save_failed,
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            // Show success message
            Toast.makeText(
                    ImageEditorActivity.this,
                    R.string.image_saved,
                    Toast.LENGTH_SHORT
            ).show();

            // Open share dialog
            shareImage(savedUri);

            // Finish activity
            finish();
        }
    }

    private void shareImage(Uri imageUri) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_ayah)));

        } catch (Exception e) {
            Log.e(TAG, "Error sharing image", e);
            Toast.makeText(this, "Failed to share image", Toast.LENGTH_SHORT).show();
        }
    }
}
