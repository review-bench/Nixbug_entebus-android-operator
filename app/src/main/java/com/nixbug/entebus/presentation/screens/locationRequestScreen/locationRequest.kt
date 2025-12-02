package com.nixbug.entebus.presentation.screens.locationRequestScreen

import android.Manifest
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.nixbug.entebus.R
import com.nixbug.entebus.presentation.components.DialogBox
import com.nixbug.entebus.utils.LocationPermissionHelper

@Composable
fun LocationRequest(navController: NavHostController, activity: Activity) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            LocationPermissionHelper.isPermissionGranted(
                context
            )
        )
    }
    var showDialog by remember { mutableStateOf(false) }

    // Permission launcher to display the system's default location permission dialog
    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            hasPermission = granted
            when {
                granted -> {
                    Toast.makeText(context, "Permission granted!", Toast.LENGTH_SHORT).show()
                    navController.navigate("authScreen")
                }

                LocationPermissionHelper.isPermanentlyDenied(activity) -> {
                    showDialog = true
                }

                else -> {
                    Toast.makeText(
                        context,
                        "Please enable the location access!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }

    // Automatically navigate to the Auth screen when the user grants location permission
    // after returning from the app settings
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (LocationPermissionHelper.isPermissionGranted(context) &&
                    navController.currentDestination?.route != "authScreen"
                ) {
                    Toast.makeText(context, "Permission granted!", Toast.LENGTH_SHORT).show()
                    navController.navigate("authScreen")
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (showDialog) {
        DialogBox(
            title = "Permission Required",
            message = "Location access is required to continue. Please enable location in Settings.",
            confirmText = "Open Settings",
            onConfirmClick = {
                LocationPermissionHelper.openAppSettings(context)
                showDialog = false
            },
            dismissible = false,
            showExitButton = true,
            exitText = "Exit App",
            onExitClick = { activity.finishAffinity() }
        )
    }


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
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "We use your location to show nearby companies and track buses in real time. Your data stays private and secure.",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

        }

        Spacer(modifier = Modifier.height(15.dp))

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
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D6EFD))
        ) {

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

        Spacer(modifier = Modifier.height(8.dp))

    }
}
