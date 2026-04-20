package com.nixbug.entebus.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import android.content.Context
import android.content.ContextWrapper

@Composable
fun SetStatusBarColor(
    color: androidx.compose.ui.graphics.Color,
    darkIcons: Boolean
) {
    val view = LocalView.current
    val context = view.context

    LaunchedEffect(color, darkIcons) {
        if (view.isInEditMode) return@LaunchedEffect
        val activity = context.findActivity() ?: return@LaunchedEffect
        val window = activity.window

        window.statusBarColor = color.toArgb()
        WindowCompat.getInsetsController(window, view)
            .isAppearanceLightStatusBars = darkIcons
    }
}

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}
