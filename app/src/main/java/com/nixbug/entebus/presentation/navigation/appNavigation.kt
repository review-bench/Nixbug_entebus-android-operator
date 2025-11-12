package com.nixbug.entebus.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nixbug.entebus.presentation.components.DialogBox
import com.nixbug.entebus.presentation.screens.authScreen.Auth
import com.nixbug.entebus.presentation.screens.locationRequestScreen.LocationRequest
import com.nixbug.entebus.utils.locationPermissionHelper

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val isLoggedIn=false
    val context= LocalContext.current
    var hasPermission by remember { mutableStateOf(locationPermissionHelper.isPermissionGranted(context)) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPermission = locationPermissionHelper.isPermissionGranted(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (!hasPermission && isLoggedIn) {
        DialogBox(
            title = "Permission Required",
            message = "This app needs location permission. Please enable it in settings.",
            confirmText = "Open Settings",
            onConfirmClick = {
                locationPermissionHelper.openAppSettings(context)
            },
            dismissible = false
        )
    }

    NavHost(
        navController = navController,
        startDestination = if (hasPermission) "authScreen" else "locationRequestScreen"
    )
    {
        composable("authScreen") {
            Auth()
        }
        composable("locationRequestScreen") {
            LocationRequest(navController=navController)
        }
    }
}
