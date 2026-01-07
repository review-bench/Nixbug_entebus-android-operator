package com.nixbug.entebus.presentation.navigation

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import com.nixbug.entebus.presentation.screens.auth.AuthScreen
import com.nixbug.entebus.presentation.screens.locationrequest.LocationRequestScreen
import com.nixbug.entebus.presentation.screens.main.MainScreen
import com.nixbug.entebus.presentation.screens.qrscanner.QrScannerScreen
import com.nixbug.entebus.presentation.screens.ticket.TicketScreen
import com.nixbug.entebus.utils.LocationPermissionHelper

@SuppressLint("ContextCastToActivity")
@Composable
fun AppNavigation(
    activity: Activity,
    viewModel: AppNavigationViewModel = hiltViewModel(),
) {
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
        return
    }

    val locationData = locationState.value.data
    val hasLocation = locationData?.hasLocation == true

    LaunchedEffect(hasPermission, hasLocation) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentRoute != AppNavItem.LocationRequest.route) return@LaunchedEffect
        if (hasPermission && hasLocation) {
            navController.navigate(AppNavItem.Auth.route) {
                popUpTo(AppNavItem.LocationRequest.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        startDestination = if (hasLocation) AppNavItem.Auth.route else AppNavItem.LocationRequest.route
    )
    {
        composable(AppNavItem.Auth.route) {
            AuthScreen(navController = navController)
        }
        composable(AppNavItem.LocationRequest.route) {
            LocationRequestScreen(navController = navController, activity = activity)
        }
        composable(AppNavItem.MainScreen.route) {
            MainScreen(rootNavController = navController)
        }
        composable(AppNavItem.TicketScreen.route) {
            TicketScreen(navController = navController)
        }
        composable(AppNavItem.QrScannerScreen.route) {
            QrScannerScreen(navController = navController)
        }
    }
}
