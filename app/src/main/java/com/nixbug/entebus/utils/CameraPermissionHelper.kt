package com.nixbug.entebus.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Utility object to handle Camera permission related operations.
 *
 * This helper centralizes permission checks and common actions
 * such as detecting permanent denial and navigating the user
 * to the app's settings screen.
 */
object CameraPermissionHelper {

    /**
     * Android Camera permission constant.
     */
    const val CAMERA_PERMISSION = android.Manifest.permission.CAMERA

    /**
     * Checks whether the Camera permission is currently granted.
     *
     * @param context Context used to access the permission status.
     * @return `true` if the Camera permission has been granted,
     *         `false` otherwise.
     */
    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Determines whether the Camera permission has been permanently denied.
     *
     * A permission is considered permanently denied when:
     * - The permission is NOT granted, AND
     * - The user has denied it before with "Don't ask again"
     *   (`shouldShowRequestPermissionRationale` returns false)
     *
     * This avoids incorrectly treating the first permission request
     * as a permanent denial.
     *
     * @param activity Activity required to check permission rationale.
     * @return `true` if the permission is permanently denied, `false` otherwise.
     */
    fun isPermissionPermanentlyDenied(activity: Activity): Boolean {
        val isGranted = hasCameraPermission(activity)

        return !isGranted &&
                !ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    CAMERA_PERMISSION
                )
    }

    /**
     * Opens the application's settings screen so the user can manually
     * grant the Camera permission.
     *
     * This is typically used when the permission has been permanently denied
     * and the system permission dialog will no longer appear.
     *
     * @param context Context used to start the settings activity.
     */
    fun openAppSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
