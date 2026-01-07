package com.nixbug.entebus.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.net.Uri
import android.provider.Settings

object CameraPermissionHelper {

    const val CAMERA_PERMISSION = android.Manifest.permission.CAMERA

    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isPermissionPermanentlyDenied(
        activity: Activity
    ): Boolean {
        return !ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            CAMERA_PERMISSION
        )
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        context.startActivity(intent)
    }
}
