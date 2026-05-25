package com.nixbug.entebus.presentation.screens.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nixbug.entebus.presentation.components.AppHeader
import com.nixbug.entebus.presentation.screens.ticket.components.BottomSummaryCard
import com.nixbug.entebus.presentation.screens.ticket.components.PassengerCountList
import com.nixbug.entebus.utils.SetStatusBarColor
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import com.nixbug.entebus.utils.titleCaseFirstChar
import kotlinx.coroutines.delay

@Composable
fun LegacyTicketScreen(
    navController: NavHostController,
    viewModel: TicketViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val destinationLandmarks = if (state.startingLandmarkIndex != null) {
        state.landmarks.drop(state.startingLandmarkIndex!! + 1)
    } else {
        state.landmarks
    }
    val focusManager = LocalFocusManager.current
    SetStatusBarColor(color = Color.White, darkIcons = true)
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
            showTopBarActions = true,
            primaryActionText = "Audit",
            primaryActionIcon = Icons.Default.Print,
            onPrimaryActionClick = { viewModel.loadPassengerStats() },
            isPrimaryActionEnabled = !state.isLoadingPassengerStats && !state.isPrinting
        )
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .weight(0.73f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.58f)
                        .fillMaxHeight()
                        .padding(horizontal = 5.dp)
                ) {
                    LandmarkNumberDropdownField(
                        selectedLandmark = state.startingLandmark,
                        landmarks = state.landmarks,
                        label = "From",
                        displayStartIndex = 1,
                        onLandmarkSelected = { landmark, index ->
                            viewModel.selectStartingLandmark(
                                landmark,
                                index
                            )
                        },
                        onFocus = { viewModel.clearStartingLandmark() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val canMoveStartPrevious = (state.startingLandmarkIndex ?: 0) > 0
                        ElevatedButton(
                            enabled = canMoveStartPrevious,
                            onClick = { viewModel.selectPreviousStartingLandmark() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardDoubleArrowLeft,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        val canMoveStartNext = state.startingLandmarkIndex != null &&
                                state.startingLandmarkIndex!! <
                                ((state.endingLandmarkIndex ?: state.landmarks.size) - 1)
                        ElevatedButton(
                            enabled = canMoveStartNext,
                            onClick = { viewModel.selectNextStartingLandmark() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardDoubleArrowRight,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LandmarkNumberDropdownField(
                        selectedLandmark = state.endingLandmark,
                        landmarks = destinationLandmarks,
                        label = "To",
                        displayStartIndex = (state.startingLandmarkIndex ?: -1) + 2,
                        onLandmarkSelected = { landmark, _ ->
                            val actualIndex = state.landmarks.indexOfFirst { it.id == landmark.id }
                            viewModel.selectEndingLandmark(landmark, actualIndex)
                        },
                        onFocus = { viewModel.clearEndingLandmark() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val canMoveEndPrevious = state.endingLandmarkIndex != null &&
                                state.endingLandmarkIndex!! >
                                ((state.startingLandmarkIndex ?: -1) + 1)
                        ElevatedButton(
                            enabled = canMoveEndPrevious,
                            onClick = { viewModel.selectPreviousEndingLandmark() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardDoubleArrowLeft,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        val canMoveEndNext = state.endingLandmarkIndex != null &&
                                state.endingLandmarkIndex!! <
                                state.landmarks.lastIndex
                        ElevatedButton(
                            enabled = canMoveEndNext,
                            onClick = { viewModel.selectNextEndingLandmark() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardDoubleArrowRight,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandmarkNumberDropdownField(
    selectedLandmark: Landmark?,
    landmarks: List<Landmark>,
    label: String,
    onLandmarkSelected: (Landmark, Int) -> Unit,
    onFocus: () -> Unit,
    displayStartIndex: Int = 1
) {
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    var rawInput by remember { mutableStateOf("") }
    var displayText by remember { mutableStateOf("") }

    val displayValue = selectedLandmark?.let { landmark ->
        val index = landmarks.indexOfFirst { it.id == landmark.id }
        if (index >= 0) {
            "${displayStartIndex + index} ${landmark.name.titleCaseFirstChar()}"
        } else {
            landmark.name.titleCaseFirstChar()
        }
    } ?: ""

    LaunchedEffect(selectedLandmark, isFocused) {
        if (!isFocused) {
            displayText = displayValue
        }
    }

    LaunchedEffect(rawInput) {
        if (rawInput.isBlank()) return@LaunchedEffect
        delay(800)
        val enteredIndex = rawInput.toIntOrNull() ?: return@LaunchedEffect
        val minValue = displayStartIndex
        val maxValue = displayStartIndex + landmarks.size - 1
        if (enteredIndex !in minValue..maxValue) {
            rawInput = ""
            SnackbarManager.show(
                message = "Please enter a number between $minValue and $maxValue",
                type = SnackbarType.ERROR
            )
            focusManager.clearFocus()
            return@LaunchedEffect
        }
        val landmarkIndex = enteredIndex - displayStartIndex
        if (landmarkIndex in landmarks.indices) {
            val landmark = landmarks[landmarkIndex]
            onLandmarkSelected(landmark, landmarkIndex)
            displayText =
                "${displayStartIndex + landmarkIndex} ${landmark.name.titleCaseFirstChar()}"
            focusManager.clearFocus()
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {},
        modifier = Modifier.background(
            Color.White,
            RoundedCornerShape(16.dp)
        )
    ) {

        OutlinedTextField(
            value = if (isFocused) displayText else displayValue,
            onValueChange = { value ->
                val digits = value.filter(Char::isDigit)
                rawInput = digits
                displayText = digits
                if (digits.isNotEmpty()) {
                    expanded = true
                }
            },
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .onFocusChanged {
                    if (it.isFocused) {
                        isFocused = true
                        rawInput = ""
                        displayText = ""
                        onFocus()
                        expanded = true
                    }
                    if (!it.isFocused) {
                        isFocused = false
                        expanded = false
                        rawInput = ""
                    }
                },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                Icon(
                    imageVector =
                        if (expanded)
                            Icons.Default.KeyboardArrowUp
                        else
                            Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF8FAFC),
                unfocusedContainerColor = Color(0xFFF8FAFC),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF8BC34A).copy(alpha = 0.12f))
        ) {
            landmarks.forEachIndexed { index, landmark ->

                DropdownMenuItem(
                    text = {
                        Text(
                            text = landmark.name.titleCaseFirstChar(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingIcon = {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "${displayStartIndex + index}",
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 6.dp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    onClick = {
                        onLandmarkSelected(landmark, index)
                        displayText = "${displayStartIndex + index} ${landmark.name}"
                        rawInput = ""
                        expanded = false
                        focusManager.clearFocus()
                    }
                )
                if (index < landmarks.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = Color(0xFFC8C8CE)
                    )
                }
            }
        }
    }
}
