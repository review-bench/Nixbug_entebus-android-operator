package com.nixbug.entebus.presentation.screens.ticketScreen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nixbug.entebus.presentation.components.AppHeader

@Composable
fun TicketScreen(navController: NavHostController, viewModel: TicketViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is TicketUiEvent.ShowToast -> {
                    Toast
                        .makeText(context, event.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ------------------------ HEADER ----------------------------
        AppHeader(
            title = "Ticket",
            onBackClick = { navController.popBackStack() },
            showTopBarActions = true,
            primaryActionText = "QR",
            primaryActionIcon = Icons.Default.QrCode,
            onPrimaryActionClick = { /* Print ticket */ })

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .weight(0.75f)
                    .fillMaxWidth()
            ) {
                // ------------------------ LANDMARK LIST ----------------------------
                Box(
                    modifier = Modifier
                        .weight(0.65f)
                        .fillMaxHeight()
                        .padding(horizontal = 5.dp)
                ) {
                    LandmarkList(
                        landmarks = state.landmarks,
                        handleLandmarkSelection = { landmark, index ->
                            viewModel.handleLandmarkSelection(landmark, index)
                        },
                        startingLandmark = state.startingLandmark,
                        endingLandmark = state.endingLandmark
                    )
                }
                // ------------------------ PASSENGER COUNT LIST----------------------------
                Box(
                    modifier = Modifier
                        .weight(0.45f)
                        .fillMaxHeight()
                ) {
                    PassengerCountList(
                        passengers = state.passengers,
                        onCountChange = { passenger, newCount ->
                            viewModel.updatePassengerCount(passenger.id, newCount)
                        }
                    )
                }
            }
            // ------------------------ BOTTOM SUMMARY ----------------------------
            Box(
                modifier = Modifier
                    .weight(0.25f)
                    .padding(10.dp)
                    .background(
                        color = (Color(0xFFF2F5F2)), shape = RoundedCornerShape(
                            topStart = 22.dp, topEnd = 22.dp, bottomStart = 22.dp, bottomEnd = 22.dp
                        )
                    )
                    .fillMaxSize()
            ) {
                BottomSummaryCard(
                    startingLandmark = state.startingLandmark,
                    endingLandmark = state.endingLandmark,
                    totalDistanceKm = state.totalDistanceKm
                )
            }
        }
    }
}

@Composable
fun LandmarkList(
    landmarks: List<Landmark>,
    handleLandmarkSelection: (Landmark, Int) -> Unit,
    startingLandmark: Landmark?,
    endingLandmark: Landmark?
) {
    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
    ) {
        itemsIndexed(landmarks) { index, landmark ->
            val isFirst = index == 0
            val isLast = index == landmarks.lastIndex
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (startingLandmark?.id == landmark.id) {
                            Color(0x5B9BDA54)
                        } else if (endingLandmark?.id == landmark.id) {
                            Color(0x5B9BDA54)
                        } else {
                            Color.Transparent
                        }
                    )
                    .clickable { handleLandmarkSelection(landmark, index) }
            )
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LandmarksTimelineIcons(isFirst, isLast)
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = landmark.name,
                            color = Color.Black,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (startingLandmark?.id == landmark.id) {
                            Text(text = "From", color = Color.Gray, fontSize = 12.sp)
                        }
                        if (endingLandmark?.id == landmark.id) {
                            Text(text = "To", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
    }
}

@Composable
fun LandmarksTimelineIcons(isFirst: Boolean, isLast: Boolean) {
    if (!isFirst) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(15.dp)
                .background(Color.Gray)
        )
    }

    if (isFirst) {
        Column {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(15.dp)
                    .background(Color.Transparent)
            )
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Start",
                tint = Color(0xFF8BC34A),
                modifier = Modifier.size(20.dp)
            )
        }

    } else if (isLast) {
        Column {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "End",
                tint = Color(0xFF8BC34A),
                modifier = Modifier.size(20.dp)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(15.dp)
                    .background(Color.Transparent)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    color = Color(0x5B9BDA54), shape = CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = Color(0xFF8BC34A), shape = CircleShape
                    )
            )
        }
    }
    if (!isLast) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(15.dp)
                .background(Color.Gray)
        )
    }
}

@Composable
fun PassengerCountList(passengers: List<Passenger>, onCountChange: (Passenger, Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Passenger items
            items(passengers) { passenger ->
                PassengerCountItem(
                    passenger = passenger,
                    onCountChange = onCountChange
                )
            }
        }
    }
}

@Composable
fun PassengerCountItem(passenger: Passenger, onCountChange: (Passenger, Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White, shape = RoundedCornerShape(12.dp)
            )
            .padding(5.dp, 12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

            // Passenger name
            Text(
                text = passenger.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            // Counter row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                CounterButton(text = "−", onClick = { /* decrement */ })

                BasicTextField(
                    value = passenger.count.toString(),
                    onValueChange = { input ->
                        if (input.isDigitsOnly()) {
                            onCountChange(
                                passenger,
                                input.toIntOrNull() ?: 0
                            )
                        }
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    ),
                    modifier = Modifier
                        .width(48.dp)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .background(Color(0xFFF9F9F9))
                        .padding(vertical = 6.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )


//                Text(
//                    text = "0",
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    modifier = Modifier.padding(horizontal = 16.dp)
//                )

                CounterButton(text = "+", onClick = { /* increment */ })
            }
        }
    }
}

@Composable
fun CounterButton(
    text: String, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(
                color = Color(0xFF8BC34A), shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }, contentAlignment = Alignment.Center
    ) {
        Text(
            text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun BottomSummaryCard(
    startingLandmark: Landmark?,
    endingLandmark: Landmark?,
    totalDistanceKm: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            if (startingLandmark?.name != null) {
                Row {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Back",
                        tint = Color(0xFF8BC34A)
                    )
                    Text(
                        " ${startingLandmark.name} ->  ${endingLandmark?.name ?: ""}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(10.dp))

                HorizontalDivider(thickness = 1.dp, color = Color.Black)
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Total Distance", fontSize = 12.sp, fontWeight = FontWeight.Medium
                    )
                    Text(
                        String.format("%.2f km", totalDistanceKm),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        "Total Fare", fontSize = 12.sp, fontWeight = FontWeight.Medium
                    )
                    Text(
                        "0 RS", fontSize = 18.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
            Button(
                onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(13.dp)
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                    Text(
                        " Print Ticket", fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
        }

    }
}
