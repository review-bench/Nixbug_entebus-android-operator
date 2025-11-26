package com.nixbug.entebus.presentation.screens.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = viewModel()) {
    val state = viewModel.uiState.collectAsState().value
    Box {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar()
            ServiceList(
                services = state.companyList,
                onServiceClick = {},
                modifier = Modifier.weight(1f)
            )
            ServiceStartButton()
        }
    }
}

@Composable
fun TopBar() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All Services", "Duty")
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        tabs.forEachIndexed { index, title ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() },
                    indication = null) { selectedTab = index }
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = if (selectedTab == index) FontWeight.ExtraBold else FontWeight.Bold,
                    color = if (selectedTab == index) Color.Black else Color.Gray
                )
                Spacer(modifier = Modifier.height(3.dp))
                if (selectedTab == index) {
                    Box(
                        Modifier
                            .width(100.dp)
                            .height(4.dp)
                            .background(Color(0xFF8BC34A), shape = RoundedCornerShape(50))
                    )
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}


@Composable
fun ServiceList(
    services: List<HomeUiModel>,
    onServiceClick: (HomeUiModel) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 12.dp)

    ) {
        item {
            Spacer(Modifier.height(12.dp))
        }
        items(services) { service ->
            ServiceCard(service = service, onServiceClick)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun ServiceCard(service: HomeUiModel, onClick: (HomeUiModel) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(service) },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp,
            focusedElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1f)
            ) {

                Text(
                    text = service.route,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF8BC34A)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Starting At: ${service.startTime}", fontSize = 14.sp)
                }

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.DirectionsBus,
                        contentDescription = null,
                        tint = Color(0xFF8BC34A)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Reg No: ${service.regNo}", fontSize = 14.sp)
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color(0xFF8BC34A),
                        shape = RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Alignment.Center

            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowOutward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)

                )
            }

        }
    }
}


@Composable
fun ServiceStartButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { /* Handle button click */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
            shape = RoundedCornerShape(10.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 10.dp,
                focusedElevation = 8.dp
            )
        ) {
            Text(
                text = "Start Service",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,

                )
        }
    }
}

//@Composable
//fun