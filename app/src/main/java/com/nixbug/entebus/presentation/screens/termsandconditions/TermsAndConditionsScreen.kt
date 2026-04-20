package com.nixbug.entebus.presentation.screens.termsandconditions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.nixbug.entebus.presentation.components.AppHeader
import com.nixbug.entebus.utils.SetStatusBarColor

@Composable
fun TermsAndConditionsScreen(
    navController: NavHostController,
) {
    SetStatusBarColor(
        color = Color.White,
        darkIcons = true
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AppHeader(
            title = "Terms & Conditions",
            onBackClick = { navController.popBackStack() }
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Terms and Conditions Screen")
        }
    }
}
