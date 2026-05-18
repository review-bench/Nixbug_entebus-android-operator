package com.nixbug.entebus.presentation.screens.locationrequest

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.nixbug.entebus.R
import com.nixbug.entebus.presentation.components.DialogBox
import com.nixbug.entebus.presentation.navigation.AppNavItem
import com.nixbug.entebus.utils.LocationPermissionHelper
import com.nixbug.entebus.utils.SetStatusBarColor


@Composable
fun LocationRequestScreen(
    navController: NavHostController,
    activity: Activity,
) {

    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    val navigateToAuth = {
        navController.navigate(AppNavItem.Auth.route) {
            popUpTo(AppNavItem.LocationRequest.route) { inclusive = true }
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (LocationPermissionHelper.isPermissionGranted(context)) {
                    navigateToAuth()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            when {
                granted -> navigateToAuth()

                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) -> {
                    showRationale = true
                }

                else -> {
                    showSettingsDialog = true
                }
            }
        }

    if (showSettingsDialog) {
        DialogBox(
            title = "Location Access Denied",
            message = "To enable nearby company suggestions, please allow location access from Settings. You may continue without location access, but some features will be limited.",
            confirmText = "Open Settings",
            onConfirmClick = {
                showSettingsDialog = false
                LocationPermissionHelper.openAppSettings(context)
            },
            dismissible = true,
            onDismiss = { showSettingsDialog = false },
            showExitButton = true,
            exitText = "Continue Without Location",
            onExitClick = {
                showSettingsDialog = false
                navigateToAuth()
            },
            titleColor = MaterialTheme.colorScheme.primary,
            confirmBtnColor = MaterialTheme.colorScheme.primary,
            exitBtnBorderColor = MaterialTheme.colorScheme.primary
        )
    }

    if (showRationale) {
        DialogBox(
            title = "Location Importance",
            message = "We use your location to show nearby transport operators and companies for a faster setup. You may continue without location access, but some features will be limited.",
            confirmText = "Allow Location Access",
            onConfirmClick = {
                showRationale = false
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            dismissible = true,
            onDismiss = { showRationale = false },
            showExitButton = true,
            exitText = "Continue Without Location",
            onExitClick = {
                showRationale = false
                navigateToAuth()
            },
            titleColor = MaterialTheme.colorScheme.primary,
            confirmBtnColor = MaterialTheme.colorScheme.primary,
            exitBtnBorderColor = MaterialTheme.colorScheme.primary
        )
    }

    SetStatusBarColor(
        color = Color.White,
        darkIcons = true
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            Image(
                painter = painterResource(id = R.drawable.location_screen_img),
                contentDescription = "Location Permission",
                modifier = Modifier
                    .size(300.dp)
                    .padding(8.dp)
            )

            Text(
                text = "Enable Location Access",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF1E1E1E),
                modifier = Modifier.padding(top = 10.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "We use your location to show nearby transport operators and companies for a faster setup. Your location data is used only for this purpose and is kept secure.You can also continue without location access",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

        }

        Spacer(modifier = Modifier.height(5.dp))

        Button(
            onClick = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        )
        {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Location Icon",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Allow Location Access",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { navigateToAuth() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            )
        )
        {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Skip Arrow Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Skip For Now",
                    color = Color.Gray,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
        Spacer(modifier = Modifier.height(25.dp))
    }
}
