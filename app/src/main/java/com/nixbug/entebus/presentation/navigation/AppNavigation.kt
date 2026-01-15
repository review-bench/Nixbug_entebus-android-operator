package com.nixbug.entebus.presentation.navigation

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nixbug.entebus.presentation.components.DialogBox
import com.nixbug.entebus.presentation.components.StyledSnackbar
import com.nixbug.entebus.presentation.screens.auth.AuthScreen
import com.nixbug.entebus.presentation.screens.locationrequest.LocationRequestScreen
import com.nixbug.entebus.presentation.screens.main.MainScreen
import com.nixbug.entebus.presentation.screens.qrscanner.QrScannerScreen
import com.nixbug.entebus.presentation.screens.ticket.TicketScreen
import com.nixbug.entebus.utils.LocationPermissionHelper
import com.nixbug.entebus.utils.snackbar.SnackbarEvent
import com.nixbug.entebus.utils.snackbar.SnackbarManager

@SuppressLint("ContextCastToActivity", "UnusedMaterial3ScaffoldPaddingParameter")
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
    val locationData = locationState.value.data
    val hasLocation = locationData?.hasLocation == true
    val snackbarHostState = remember { SnackbarHostState() }
    var currentEvent by remember { mutableStateOf<SnackbarEvent?>(null) }

    LaunchedEffect(Unit) {
        SnackbarManager.events.collect { event ->
            if (event == null) {
                snackbarHostState.currentSnackbarData?.dismiss()
            } else {
                currentEvent = event
                val result = snackbarHostState.showSnackbar(
                    message = event.message,
                    actionLabel = event.actionLabel,
                    duration = event.duration
                )
                if (result == SnackbarResult.ActionPerformed) {
                    event.onAction?.invoke()
                }
            }
        }
    }

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

    if (locationState.value.isLoading) {
        return
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

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState, modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) { snackbarData ->
                currentEvent?.let { event ->
                    StyledSnackbar(
                        event = event,
                        snackbarData = snackbarData
                    )
                }
            }
        }
    ) {
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
                TicketScreen(navController = navController, activity = activity)
            }
            composable(AppNavItem.QrScannerScreen.route) {
                QrScannerScreen(navController = navController)
            }
        }
    }
}
