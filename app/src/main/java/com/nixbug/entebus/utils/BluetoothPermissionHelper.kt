package com.nixbug.entebus.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object BluetoothPermissionHelper {

    /**
     * Define the required permissions based on Android Version.
     * API 31+ (Android 12) needs SCAN and CONNECT.
     * API 26-30 needs FINE_LOCATION to "see" Bluetooth devices.
     */
    val REQUIRED_PERMISSIONS: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    /**
     * Checks if ALL required Bluetooth permissions are granted.
     */
    fun hasBluetoothPermissions(context: Context): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Checks if any of the permissions show a "Rationale" (User denied once).
     */
    fun shouldShowRationale(activity: Activity): Boolean {
        return REQUIRED_PERMISSIONS.any {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }
    }

    /**
     * Checks if permissions are permanently denied.
     * (Not granted AND should NOT show rationale)
     */
    fun isPermanentlyDenied(activity: Activity): Boolean {
        return REQUIRED_PERMISSIONS.any { permission ->
            val isGranted = ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED
            !isGranted && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }
}
