package com.nixbug.entebus.presentation.screens.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import com.nixbug.entebus.utils.utcToIstConverter
import kotlinx.coroutines.delay

@Composable
fun SummaryScreen(
    viewModel: SummaryScreenViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsState()
    var showPrintDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(bottom = 135.dp)
        ) {
            SummaryHeader()

            SummaryList(
                summaryData = state.summary,
                selectedIds = state.selectedServiceIds,
                onToggle = viewModel::toggleSelection,
                modifier = Modifier.weight(1f)
            )
        }

        PrintButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .padding(horizontal = 30.dp),
            onClick = {
                if (state.selectedServiceIds.isNotEmpty()) {
                    showPrintDialog = true
                } else {
                    SnackbarManager.show(
                        message = "Please select at least one service",
                        type = SnackbarType.WARNING
                    )
                }
            }
        )

    }

    if (showPrintDialog) {
        PrintSummaryDialog(
            summaryData = state.summary,
            selectedIds = state.selectedServiceIds,
            onDismiss = { showPrintDialog = false }
        )
    }

}

@Composable
fun SummaryHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(bottom = 12.dp)
    ) {
        Text(
            text = "Collection Overview",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Showing collections from today and yesterday",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun SummaryList(
    summaryData: List<SummaryUiModel>,
    selectedIds: Set<Int>,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedByService = remember(summaryData) { summaryData.groupBy { it.serviceId } }

    LazyColumn(
        modifier = modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        items(groupedByService.values.toList()) { serviceGroup ->
            val serviceId = serviceGroup.first().serviceId
            SummaryCard(
                serviceGroup = serviceGroup,
                isSelected = selectedIds.contains(serviceId),
                onClick = { onToggle(serviceId) }
            )
        }
    }
}

@Composable
fun PrintButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "Preview Collection",
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SummaryCard(
    serviceGroup: List<SummaryUiModel>,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    val firstItem = remember(serviceGroup) { serviceGroup.first() }
    val totalCollection = remember(serviceGroup) { serviceGroup.sumOf { it.collection } }
    val (startDate, startTime) = remember(firstItem.startedOn) {
        utcToIstConverter(firstItem.startedOn) ?: ("Invalid date" to "Invalid time")
    }
    val (finishDate, finishTime) = remember(firstItem.finishedOn) {
        utcToIstConverter(firstItem.finishedOn) ?: ("Invalid date" to "Invalid time")
    }
    val containerColor = if (isSelected) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        SummaryCardContent(
            firstItem = firstItem,
            serviceGroup = serviceGroup,
            startDate = startDate,
            startTime = startTime,
            finishDate = finishDate,
            finishTime = finishTime,
            totalCollection = totalCollection,
            isSelected = isSelected,
            onToggle = onClick
        )
    }

}

@Composable
fun SummaryCardContent(
    firstItem: SummaryUiModel,
    serviceGroup: List<SummaryUiModel>,
    startDate: String,
    startTime: String,
    finishDate: String,
    finishTime: String,
    totalCollection: Int,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 12.dp)
            .fillMaxWidth(),
    ) {
        SummaryCardHeader(
            serviceName = firstItem.serviceName,
            regNo = firstItem.regNo,
            isSelected = isSelected,
            onToggle = onToggle,
            startDate = startDate,
            startTime = startTime,
            finishDate = finishDate,
            finishTime = finishTime
        )
        Spacer(modifier = Modifier.height(8.dp))
        OperatorList(serviceGroup)
        Spacer(modifier = Modifier.height(8.dp))
        DividerLine()
        Spacer(modifier = Modifier.height(8.dp))
        TotalRow(totalCollection)
    }
}

@Composable
fun SummaryCardHeader(
    serviceName: String,
    regNo: String,
    isSelected: Boolean,
    onToggle: () -> Unit,
    startDate: String,
    startTime: String,
    finishDate: String,
    finishTime: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            ExpandableEllipsizedText(
                text = serviceName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = regNo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            ServiceDateRow(
                startDate = startDate,
                startTime = startTime,
                finishDate = finishDate,
                finishTime = finishTime
            )
        }

        Checkbox(
            checked = isSelected,
            onCheckedChange = { _ -> onToggle() }
        )
    }

}

@Composable
fun OperatorList(serviceGroup: List<SummaryUiModel>) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        serviceGroup.forEach {
            OperatorRow(
                name = it.operatorName,
                amount = it.collection
            )
        }
    }
}

@Composable
fun TotalRow(total: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = "Total Collection",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        Text(
            text = "₹$total",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ServiceDateRow(
    startDate: String,
    startTime: String,
    finishDate: String,
    finishTime: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        ExpandableEllipsizedText(
            text = if (startDate == finishDate) {
                "$startDate, $startTime To $finishTime"
            } else {
                "$startDate, $startTime To $finishDate, $finishTime"
            },
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.Gray
            )
        )
    }
}

@Composable
fun ExpandableEllipsizedText(
    text: String,
    style: TextStyle
) {
    var expanded by remember { mutableStateOf(false) }
    var isEllipsized by remember { mutableStateOf(false) }

    if (expanded) {
        LaunchedEffect(Unit) {
            delay(2000)
            expanded = false
        }
    }

    Box {
        Text(
            text = text,
            style = style,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { layoutResult ->
                isEllipsized = layoutResult.isLineEllipsized(0)
            },
            modifier = Modifier.clickable {
                if (isEllipsized) {
                    expanded = true
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .padding(8.dp)
            ) {
                Text(
                    text = text,
                    style = style
                )
            }
        }
    }
}

@Composable
fun OperatorRow(
    name: String,
    amount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "₹$amount",
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
fun DividerLine() {
    HorizontalDivider()
}

@Composable
fun PrintSummaryDialog(
    summaryData: List<SummaryUiModel>,
    selectedIds: Set<Int>,
    onDismiss: () -> Unit
) {

    val selectedServices = remember(summaryData, selectedIds) {
        summaryData
            .filter { selectedIds.contains(it.serviceId) }
            .groupBy { it.serviceId }
    }
    val grandTotal = remember(selectedServices) {
        selectedServices.values
            .sumOf { group -> group.sumOf { it.collection } }
    }

    Dialog(onDismissRequest = onDismiss) {

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Print Preview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    item {
                        Text("----------- Print Header -----------")
                        Text(
                            text = "Krishna",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }


                    itemsIndexed(selectedServices.values.toList()) { index, serviceGroup ->

                        val first = serviceGroup.first()

                        val total = remember(serviceGroup) {
                            serviceGroup.sumOf { it.collection }
                        }

                        val (startDate, startTime) = remember(first.startedOn) {
                            utcToIstConverter(first.startedOn) ?: ("Invalid" to "Invalid")
                        }

                        val (finishDate, finishTime) = remember(first.finishedOn) {
                            utcToIstConverter(first.finishedOn) ?: ("Invalid" to "Invalid")
                        }

                        Column {

                            Text(
                                text = "${index + 1}. ${first.serviceName}",
                                fontWeight = FontWeight.Bold
                            )

                            Text(text = first.regNo)

                            Text(
                                text = if (startDate == finishDate) {
                                    "$startDate, $startTime To $finishTime"
                                } else {
                                    "$startDate, $startTime To $finishDate, $finishTime"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            serviceGroup.forEach {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(it.operatorName)
                                    Text("₹${it.collection}")
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Service Total",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "₹$total",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "GRAND TOTAL",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "₹$grandTotal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        Text("----------- Print Footer ------------")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // TODO: Replace onDismiss with actual printer implementation once printing logic is integrated.
                // Currently dismissing the dialog because printer functionality is not yet implemented.
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Generate Receipt")
                }
            }
        }
    }
}
