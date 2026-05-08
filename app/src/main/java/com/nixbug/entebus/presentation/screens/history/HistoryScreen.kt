package com.nixbug.entebus.presentation.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nixbug.entebus.domain.model.DutyHistoryModel
import com.nixbug.entebus.utils.SetStatusBarColor
import com.nixbug.entebus.utils.utcToIstConverter

@Composable
fun HistoryScreen(viewModel: HistoryScreenViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var showSheet by remember { mutableStateOf(false) }
    SetStatusBarColor(
        color = Color.White,
        darkIcons = true
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusManager.clearFocus() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
                .padding(bottom = 70.dp)
        ) {
            HistoryHeader(
                onFilterClick = { showSheet = true },
                searchText = state.searchQuery,
                onSearchTextChange = viewModel::onSearchQueryChange,
            )
            DutyHistoryList(
                dutyHistoryData = state.history,
                calculateDuration = viewModel::calculateDuration,
                onLoadMore = viewModel::loadMoreHistory,
                isLoadingMore = state.isPaginationLoading,
                hasMore = state.hasMore
            )
        }
        DutyHistoryOverlay(
            showSheet = showSheet,
            onDismiss = { showSheet = false },
            convertMillisToDate = viewModel::convertMillisToDate,
            selectedStatus = state.selectedStatus,
            selectedDate = state.selectedDate,

            onStatusSelected = viewModel::onStatusSelected,
            onDateSelected = viewModel::onDateSelected,

            onClear = viewModel::clearFilters,

            onApply = {
                viewModel.applyFilters()
                showSheet = false
            }
        )
    }
}

@Composable
fun HistoryHeader(
    onFilterClick: () -> Unit,
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Duty History",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black
                )
                Text(
                    "Displaying All Duty History", style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = "Filter",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            label = { Text("Search by duty ID...") },
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Search items"
                },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Search
            )
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun DutyHistoryList(
    dutyHistoryData: List<DutyHistoryModel>,
    calculateDuration: (String?, String?) -> String,
    onLoadMore: (() -> Unit)? = null,
    isLoadingMore: Boolean = false,
    hasMore: Boolean = true
) {
    if (dutyHistoryData.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No history available!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
        contentPadding = WindowInsets.ime.asPaddingValues(),
    ) {
        item { Spacer(Modifier.height(10.dp)) }
        items(dutyHistoryData) { history ->
            DutyHistoryCard(
                history = history,
                calculateDuration = calculateDuration
            )
            Spacer(Modifier.height(15.dp))
        }

        item {
            Spacer(Modifier.height(10.dp))
            if (onLoadMore != null) {
                when {
                    isLoadingMore -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    hasMore -> {
                        ViewMoreText(
                            text = "Load More",
                            onClick = onLoadMore,
                            isExpanded = false
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun DutyHistoryCard(history: DutyHistoryModel, calculateDuration: (String?, String?) -> String) {

    val (statusText, bgColor, textColor) = when (history.status) {
        1 -> Triple(
            "STARTED",
            Color(0xFFDFF3E1),
            Color(0xFF2E7D32)
        )

        2 -> Triple(
            "ENDED",
            Color(0xFFFDEED6),
            Color(0xFFEF6C00)
        )

        3 -> Triple(
            "AUDITED",
            Color(0xFFFDEED6),
            Color(0xFFEF6C00)
        )


        else -> Triple(
            "UNKNOWN",
            Color(0xFFEEEEEE),
            Color(0xFF616161)
        )
    }
    val (startDate, startTime) = utcToIstConverter(history.startedOn)
        ?: ("Invalid date" to "Invalid time")
    val finishDateTime = utcToIstConverter(history.finishedOn)
    val durationText = calculateDuration(history.startedOn, history.finishedOn)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            /** Top Row **/
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    LabeledIdRow(
                        label = "Service ID",
                        value = history.serviceId.toString()
                    )

                    LabeledIdRow(
                        label = "Duty ID",
                        value = history.id.toString()
                    )
                }


                /** Status Chip **/
                Box(
                    modifier = Modifier
                        .background(
                            color = bgColor,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .width(110.dp)
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            /** Date & Time Info **/
            InfoRow(
                icon = Icons.Default.PlayArrow,
                contentDescription = "Started on",
                label = "Started on",
                value = "$startDate, $startTime"
            )

            InfoRow(
                icon = Icons.Outlined.Stop,
                contentDescription = "Finished on",
                label = if (history.status == 3) {
                    "Terminated on"
                } else {
                    "Finished on"
                },
                value = finishDateTime?.let { (date, time) -> "$date, $time" } ?: "Not finished yet"
            )

            InfoRow(
                icon = Icons.Outlined.Timer,
                contentDescription = "Duration",
                label = "Duration",
                value = durationText
            )

            /** Dashed Separator **/
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(20) {
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }

            /** Collection **/
            val hasFinished = finishDateTime != null
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (hasFinished) {
                    Icon(
                        imageVector = Icons.Outlined.Payments,
                        contentDescription = "Payments Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Collection : ",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                }

                Text(
                    text = if (hasFinished) {
                        "₹ ${history.collection}"
                    } else {
                        "End duty to view collection"
                    },
                    style = if (hasFinished) {
                        MaterialTheme.typography.titleMedium
                    } else {
                        MaterialTheme.typography.labelSmall
                    },
                    color = if (hasFinished) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    contentDescription: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = label,
            modifier = Modifier.width(100.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )

        Text(
            text = ":",
            modifier = Modifier.width(8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

@Composable
private fun LabeledIdRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    labelWidth: Dp = 80.dp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.width(labelWidth),
            style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "•",
            modifier = Modifier.width(12.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DutyHistoryOverlay(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    convertMillisToDate: (Long) -> String,
    selectedStatus: Int?,
    selectedDate: String?,

    onStatusSelected: (Int?) -> Unit,
    onDateSelected: (String?) -> Unit,

    onClear: () -> Unit,
    onApply: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showSheet) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                /** Header **/
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filter",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Close"
                            )
                        }

                    }
                    HorizontalDivider()
                }

                /** Status Section **/
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    StatusOption(
                        label = "Started",
                        isSelected = selectedStatus == 1,
                        onClick = { onStatusSelected(1) }
                    )

                    StatusOption(
                        label = "Ended",
                        isSelected = selectedStatus == 2,
                        onClick = { onStatusSelected(2) }
                    )

                    StatusOption(
                        label = "Audited",
                        isSelected = selectedStatus == 3,
                        onClick = { onStatusSelected(3) }
                    )
                }

                /** Date Section **/
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Date",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = "Calendar")
                            Text(
                                text = selectedDate ?: "Select Date",
                                modifier = Modifier.padding(6.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                /** Action Buttons **/
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onClear,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Clear All", style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onApply,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Apply", style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    /** Date Picker **/
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(convertMillisToDate(it))
                    }
                    showDatePicker = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun StatusOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Box(
            modifier = Modifier
                .size(25.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else Color.Transparent
                )
                .border(
                    width = 2.dp,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Check icon",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun ViewMoreText(
    text: String = "View More",
    onClick: () -> Unit,
    isExpanded: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector = if (isExpanded) Icons.Filled.KeyboardDoubleArrowUp else Icons.Filled.KeyboardDoubleArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(25.dp)
        )
    }
}

