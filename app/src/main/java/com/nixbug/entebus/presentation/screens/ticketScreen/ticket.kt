package com.nixbug.entebus.presentation.screens.ticketScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nixbug.entebus.presentation.components.AppHeader

@Composable
fun TicketScreen(navController: NavHostController, viewModel: TicketViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .safeDrawingPadding()
            .fillMaxSize()
            .background(Color.White)
    ) {

        // ------------------------ HEADER ----------------------------
        AppHeader(
            title = "Ticket",
            onBackClick = { navController.popBackStack() },
            showTopBarActions = true,
            primaryActionText = "Ticket",
            primaryActionIcon = Icons.Default.Print,
            onPrimaryActionClick = { /* Print ticket */ },
            secondaryActionText = "QR",
            secondaryActionIcon = Icons.Default.QrCode,
            onSecondaryActionClick = { /* Show QR */ }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {

            // ------------------------ LANDMARK & PASSENGER COUNT CONTAINER ----------------------------
            Row(
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxSize()
            ) {

                // ------------------------ LANDMARK LIST ----------------------------
                Box(
                    modifier = Modifier
                        .weight(0.65f)
                        .fillMaxSize()
                ) {
                    LandmarkList(landmarks = state.landmarks)
                }

                // ------------------------ PASSENGER COUNT LIST----------------------------
                Box(
                    modifier = Modifier
                        .weight(0.45f)
                        .fillMaxSize()
                ) {
                    PassengerCountList(passengers = state.passengers)
                }
            }

            // ------------------------ BOTTOM SUMMARY ----------------------------
            Box(
                modifier = Modifier
                    .weight(0.25f)
                    .fillMaxSize()
            ) {
                BottomSummaryCard()
            }
        }
    }
}

@Composable
fun LandmarkList(landmarks: List<Landmark>) {
    Column {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
        ) {
            items(landmarks) { landmark ->
                Text(
                    text = landmark.name,
                    color = Color.Black
                )
            }
        }
    }
}


@Composable
fun PassengerCountList(passengers: List<Passenger>) {
    Column {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
        ) {
            items(passengers) { passenger ->
//                Text(
//                    text = passenger.name,
//                    modifier = Modifier.padding(vertical = 8.dp),
//                    color = Color.Black
//                )
                Box(modifier = Modifier.clickable(onClick = {})) {
                    Text(
                        text = passenger.name,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun BottomSummaryCard() {
    Text("Bottom summary card")
}