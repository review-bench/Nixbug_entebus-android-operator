package com.nixbug.entebus.presentation.screens.homeScreen

import android.widget.Toast
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
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nixbug.entebus.presentation.navigation.AppNavItem
import com.nixbug.entebus.ui.theme.DisabledButtonColor
import com.nixbug.entebus.ui.theme.SelectedServiceColor

@Composable
fun HomeScreen(
    rootNavController: NavHostController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    val filteredOtherServices = remember(state.otherServiceList, searchQuery) {
        state.otherServiceList.filter {
            it.regNo.contains(searchQuery, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeUiEvent.ServiceAlreadyRunning -> {
                    Toast.makeText(
                        context,
                        "A service is already running",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is HomeUiEvent.NavigateToTicketScreen -> {
                    rootNavController.navigate(AppNavItem.TicketScreen.route)
                }
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusManager.clearFocus() }
    ) {
        Column(Modifier.fillMaxSize()) {

            TopBar(
                selectedTab = selectedTab,
                onTabChange = { selectedTab = it }
            )

            when (selectedTab) {
                0 -> {
                    ServiceList(
                        ongoingService = state.ongoingService,
                        assignedServices = state.assignedServiceList,
                        downloadedServices = state.downloadedServiceList,
                        otherServices = filteredOtherServices,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        onResumeService = viewModel::onResumeService,
                        onStartAssigned = viewModel::onStartAssigned,
                        onStartDownloaded = viewModel::onStartDownloaded,
                        onDownload = viewModel::onDownloadService,
                        convertUtcToIst = viewModel::convertUtcToIst,
                        extractRouteName = viewModel::extractRouteName,
                        canStartService = viewModel::canStartService,
                        modifier = Modifier.weight(1f)
                    )
                }

                1 -> DutyScreen()
            }
        }
    }
}

@Composable
fun TopBar(selectedTab: Int, onTabChange: (Int) -> Unit) {

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
                    ) { onTabChange(index) }
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (selectedTab == index) Color.Black else Color.Gray
                )
                Spacer(modifier = Modifier.height(3.dp))
                if (selectedTab == index) {
                    Box(
                        Modifier
                            .fillMaxWidth(.9f)
                            .height(4.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(50)
                            )
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
    ongoingService: HomeUiModel?,
    assignedServices: List<HomeUiModel>,
    downloadedServices: List<HomeUiModel>,
    otherServices: List<HomeUiModel>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onResumeService: () -> Unit,
    onStartAssigned: (HomeUiModel) -> Unit,
    onStartDownloaded: (HomeUiModel) -> Unit,
    onDownload: (HomeUiModel) -> Unit,
    convertUtcToIst: (String) -> Pair<String, String>,
    extractRouteName: (String) -> String,
    canStartService: (String) -> Boolean,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 12.dp),
        contentPadding = WindowInsets.ime.asPaddingValues()
    ) {

        item { Spacer(Modifier.height(12.dp)) }

        // ---------------- Ongoing ----------------
        ongoingService?.let { service ->
            val (date, time) = convertUtcToIst(service.startingAt)

            item { SectionHeader("Ongoing Service") }
            item {
                ServiceCard(
                    service = service,
                    cardType = CardType.ONGOING,
                    date = date,
                    time = time,
                    routeName = extractRouteName(service.name),
                    isEnabled = true,
                    onActionClick = { onResumeService() }
                )
            }
            item { SectionDivider() }
        }

        // ---------------- Assigned ----------------
        if (assignedServices.isNotEmpty()) {
            item { SectionHeader("Assigned Services") }

            items(assignedServices) { service ->
                val (date, time) = convertUtcToIst(service.startingAt)

                ServiceCard(
                    service = service,
                    cardType = CardType.ASSIGNED,
                    date = date,
                    time = time,
                    routeName = extractRouteName(service.name),
                    isEnabled = canStartService(service.startingAt),
                    onActionClick = { onStartAssigned(service) }
                )
            }
            item { SectionDivider() }
        }

        // ---------------- Downloaded ----------------
        if (downloadedServices.isNotEmpty()) {
            item { SectionHeader("Downloaded Services") }

            items(downloadedServices) { service ->
                val (date, time) = convertUtcToIst(service.startingAt)

                ServiceCard(
                    service = service,
                    cardType = CardType.DOWNLOADED,
                    date = date,
                    time = time,
                    routeName = extractRouteName(service.name),
                    isEnabled = canStartService(service.startingAt),
                    onActionClick = { onStartDownloaded(service) }
                )
            }
            item { SectionDivider() }
        }

        // ---------------- Other ----------------
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader("Other Services")
                ServiceSearchBar(searchQuery, onSearchChange)
            }
            Spacer(modifier = Modifier.height(15.dp))
        }

        if (otherServices.isNotEmpty()) {
            items(otherServices) { service ->
                val (date, time) = convertUtcToIst(service.startingAt)

                ServiceCard(
                    service = service,
                    cardType = CardType.OTHER,
                    date = date,
                    time = time,
                    routeName = extractRouteName(service.name),
                    isEnabled = true,
                    onActionClick = { onDownload(service) }
                )
            }
        } else {
            item {
                Text(
                    text = "No services available!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ServiceCard(
    service: HomeUiModel,
    cardType: CardType,
    onActionClick: () -> Unit,
    date: String,
    time: String,
    routeName: String,
    isEnabled: Boolean
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (cardType == CardType.ONGOING) SelectedServiceColor else
                MaterialTheme.colorScheme.surface
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
                text = routeName,
                style = MaterialTheme.typography.titleMedium,
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
                            "Calendar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            date,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.DarkGray
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    //-------------- REG NUMBER ----------------------//
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.DirectionsBus,
                                "Vehicle registration",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                service.regNo,
                                style = MaterialTheme.typography.labelLarge,
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
                            contentDescription = "Service time",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            time,
                            style = MaterialTheme.typography.labelLarge,
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
fun ServiceSearchBar(query: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onChange,
        label = {
            Text(
                "Search...",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )
        },
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
            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search icon")
        }
    )
}

@Composable
fun SmallCardButton(cardType: CardType, onClick: () -> Unit, enabled: Boolean) {

    val text = when (cardType) {
        CardType.ASSIGNED -> "Start"
        CardType.DOWNLOADED -> "Start"
        else -> "Download"

    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .height(35.dp)
            .width(95.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = DisabledButtonColor
        ),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}

@Composable
fun FullWidthActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(35.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium, color = Color.White)
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
        color = Color.Black
    )
}

@Composable
fun SectionDivider() {
    Spacer(Modifier.height(12.dp))
    HorizontalDivider(thickness = Dp.Hairline, color = Color.Black)
    Spacer(Modifier.height(12.dp))
}

@Composable
fun DutyScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Duty Page",
            style = MaterialTheme.typography.titleLarge
        )
    }
}
