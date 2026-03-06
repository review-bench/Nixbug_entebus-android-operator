package com.nixbug.entebus.presentation.navigation.bottomtab

import android.app.Activity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nixbug.entebus.presentation.screens.history.HistoryScreen
import com.nixbug.entebus.presentation.screens.home.HomeScreen
import com.nixbug.entebus.presentation.screens.profile.ProfileScreen
import com.nixbug.entebus.presentation.screens.summary.SummaryScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController,
    activity: Activity
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        enterTransition = { fadeIn(animationSpec = tween(0)) },
        exitTransition = { fadeOut(animationSpec = tween(0)) },
        popEnterTransition = { fadeIn(animationSpec = tween(0)) },
        popExitTransition = { fadeOut(animationSpec = tween(0)) }
    ) {
        composable(BottomNavItem.Home.route) { HomeScreen(rootNavController = rootNavController,activity = activity) }
        composable(BottomNavItem.History.route) { HistoryScreen() }
        composable(BottomNavItem.Summary.route) { SummaryScreen(activity = activity) }
        composable(BottomNavItem.Profile.route) { ProfileScreen(rootNavController = rootNavController) }
    }
}
