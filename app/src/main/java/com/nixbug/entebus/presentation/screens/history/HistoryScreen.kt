package com.nixbug.entebus.presentation.screens.history

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nixbug.entebus.utils.utcToIstConverter

@Composable
fun HistoryScreen(viewModel: HistoryScreenViewModel = hiltViewModel()) {

    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusManager.clearFocus() }

    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            HistoryHeader()
            Spacer(Modifier.height(8.dp))
            DutyHistoryList(dutyHistoryData = state.dutyHistoryData)
        }

    }
}

@Composable
fun HistoryHeader() {
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
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = "Dismiss",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Search...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}

@Composable
fun DutyHistoryList(dutyHistoryData: List<HistoryUiModel>) {
    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
        contentPadding = WindowInsets.ime.asPaddingValues(),
    ) {
        if (dutyHistoryData.isNotEmpty()) {
            item { Spacer(Modifier.height(10.dp)) }
            items(dutyHistoryData) { history ->
                DutyHistoryCard(history)
                Spacer(Modifier.height(12.dp))
            }
        } else {
            item { Text("No history available!") }
        }
    }
}

@Composable
fun DutyHistoryCard(history: HistoryUiModel) {

    val (statusText, bgColor, textColor) = when (history.status) {
        2 -> Triple(
            "STARTED",
            Color(0xFFDFF3E1),
            Color(0xFF2E7D32)
        )

        3 -> Triple(
            "TERMINATED",
            Color(0xFFFDECEA),
            Color(0xFFC62828)
        )

        4 -> Triple(
            "ENDED",
            Color(0xFFFDEED6),
            Color(0xFFEF6C00)
        )

        else -> Triple(
            "UNKNOWN",
            Color(0xFFEEEEEE),
            Color(0xFF616161)
        )
    }
    val (startDate, startTime) = utcToIstConverter(history.startedOn) ?: return
    val finishDateTime = utcToIstConverter(history.finishedOn)

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
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            /** Top Row **/
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Service ID • ${history.serviceId}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Duty ID • ${history.id}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                label = "Started on",
                value = "$startDate, $startTime"
            )

            InfoRow(
                icon = Icons.Outlined.Stop,
                label = "Finished on",
                value = finishDateTime?.let { (date, time) ->
                    "$date, $time"
                } ?: "Not finished yet"
            )

            InfoRow(
                icon = Icons.Outlined.Timer,
                label = "Duration",
                value = if (finishDateTime != null) {
                    "1 hrs"
                } else {
                    "Not finished yet"
                }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Payments,
                    contentDescription = "Payments Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Collection : ", style = MaterialTheme.typography.titleMedium)
                Text(
                    text =if (finishDateTime != null) {
                        "₹ 100"
                    } else {
                        "₹ 0"
                    } ,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
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
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = label,
            modifier = Modifier.width(90.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )

        Text(
            text = ":",
            modifier = Modifier.width(8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

