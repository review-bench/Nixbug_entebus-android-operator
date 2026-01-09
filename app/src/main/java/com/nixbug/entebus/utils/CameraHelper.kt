package com.nixbug.entebus.utils

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager

/**
 * Checks whether the device has a usable camera.
 *
 * @return true if the device reports camera hardware AND at least one camera
 *         ID is accessible. Returns false if the camera is unavailable or
 *         cannot be accessed due to system or permission restrictions.
 */
fun isCameraAvailable(context: Context): Boolean {
    val packageManager = context.packageManager

    if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
        return false
    }

    return try {
        val cameraManager =
            context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraManager.cameraIdList.isNotEmpty()
    } catch (e: Exception) {
        false
    }
}
