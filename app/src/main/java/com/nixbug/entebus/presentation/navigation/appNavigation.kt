package com.nixbug.entebus.presentation.navigation

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nixbug.entebus.presentation.components.DialogBox
import com.nixbug.entebus.presentation.screens.locationRequestScreen.LocationRequest
import com.nixbug.entebus.presentation.screens.authScreen.Auth
import com.nixbug.entebus.utils.LocationPermissionHelper

@SuppressLint("ContextCastToActivity")
@Composable
fun AppNavigation(viewModel: AppNavigationViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val isLoggedIn = false
    val context = LocalContext.current
    val locationState = viewModel.locationState.collectAsState()
    var hasPermission by remember {
        mutableStateOf(
            LocationPermissionHelper.isPermissionGranted(
                context
            )
        )
    }

    // Recheck location permission whenever the user returns to the app
    // (e.g., after manually changing permission settings)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPermission = LocationPermissionHelper.isPermissionGranted(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Show a dialog box if the user denies location permission
    if (!hasPermission && isLoggedIn) {
        DialogBox(
            title = "Permission Required",
            message = "This app needs location permission. Please enable it in settings.",
            confirmText = "Open Settings",
            onConfirmClick = {
                LocationPermissionHelper.openAppSettings(context)
            },
            dismissible = false,
            showExitButton = true,
            exitText = "Cancel",
            onExitClick = {}
        )
    }

    if (locationState.value.isLoading) {
        return  // Or show a splash screen
    }

    val locationData = locationState.value.data
    val hasLocation = locationData?.hasLocation == true

    val startDestination = when {
        !hasPermission -> "locationRequestScreen"
        !hasLocation -> "locationRequestScreen"
        else -> "authScreen"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    )
    {
        composable("authScreen") {
            Auth()
        }
        composable("locationRequestScreen") {
            LocationRequest(navController = navController)
        }
    }
}
