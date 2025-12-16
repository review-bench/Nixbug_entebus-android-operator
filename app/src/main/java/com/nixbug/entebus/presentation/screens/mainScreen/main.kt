package com.nixbug.entebus.presentation.screens.mainScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.nixbug.entebus.presentation.navigation.bottomTab.BottomNavGraph
import com.nixbug.entebus.presentation.navigation.bottomTab.BottomNavigationBar

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
        ) {
            BottomNavGraph(navController)
        }
    }
}
