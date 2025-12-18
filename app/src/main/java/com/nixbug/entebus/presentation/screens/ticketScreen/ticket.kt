package com.nixbug.entebus.presentation.screens.ticketScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.nixbug.entebus.presentation.components.AppHeader

@Composable
fun TicketScreen(navController: NavHostController) {
    Box(modifier = Modifier.safeDrawingPadding()) {
        AppHeader(
            title = "Ticket",
            onBackClick = { navController.popBackStack() },
            showTopBarActions = true,
            primaryActionText = "Print",
            primaryActionIcon = Icons.Default.Print,
            onPrimaryActionClick = { /* Print ticket */ },
            secondaryActionText = "QR",
            secondaryActionIcon = Icons.Default.QrCode,
            onSecondaryActionClick = { /* Show QR */ }
        )

    }
}