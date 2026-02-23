package com.example.quran.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Helper class for handling runtime permissions across different Android versions.
 */
public class PermissionHelper {

    // Permission request codes
    public static final int REQUEST_CAMERA_PERMISSION = 100;
    public static final int REQUEST_STORAGE_PERMISSION = 101;

    /**
     * Check if camera permission is granted.
     *
     * @param activity The activity context
     * @return true if camera permission is granted
     */
    public static boolean checkCameraPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if storage read permission is granted based on Android version.
     *
     * @param activity The activity context
     * @return true if storage read permission is granted
     */
    public static boolean checkStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires READ_MEDIA_IMAGES
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            // Android 12 and below require READ_EXTERNAL_STORAGE
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Request camera permission.
     *
     * @param activity The activity context
     * @param requestCode The request code for permission result callback
     */
    public static void requestCameraPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.CAMERA},
                requestCode
        );
    }

    /**
     * Request storage read permission based on Android version.
     *
     * @param activity The activity context
     * @param requestCode The request code for permission result callback
     */
    public static void requestStoragePermission(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires READ_MEDIA_IMAGES
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    requestCode
            );
        } else {
            // Android 12 and below require READ_EXTERNAL_STORAGE
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    requestCode
            );
        }
    }

    /**
     * Check if permission request result was granted.
     *
     * @param grantResults The grant results array from onRequestPermissionsResult
     * @return true if permission was granted
     */
    public static boolean isPermissionGranted(int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
