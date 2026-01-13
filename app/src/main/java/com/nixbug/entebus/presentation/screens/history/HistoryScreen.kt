package com.nixbug.entebus.presentation.screens.history

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HistoryScreen(viewModel: HistoryScreenViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            HistoryHeader()
            DutyHistoryList(dutyHistoryData = state.dutyHistoryData)
        }

    }
}

@Composable
fun HistoryHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Duty History")
                Text("Displaying All Duty History")
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
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DutyHistoryList(dutyHistoryData: List<HistoryUiModel>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(),
        contentPadding = WindowInsets.ime.asPaddingValues()
    ) {
        if (dutyHistoryData.isNotEmpty()) {
            item { Spacer(Modifier.height(12.dp)) }
            items(dutyHistoryData) { history ->
                DutyHistoryCard(history)
                 Spacer(Modifier.height(5.dp))
            }

        }
    }
}

@Composable
fun DutyHistoryCard(history: HistoryUiModel) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp,
            focusedElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(15.dp)
        ) {
            Text(text = history.startedOn, modifier = Modifier.padding(bottom = 50.dp))
        }
    }

}
