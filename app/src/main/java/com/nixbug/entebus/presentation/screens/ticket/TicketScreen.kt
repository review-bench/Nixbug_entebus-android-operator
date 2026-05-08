package com.nixbug.entebus.presentation.screens.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
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
import com.nixbug.entebus.presentation.components.loadingskeleton.TicketScreenSkeleton
import com.nixbug.entebus.presentation.components.loadingskeleton.rememberShimmerBrush
import com.nixbug.entebus.ui.theme.DarkOrange
import com.nixbug.entebus.ui.theme.DisabledButtonColor
import com.nixbug.entebus.utils.SetStatusBarColor
import com.nixbug.entebus.utils.titleCaseFirstChar
import com.nixbug.entebus.utils.toCurrency
import java.util.Locale

@Composable
fun TicketScreen(
    navController: NavHostController,
    viewModel: TicketViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current


    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                TicketUiEvent.EmitNavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    SetStatusBarColor(
        color = Color.White,
        darkIcons = true
    )

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        // HEADER
        AppHeader(
            title = "Ticket",
            onBackClick = { navController.popBackStack() },
        )
        if (state.isLoading) {
            val shimmerBrush = rememberShimmerBrush()
            TicketScreenSkeleton(shimmerBrush = shimmerBrush)
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            )
            {

                Row(
                    modifier = Modifier
                        .weight(0.73f)
                        .fillMaxWidth()
                )
                {

                    // LANDMARK LIST
                    Box(
                        modifier = Modifier
                            .weight(0.58f)
                            .fillMaxHeight()
                            .padding(horizontal = 5.dp)
                    ) {
                        LandmarkList(
                            landmarks = state.landmarks,
                            onToggleLandmarkSelection = { landmark, index ->
                                viewModel.toggleLandmarkSelection(
                                    landmark,
                                    index
                                )
                            },
                            startingLandmark = state.startingLandmark,
                            endingLandmark = state.endingLandmark,
                            startingLandmarkIndex = state.startingLandmarkIndex,
                            endingLandmarkIndex = state.endingLandmarkIndex
                        )
                    }

                    // PASSENGER COUNT LIST
                    Box(
                        modifier = Modifier
                            .weight(0.42f)
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
                // BOTTOM SUMMARY
                Surface(
                    modifier = Modifier
                        .weight(0.27f)
                        .padding(10.dp, 8.dp)
                        .fillMaxSize(),
                    shape = RoundedCornerShape(22.dp),
                    tonalElevation = 6.dp,
                    shadowElevation = 6.dp,
                    color = Color(0xFFEEF6EE)
                ) {
                    BottomSummaryCard(
                        startingLandmark = state.startingLandmark,
                        endingLandmark = state.endingLandmark,
                        totalDistanceKm = state.totalDistanceKm,
                        passengers = state.passengers,
                        isPrinting = state.isPrinting,
                        onPrintClick = { viewModel.printTicket() },
                        totalFare = state.totalFare
                    )
                }
            }
        }
    }
}

@Composable
fun LandmarkList(
    landmarks: List<Landmark>,
    onToggleLandmarkSelection: (Landmark, Int) -> Unit,
    startingLandmark: Landmark?,
    endingLandmark: Landmark?,
    startingLandmarkIndex: Int?,
    endingLandmarkIndex: Int?
) {
    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
    ) {
        itemsIndexed(landmarks) { index, landmark ->
            val isFirst = index == 0
            val isLast = index == landmarks.lastIndex
            /* When the selected start and end landmarks are consecutive items,
               we add a tiny bottom spacing to visually separate their highlight backgrounds.
               Without this, the two highlighted rows visually merge
               into a single block, which looks like one selection.
            */
            val isAdjacent = startingLandmarkIndex != null &&
                    endingLandmarkIndex != null &&
                    index == startingLandmarkIndex &&
                    endingLandmarkIndex - startingLandmarkIndex == 1

            val isStart = startingLandmarkIndex != null && index == startingLandmarkIndex
            val isEnd = endingLandmarkIndex != null && index == endingLandmarkIndex

            val inBetween = startingLandmarkIndex != null &&
                    endingLandmarkIndex != null &&
                    index > minOf(startingLandmarkIndex, endingLandmarkIndex) &&
                    index < maxOf(startingLandmarkIndex, endingLandmarkIndex)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = if (isAdjacent) {
                            1.dp
                        } else {
                            0.dp
                        }
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (startingLandmark?.id == landmark.id) {
                            Color(0xB59BDA54)
                        } else if (endingLandmark?.id == landmark.id) {
                            DisabledButtonColor
                        } else {
                            Color.Transparent
                        }
                    )
                    .clickable { onToggleLandmarkSelection(landmark, index) }
            )
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LandmarksTimelineIcons(
                            index = index + 1,
                            isFirst,
                            isLast,
                            isStart,
                            isEnd,
                            inBetween
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        if (startingLandmark?.id == landmark.id) {
                            Text(
                                text = "From",
                                color = DarkOrange,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                        if (endingLandmark?.id == landmark.id) {
                            Text(
                                text = "Till",
                                color = DarkOrange,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                        Text(
                            text = landmark.name.titleCaseFirstChar(),
                            color = Color.Black,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
    }
}

@Composable
fun LandmarksTimelineIcons(
    index: Int,
    isFirst: Boolean,
    isLast: Boolean,
    isStart: Boolean,
    isEnd: Boolean,
    inBetween: Boolean,
) {

    val circleColor = when {
        isStart || isEnd || inBetween -> Color(0x6B974B00)
        else -> MaterialTheme.colorScheme.primary
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Top line
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(15.dp)
                .background(
                    if (isFirst) Color.Transparent else Color.Gray
                )
        )

        // Middle icon
        if (inBetween) {
            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowDown,
                contentDescription = null,
                tint = DarkOrange,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = circleColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = index.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 10.sp
                )
            }
        }

        // Bottom line
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(15.dp)
                .background(
                    if (isLast) Color.Transparent else Color.Gray
                )
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(8.dp, 10.dp)
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
    val textValue = if (passenger.passengerCount == 0) "" else passenger.passengerCount.toString()
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            )
            .background(
                color = Color.White, shape = RoundedCornerShape(12.dp)
            )
            .padding(5.dp, 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            // Passenger name
            Text(
                text = passenger.name,
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
            )

            // Counter row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                CounterButton(text = "−", onClick = {
                    if (passenger.passengerCount > 0) {
                        onCountChange(passenger, passenger.passengerCount - 1)
                    }
                })
                BasicTextField(
                    value = textValue,
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
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            if (!isFocused && textValue.isEmpty()) {
                                Text(
                                    text = "0",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier
                        .width(46.dp)
                        .height(38.dp)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .background(Color(0xFFF9F9F9))
                        .onFocusChanged {
                            isFocused = it.isFocused
                        },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                )
                CounterButton(
                    text = "+",
                    onClick = {
                        onCountChange(passenger, passenger.passengerCount + 1)
                    })
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
                color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }, contentAlignment = Alignment.Center
    ) {

        Text(
            text = text, color = Color.White, style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun BottomSummaryCard(
    startingLandmark: Landmark?,
    endingLandmark: Landmark?,
    totalDistanceKm: Float,
    passengers: List<Passenger>,
    onPrintClick: () -> Unit,
    isPrinting: Boolean,
    totalFare: Double
) {
    val isTicketValid = startingLandmark != null &&
            endingLandmark != null &&
            passengers.any { it.passengerCount > 0 }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp, 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            if (startingLandmark != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        " ${startingLandmark.name} -> ${endingLandmark?.name ?: ""}",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdsClick,
                        contentDescription = "AdsClick",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        " Select a starting point and destination",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
            HorizontalDivider(thickness = 1.dp, color = Color.Black)
            Spacer(Modifier.height(5.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Total Distance",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    Text(
                        String.format(Locale.getDefault(), "%.2f km", totalDistanceKm),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp
                    )
                }
                Column {
                    Text(
                        "Total Fare",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    Text(
                        text = "${totalFare.toCurrency()} \u20B9",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
            Button(
                onClick = { onPrintClick() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(13.dp),
                contentPadding = PaddingValues(5.dp),
                colors = ButtonDefaults.buttonColors(disabledContainerColor = DisabledButtonColor),
                enabled = isTicketValid && !isPrinting,
            ) {
                if (isPrinting) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "Print Ticket",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            " Print Ticket", style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}
