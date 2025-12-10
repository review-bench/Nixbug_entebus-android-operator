package com.nixbug.entebus.presentation.screens.homeScreen

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = viewModel()) {
    val state = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val filteredOtherServices = state.otherServiceList.filter {
        it.regNo.contains(searchQuery, ignoreCase = true)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                HomeUiEvent.ServiceAlreadyRunning -> {
                    Toast.makeText(context, "A service is already running", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            }) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar()
            ServiceList(
                ongoingService = state.ongoingService,
                assignedServices = state.assignedServiceList,
                downloadedServices = state.downloadedServiceList,
                otherServices = filteredOtherServices,
                onResume = { viewModel.onResumeService(it) },
                onStartAssigned = { viewModel.onStartAssigned(it) },
                onStartDownloaded = { viewModel.onStartDownloaded(it) },
                onDownload = { viewModel.onDownloadService(it) },
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                viewModel = viewModel,
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent)
            )
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
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { selectedTab = index }
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
                            .fillMaxWidth(.9f)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ServiceList(
    ongoingService: HomeUiModel?,
    assignedServices: List<HomeUiModel>,
    downloadedServices: List<HomeUiModel>,
    otherServices: List<HomeUiModel>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onResume: (HomeUiModel) -> Unit,
    onStartAssigned: (HomeUiModel) -> Unit,
    onStartDownloaded: (HomeUiModel) -> Unit,
    onDownload: (HomeUiModel) -> Unit,
    viewModel: HomeScreenViewModel,
    modifier: Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 12.dp),
        contentPadding = WindowInsets.ime.asPaddingValues()
    ) {
        item { Spacer(Modifier.height(12.dp)) }

        // ------------------------ ONGOING SERVICE----------------------------
        if (ongoingService != null) {
            item { SectionHeader("Ongoing Service") }
            item {
                ServiceCard(
                    service = ongoingService,
                    cardType = CardType.ONGOING,
                    onActionClick = { onResume(ongoingService) },
                    viewModel = viewModel
                )
            }
            if (assignedServices.isNotEmpty() || downloadedServices.isNotEmpty() || otherServices.isNotEmpty()) {
                item { SectionDivider() }
            }
        }

        // ------------------ ASSIGNED SERVICES -------------------------------
        if (assignedServices.isNotEmpty()) {
            item { SectionHeader("Assigned Service") }
            items(assignedServices) { service ->
                ServiceCard(
                    service = service,
                    cardType = CardType.ASSIGNED,
                    onActionClick = { onStartAssigned(service) },
                    viewModel = viewModel
                )
            }
            if (downloadedServices.isNotEmpty() || otherServices.isNotEmpty()) {
                item { SectionDivider() }
            }
        }

        // ------------------ DOWNLOADED SERVICES -----------------------------
        if (downloadedServices.isNotEmpty()) {
            item { SectionHeader("Downloaded Service") }
            items(downloadedServices) { service ->
                ServiceCard(
                    service = service,
                    cardType = CardType.DOWNLOADED,
                    onActionClick = { onStartDownloaded(service) },
                    viewModel = viewModel
                )
            }
            if (otherServices.isNotEmpty()) {
                item { SectionDivider() }
            }
        }

        // ------------------ OTHER SERVICES ----------------------------------
        item(key = "search_bar") {
            Row(
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader("Other Services")
                SearchBar(query = searchQuery, onChange = onSearchChange)
            }
            Spacer(modifier = Modifier.height(15.dp))
        }

        if (otherServices.isNotEmpty()) {
            items(otherServices) { service ->
                ServiceCard(
                    service = service,
                    cardType = CardType.OTHER,
                    onActionClick = { onDownload(service) },
                    viewModel = viewModel
                )
            }
        } else {
            item {
                Text(
                    text = "No services available!",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    textAlign = TextAlign.Center

                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ServiceCard(
    service: HomeUiModel,
    cardType: CardType,
    onActionClick: () -> Unit,
    viewModel: HomeScreenViewModel
) {
    val (date, time) = viewModel.convertUtcToIst(service.startingAt)
    val isEnabled =
        when (cardType) {
            CardType.ASSIGNED, CardType.DOWNLOADED ->
                viewModel.canStartService(service.startingAt)
            else -> true
        }
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (cardType == CardType.ONGOING) Color(0xFFDFF6DD) else Color(
                0xFFFAFAFA
            )
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

            //-------------- ROUTE NAME ----------------------//
            Text(
                text = viewModel.extractRouteName(service.name),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 3.dp),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = if (cardType == CardType.ONGOING) Alignment.Top else Alignment.CenterVertically) {

                Column(Modifier.weight(1f)) {

                    //-------------- DATE ----------------------//
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.CalendarMonth,
                            null,
                            tint = Color(0xFF8BC34A)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            date,
                            fontSize = 15.sp,
                            color = Color.DarkGray
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    //-------------- REG NUMBER ----------------------//
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.DirectionsBus,
                                null,
                                tint = Color(0xFF8BC34A)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                service.regNo,
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                        }
                    }

                }

                Column(horizontalAlignment = Alignment.End) {

                    //-------------- TIME ----------------------//
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF8BC34A)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            time,
                            fontSize = 15.sp,
                            color = Color.DarkGray
                        )
                    }

                    //-------------- START/DOWNLOAD BUTTON ----------------------//
                    if (cardType != CardType.ONGOING) {
                        Spacer(Modifier.height(10.dp))
                        SmallCardButton(cardType, onActionClick, enabled = isEnabled)
                    }
                }

            }

            //-------------- RESUME BUTTON----------------------//
            if (cardType == CardType.ONGOING) {
                Spacer(Modifier.height(12.dp))
                FullWidthActionButton("Resume Service", onActionClick)
            }
        }
    }
    Spacer(modifier = Modifier.height(15.dp))
}

@Composable
fun SearchBar(query: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onChange,
        label = { Text("Search Reg Number", fontSize = 12.sp, color = Color.Gray) },
        singleLine = true,
        modifier = Modifier
            .width(200.dp)
            .height(65.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        trailingIcon = {
            Icon(imageVector = Icons.Filled.Search,contentDescription = "Show password")

        }
    )
}

@Composable
fun SmallCardButton(cardType: CardType, onClick: () -> Unit, enabled: Boolean) {

    val text = when (cardType) {
        CardType.ASSIGNED -> "Start"
        CardType.DOWNLOADED -> "Start"
        CardType.OTHER -> "Download"
        else -> ""
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .height(35.dp)
            .width(95.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF8BC34A),
            disabledContainerColor = Color(0x5B9BDA54)
        ),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun FullWidthActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(35.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
        color = Color.Black
    )
}

@Composable
fun SectionDivider() {
    Spacer(Modifier.height(12.dp))
    HorizontalDivider(thickness = Dp.Hairline, color = Color.Black)
    Spacer(Modifier.height(12.dp))
}
