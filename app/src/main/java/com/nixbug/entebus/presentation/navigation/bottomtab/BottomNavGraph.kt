package com.nixbug.entebus.presentation.navigation.bottomtab

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nixbug.entebus.presentation.screens.history.HistoryScreen
import com.nixbug.entebus.presentation.screens.home.HomeScreen
import com.nixbug.entebus.presentation.screens.profile.ProfileScreen

@Composable
fun BottomNavGraph(navController: NavHostController, rootNavController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(BottomNavItem.Home.route) { HomeScreen(rootNavController = rootNavController) }
        composable(BottomNavItem.History.route) { HistoryScreen() }
        composable(BottomNavItem.Profile.route) { ProfileScreen() }
    }
}
