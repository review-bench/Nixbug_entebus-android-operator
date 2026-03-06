package com.nixbug.entebus.presentation.screens.main

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nixbug.entebus.presentation.navigation.bottomtab.BottomNavGraph
import com.nixbug.entebus.presentation.navigation.bottomtab.BottomNavigationBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
// Padding is handled per-screen instead of using Scaffold padding.
// This allows the Profile tab to use a primary-colored status bar
// while other screens apply their own system bar insets.
@Composable
fun MainScreen(rootNavController: NavHostController, activity: Activity) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) {
        Box {
            BottomNavGraph(navController, rootNavController,activity = activity)
        }
    }
}
