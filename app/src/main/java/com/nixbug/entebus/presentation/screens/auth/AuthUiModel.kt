package com.nixbug.entebus.presentation.screens.auth

import com.nixbug.entebus.BuildConfig

data class ClientDetailsModel(
    val androidVersion: String = android.os.Build.VERSION.RELEASE,
    val apiLevel: Int = android.os.Build.VERSION.SDK_INT,
    val apiVersion: String = BuildConfig.VERSION_NAME,
    val brand: String = android.os.Build.BRAND,
    val model: String = android.os.Build.MODEL,
    val manufacturer: String = android.os.Build.MANUFACTURER
)
