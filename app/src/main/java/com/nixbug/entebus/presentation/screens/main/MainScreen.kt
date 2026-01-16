package com.nixbug.entebus.presentation.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nixbug.entebus.presentation.navigation.bottomtab.BottomNavGraph
import com.nixbug.entebus.presentation.navigation.bottomtab.BottomNavigationBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(rootNavController: NavHostController) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) {
        Box {
            BottomNavGraph(navController, rootNavController)
        }
    }
}
