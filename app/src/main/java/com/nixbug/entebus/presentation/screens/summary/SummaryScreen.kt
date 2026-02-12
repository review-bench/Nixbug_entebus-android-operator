package com.nixbug.entebus.presentation.screens.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nixbug.entebus.utils.utcToIstConverter
import kotlinx.coroutines.delay

@Composable
fun SummaryScreen(
    viewModel: SummaryScreenViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

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
                .padding(16.dp)
                .padding(bottom = 62.dp)
                .padding(horizontal = 12.dp)
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
    val groupedByService = summaryData.groupBy { it.serviceId }

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
fun SummaryCard(
    serviceGroup: List<SummaryUiModel>, isSelected: Boolean,
    onClick: () -> Unit
) {

    val firstItem = serviceGroup.first()
    val totalCollection = serviceGroup.sumOf { it.collection }
    val (startDate, startTime) = utcToIstConverter(firstItem.startedOn)
        ?: ("Invalid date" to "Invalid time")

    val (finishDate, finishTime) = utcToIstConverter(firstItem.finishedOn)
        ?: ("Invalid date" to "Invalid time")
    val containerColor =
        if (isSelected)
            Color(0xFFE8F5E9)
        else
            MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier.fillMaxWidth(),
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
            .padding(horizontal = 8.dp, vertical = 10.dp)
            .fillMaxWidth(),
    ) {

        ServiceHeader(
            serviceName = firstItem.serviceName,
            regNo = firstItem.regNO,
            isSelected = isSelected,
            onToggle = onToggle,
            startDate = startDate,
            startTime = startTime,
            finishDate = finishDate,
            finishTime = finishTime
        )
        Spacer(modifier = Modifier.height(8.dp))
//        DividerLine()
//        Spacer(modifier = Modifier.height(8.dp))
        OperatorList(serviceGroup)
        Spacer(modifier = Modifier.height(8.dp))
        DividerLine()
        Spacer(modifier = Modifier.height(8.dp))
        TotalRow(totalCollection)
    }
}

@Composable
fun ServiceHeader(
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
            onCheckedChange = { onToggle() }
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
                "$startDate, $startTime TO $finishTime"
            } else {
                "$startDate,$startTime TO $finishDate,$finishTime"
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
                if (isEllipsized) {   // 👈 ONLY open if truncated
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
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "₹$amount",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
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
fun DividerLine() {
    HorizontalDivider()
}

@Composable
fun PrintButton(
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "PRINT SUMMARY",
            fontWeight = FontWeight.SemiBold
        )
    }
}
