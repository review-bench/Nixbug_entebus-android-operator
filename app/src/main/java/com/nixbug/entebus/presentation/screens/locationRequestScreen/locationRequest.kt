package com.nixbug.entebus.presentation.screens.locationRequestScreen

import android.Manifest
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nixbug.entebus.R
import com.nixbug.entebus.presentation.components.DialogBox
import com.nixbug.entebus.utils.locationPermissionHelper

@Composable
fun LocationRequest(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context as Activity
    var hasPermission by remember {
        mutableStateOf(
            locationPermissionHelper.isPermissionGranted(
                context
            )
        )
    }
    var showDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasPermission = granted
        when {
            granted -> {
                Toast.makeText(context, "Permission granted!", Toast.LENGTH_SHORT).show()
                navController.navigate("authScreen")
            }
            locationPermissionHelper.isPermanentlyDenied(activity) -> {
                showDialog=true
            }
            else -> {
                Toast.makeText(context, "Please enable the location access!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if(showDialog){
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

    // UI
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
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E1E),
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(
                modifier = Modifier
                    .height(15.dp)
            )

            Text(
                text = "We use your location to show nearby companies and track buses in real time. Your data stays private and secure.",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 15.sp,
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
                    tint = Color.White, // your blue color
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Allow Location Access",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
