package com.example.quran.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Helper class for creating images with text overlays.
 * Handles loading background images, drawing Arabic text with semi-transparent backgrounds,
 * and saving the final image to the device gallery.
 */
public class ImageOverlayHelper {

    private static final String TAG = "ImageOverlayHelper";
    private static final int MAX_IMAGE_DIMENSION = 2048;
    private static final float TEXT_BOX_PADDING_DP = 24f;
    private static final float TEXT_BOX_CORNER_RADIUS_DP = 16f;
    private static final int TEXT_BOX_BACKGROUND_COLOR = 0x80000000; // 50% black
    private static final int TEXT_COLOR = Color.WHITE;
    private static final String QURAN_FOLDER = "Quran";

    /**
     * Create an image with text overlay.
     *
     * @param context Android context
     * @param imageUri URI of the background image
     * @param arabicText The Arabic ayah text to overlay
     * @param surahInfo The surah name and ayah number
     * @param textX X position (0-1 relative to image width)
     * @param textY Y position (0-1 relative to image height)
     * @param textSizeDp Text size in DP
     * @return Bitmap of the final image with overlay, or null on error
     */
    public static Bitmap createOverlayImage(
            Context context,
            Uri imageUri,
            String arabicText,
            String surahInfo,
            float textX,
            float textY,
            int textSizeDp
    ) {
        try {
            // Load and scale background image
            Bitmap backgroundImage = loadAndScaleImage(context, imageUri);
            if (backgroundImage == null) {
                Log.e(TAG, "Failed to load background image");
                return null;
            }

            // Create mutable copy for drawing
            Bitmap mutableBitmap = backgroundImage.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);

            // Load Arabic font
            Typeface arabicFont = loadArabicFont(context);

            // Calculate text size in pixels
            float density = context.getResources().getDisplayMetrics().density;
            int textSizePx = (int) (textSizeDp * density);

            // Create text paints
            TextPaint arabicPaint = createTextPaint(arabicFont, TEXT_COLOR, textSizePx);
            TextPaint infoPaint = createTextPaint(
                    Typeface.DEFAULT,
                    TEXT_COLOR,
                    (int) (textSizePx * 0.6f)
            );

            // Calculate text bounds
            int maxTextWidth = (int) (mutableBitmap.getWidth() * 0.8f);
            StaticLayout arabicLayout = createStaticLayout(arabicText, arabicPaint, maxTextWidth);
            StaticLayout infoLayout = createStaticLayout(surahInfo, infoPaint, maxTextWidth);

            // Calculate total text box dimensions
            float textBoxPadding = TEXT_BOX_PADDING_DP * density;
            float totalTextHeight = arabicLayout.getHeight() + infoLayout.getHeight() + (textBoxPadding / 2);
            float totalTextWidth = Math.max(arabicLayout.getWidth(), infoLayout.getWidth());

            // Calculate text box position
            float boxX = (textX * mutableBitmap.getWidth()) - (totalTextWidth / 2);
            float boxY = (textY * mutableBitmap.getHeight()) - (totalTextHeight / 2);

            // Constrain box to image bounds
            boxX = Math.max(0, Math.min(boxX, mutableBitmap.getWidth() - totalTextWidth - textBoxPadding * 2));
            boxY = Math.max(0, Math.min(boxY, mutableBitmap.getHeight() - totalTextHeight - textBoxPadding * 2));

            // Create background box rect
            RectF backgroundBox = new RectF(
                    boxX,
                    boxY,
                    boxX + totalTextWidth + textBoxPadding * 2,
                    boxY + totalTextHeight + textBoxPadding * 2
            );

            // Draw semi-transparent background
            Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bgPaint.setColor(TEXT_BOX_BACKGROUND_COLOR);
            canvas.drawRoundRect(
                    backgroundBox,
                    TEXT_BOX_CORNER_RADIUS_DP * density,
                    TEXT_BOX_CORNER_RADIUS_DP * density,
                    bgPaint
            );

            // Draw Arabic text
            canvas.save();
            canvas.translate(boxX + textBoxPadding, boxY + textBoxPadding);
            arabicLayout.draw(canvas);
            canvas.restore();

            // Draw surah info below Arabic text
            canvas.save();
            canvas.translate(
                    boxX + textBoxPadding,
                    boxY + textBoxPadding + arabicLayout.getHeight() + (textBoxPadding / 2)
            );
            infoLayout.draw(canvas);
            canvas.restore();

            return mutableBitmap;

        } catch (Exception e) {
            Log.e(TAG, "Error creating overlay image", e);
            return null;
        }
    }

    /**
     * Load and scale image from URI if it's too large.
     * Properly handles EXIF orientation to ensure image is correctly rotated.
     */
    private static Bitmap loadAndScaleImage(Context context, Uri imageUri) {
        try {
            ContentResolver resolver = context.getContentResolver();

            // Step 1: Get EXIF orientation before loading bitmap
            int orientation = ExifInterface.ORIENTATION_NORMAL;
            try {
                InputStream exifStream = resolver.openInputStream(imageUri);
                if (exifStream != null) {
                    ExifInterface exif = new ExifInterface(exifStream);
                    orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                    );
                    exifStream.close();
                }
            } catch (IOException e) {
                Log.w(TAG, "Could not read EXIF data, assuming normal orientation", e);
            }

            // Step 2: Decode bitmap with scaling
            InputStream inputStream = resolver.openInputStream(imageUri);
            if (inputStream == null) {
                return null;
            }

            // First decode to get dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            // Calculate scale factor
            int scale = 1;
            while (options.outWidth / scale > MAX_IMAGE_DIMENSION ||
                    options.outHeight / scale > MAX_IMAGE_DIMENSION) {
                scale *= 2;
            }

            // Decode with scale factor
            inputStream = resolver.openInputStream(imageUri);
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            if (bitmap == null) {
                return null;
            }

            // Step 3: Apply EXIF rotation
            bitmap = rotateBitmapByExif(bitmap, orientation);

            return bitmap;

        } catch (IOException e) {
            Log.e(TAG, "Error loading image", e);
            return null;
        }
    }

    /**
     * Rotate bitmap based on EXIF orientation tag.
     */
    private static Bitmap rotateBitmapByExif(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.postScale(1, -1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.postRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.postRotate(270);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            case ExifInterface.ORIENTATION_UNDEFINED:
            default:
                return bitmap; // No rotation needed
        }

        try {
            Bitmap rotatedBitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    matrix,
                    true
            );

            // Recycle original bitmap if a new one was created
            if (rotatedBitmap != bitmap) {
                bitmap.recycle();
            }

            return rotatedBitmap;

        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Out of memory while rotating bitmap", e);
            return bitmap; // Return original if rotation fails
        }
    }

    /**
     * Load the Arabic font from assets.
     */
    private static Typeface loadArabicFont(Context context) {
        try {
            return Typeface.createFromAsset(context.getAssets(), "fonts/uthmantaha.ttf");
        } catch (Exception e) {
            Log.e(TAG, "Error loading Arabic font, using default", e);
            return Typeface.DEFAULT;
        }
    }

    /**
     * Create a TextPaint with specified font, color, and size.
     */
    private static TextPaint createTextPaint(Typeface font, int color, int sizePx) {
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(font);
        paint.setColor(color);
        paint.setTextSize(sizePx);
        return paint;
    }

    /**
     * Create a StaticLayout for multi-line text rendering.
     */
    private static StaticLayout createStaticLayout(String text, TextPaint paint, int maxWidth) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return StaticLayout.Builder.obtain(text, 0, text.length(), paint, maxWidth)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setLineSpacing(0, 1)
                    .setIncludePad(false)
                    .build();
        } else {
            return new StaticLayout(
                    text,
                    paint,
                    maxWidth,
                    Layout.Alignment.ALIGN_CENTER,
                    1.0f,
                    0.0f,
                    false
            );
        }
    }

    /**
     * Save the bitmap to the device gallery.
     *
     * @param context Android context
     * @param bitmap The bitmap to save
     * @param fileName The filename for the saved image
     * @return URI of the saved image, or null on error
     */
    public static Uri saveImageToGallery(Context context, Bitmap bitmap, String fileName) {
        try {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ (Scoped Storage)
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/" + QURAN_FOLDER);
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1);
            }

            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (imageUri == null) {
                Log.e(TAG, "Failed to create MediaStore entry");
                return null;
            }

            // Write bitmap to output stream
            try (OutputStream outputStream = resolver.openOutputStream(imageUri)) {
                if (outputStream == null) {
                    Log.e(TAG, "Failed to open output stream");
                    return null;
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
            }

            // Mark as not pending (Android 10+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear();
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0);
                resolver.update(imageUri, contentValues, null, null);
            }

            Log.d(TAG, "Image saved successfully: " + imageUri);
            return imageUri;

        } catch (Exception e) {
            Log.e(TAG, "Error saving image to gallery", e);
            return null;
        }
    }
}
