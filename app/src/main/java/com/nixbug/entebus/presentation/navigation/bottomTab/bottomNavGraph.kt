package com.nixbug.entebus.presentation.navigation.bottomTab

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nixbug.entebus.presentation.screens.historyScreen.HistoryScreen
import com.nixbug.entebus.presentation.screens.homeScreen.HomeScreen
import com.nixbug.entebus.presentation.screens.profileScreen.ProfileScreen

@Composable
fun BottomNavGraph(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(BottomNavItem.Home.route) { HomeScreen() }
        composable(BottomNavItem.History.route) { HistoryScreen() }
        composable(BottomNavItem.Profile.route) { ProfileScreen() }
    }
}