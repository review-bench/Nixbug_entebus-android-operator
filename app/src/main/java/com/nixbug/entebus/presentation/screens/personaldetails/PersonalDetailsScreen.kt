package com.nixbug.entebus.presentation.screens.personaldetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.nixbug.entebus.presentation.components.AppHeader

@Composable
fun PersonalDetailsScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AppHeader(
            title = "Personal Details",
            onBackClick = { navController.popBackStack() }
        )
    }
}
