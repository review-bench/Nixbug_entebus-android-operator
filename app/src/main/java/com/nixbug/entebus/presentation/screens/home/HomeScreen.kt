package com.nixbug.entebus.presentation.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.NoTransfer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults.FocusedBorderThickness
import androidx.compose.material3.OutlinedTextFieldDefaults.UnfocusedBorderThickness
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nixbug.entebus.presentation.components.DialogBox
import com.nixbug.entebus.presentation.navigation.AppNavItem
import com.nixbug.entebus.ui.theme.DisabledButtonColor
import com.nixbug.entebus.ui.theme.SelectedServiceColor
import com.nixbug.entebus.utils.utcToIstConverter
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.platform.LocalContext
import com.nixbug.entebus.presentation.components.loadingskeleton.ServiceCardSkeleton
import com.nixbug.entebus.presentation.components.loadingskeleton.rememberShimmerBrush
import com.nixbug.entebus.presentation.components.printer.PrinterPickerDialog
import com.nixbug.entebus.utils.BluetoothPermissionHelper
import com.nixbug.entebus.utils.SetStatusBarColor
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType

@Composable
fun HomeScreen(
    rootNavController: NavHostController,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    activity: Activity
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var showPermissionDialog by remember { mutableStateOf(false) }
    var isPermanentDenial by remember { mutableStateOf(false) }
    var finishDutyService by remember { mutableStateOf<HomeUiModel?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.onPermissionGranted(context)
        } else {
            isPermanentDenial = BluetoothPermissionHelper.isPermanentlyDenied(activity)
            showPermissionDialog = true
        }
    }

    val enableBluetoothLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.onPermissionGranted(context)
            }
        }

    fun openPrinterPicker() {
        val hasPermission = BluetoothPermissionHelper.hasBluetoothPermissions(context)
        if (!hasPermission) return

        val result = BluetoothPermissionHelper.getBondedDevices(context)

        result.onSuccess { devices ->
            viewModel.showPrinterPicker(devices)
        }.onFailure {
            SnackbarManager.show(
                "Permission denied to access Bluetooth",
                SnackbarType.ERROR
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                HomeUiEvent.RequestBluetoothPermission -> {
                    permissionLauncher.launch(BluetoothPermissionHelper.REQUIRED_PERMISSIONS)
                }

                HomeUiEvent.RequestEnableBluetooth -> {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    enableBluetoothLauncher.launch(enableBtIntent)
                }

                HomeUiEvent.OpenPrinterPicker -> {
                    openPrinterPicker()
                }

                HomeUiEvent.NavigateToTicketScreen -> {
                    rootNavController.navigate(AppNavItem.TicketScreen.route)
                }
            }
        }
    }
    SetStatusBarColor(
        color = Color.White,
        darkIcons = true
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusManager.clearFocus() }
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp)
        ) {

            ServiceHeader()

            OngoingServiceSection(
                ongoingService = state.ongoingService,
                onResumeService = {
                    val hasPermission = BluetoothPermissionHelper.hasBluetoothPermissions(context)
                    val isEnabled = BluetoothPermissionHelper.isBluetoothEnabled(context)
                    viewModel.handleServiceAction(
                        action = HomeScreenViewModel.ServiceAction.RESUME,
                        hasPermission = hasPermission,
                        isBluetoothEnabled = isEnabled
                    )
                },
                onFinishDutyClick = { service -> finishDutyService = service },
            )

            RowTab(
                selectedTab = state.selectedTab,
                onTabSelected = viewModel::onTabSelected,
                searchQuery = state.searchQuery,
                onSearchChange = viewModel::onSearchQueryChange
            )

            ServiceList(
                assignedServices = state.assignedServiceList,
                downloadedServices = state.downloadedServiceList,
                recentServices = state.recentServiceList,
                otherServices = state.otherServiceList,
                onStartAssigned = { service ->
                    val hasPermission = BluetoothPermissionHelper.hasBluetoothPermissions(context)
                    val isEnabled = BluetoothPermissionHelper.isBluetoothEnabled(context)
                    viewModel.handleServiceAction(
                        action = HomeScreenViewModel.ServiceAction.START_ASSIGNED,
                        service = service,
                        hasPermission = hasPermission,
                        isBluetoothEnabled = isEnabled
                    )
                },
                onStartDownloaded = { service ->
                    val hasPermission = BluetoothPermissionHelper.hasBluetoothPermissions(context)
                    val isEnabled = BluetoothPermissionHelper.isBluetoothEnabled(context)
                    viewModel.handleServiceAction(
                        action = HomeScreenViewModel.ServiceAction.START_DOWNLOADED,
                        service = service,
                        hasPermission = hasPermission,
                        isBluetoothEnabled = isEnabled
                    )
                },
                onRestartRecentService = { service ->
                    val hasPermission = BluetoothPermissionHelper.hasBluetoothPermissions(context)
                    val isEnabled = BluetoothPermissionHelper.isBluetoothEnabled(context)
                    viewModel.handleServiceAction(
                        action = HomeScreenViewModel.ServiceAction.RESTART_RECENT,
                        service = service,
                        hasPermission = hasPermission,
                        isBluetoothEnabled = isEnabled
                    )
                },
                onDownload = viewModel::onDownloadService,
                canStartService = viewModel::canStartService,
                modifier = Modifier.weight(1f),
                selectedTab = state.selectedTab,
                isServiceLoading = state.serviceLoading,
                onLoadMore = viewModel::loadMoreServices,
                isLoadingMore = state.isPaginationLoading,
                downloadingServiceId = state.downloadingServiceId,
                startingServiceId = state.startingServiceId,
                hasMore = state.hasMore,
                onLoadMoreAssigned = viewModel::loadMoreAssignedServices,
                isAssignedLoadingMore = state.isAssignedPaginationLoading,
                assignedHasMore = state.assignedHasMore,

                )
        }
    }

    finishDutyService?.let { service ->
        DialogBox(
            title = "Finish Service?",
            message = "Are you sure you want to finish the service from ${
                finishDutyService?.startingLandmarkName ?: "starting point"
            } to ${
                finishDutyService?.endingLandmarkName ?: "ending point"
            }?",
            confirmText = "Finish",
            onConfirmClick = {
                viewModel.onFinishService(service)
                finishDutyService = null
            },
            dismissible = true,
            onDismiss = { finishDutyService = null },
            showExitButton = true,
            exitText = "Cancel",
            onExitClick = { finishDutyService = null },
            titleColor = MaterialTheme.colorScheme.primary,
            confirmBtnColor = MaterialTheme.colorScheme.primary,
            exitBtnBorderColor = MaterialTheme.colorScheme.primary
        )
    }
    if (showPermissionDialog) {
        DialogBox(
            title = "Permission Required",
            message = "Bluetooth permission is required to start or resume services. Without it, ticket printing and device connection will not work properly.",
            confirmText = if (isPermanentDenial) "Open Settings" else "Retry",
            dismissible = true,
            onDismiss = { showPermissionDialog = false },
            showExitButton = true,
            exitText = "Cancel",
            onExitClick = { showPermissionDialog = false },
            onConfirmClick = {
                showPermissionDialog = false

                if (isPermanentDenial) {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    ).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    activity.startActivity(intent)
                } else {
                    permissionLauncher.launch(
                        BluetoothPermissionHelper.REQUIRED_PERMISSIONS
                    )
                }
            },
            titleColor = MaterialTheme.colorScheme.primary,
            confirmBtnColor = MaterialTheme.colorScheme.primary,
            exitBtnBorderColor = MaterialTheme.colorScheme.primary
        )
    }
    if (state.showPrinterPicker) {
        PrinterPickerDialog(
            pairedDevices = state.pairedPrinters,
            isConnecting = state.isConnectingPrinter,
            onDismiss = { viewModel.hidePrinterPicker() },
            onDeviceSelected = { deviceAddress ->
                viewModel.onPrinterSelected(deviceAddress)
            },
            context = context
        )
    }
}

@Composable
fun ServiceHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 1.dp)
            .padding(horizontal = 12.dp)
    ) {
        Text(
            "All Services",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )
        Text(
            "Displaying All Services",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(5.dp))
        HorizontalDivider()
    }
}

@Composable
fun OngoingServiceSection(
    ongoingService: HomeUiModel?,
    onResumeService: () -> Unit,
    onFinishDutyClick: (HomeUiModel) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp),
    ) {
        ongoingService?.let { service ->
            val startDateTime = utcToIstConverter(service.startingAt)

            Text(
                text = "ONGOING SERVICE",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                color = Color.DarkGray
            )

            ServiceCard(
                service = service,
                cardType = CardType.ONGOING,
                onActionClick = { onResumeService() },
                date = startDateTime?.let { (date, _) -> date } ?: "Date unavailable",
                time = startDateTime?.let { (_, time) -> time } ?: "Time unavailable",
                isEnabled = true,
                onFinishDutyClick = { onFinishDutyClick(service) }
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun RowTab(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    val tabs = listOf("Your Services", "Downloaded", "Recent", "Others")
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .background(color = Color.White)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { index, title ->

            val isSelected = selectedTab == index
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color(
                    0x45C5EAD9
                ),
                label = "TabColorAnimation"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color(0xFF6B7C75),
                label = "TextColorAnimation"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(backgroundColor)
                    .clickable { onTabSelected(index) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(6.dp))

    if (selectedTab == 3) {
        ServiceSearchBar(searchQuery, onSearchChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceSearchBar(query: String, onChange: (String) -> Unit) {
    BasicTextField(
        value = query,
        onValueChange = onChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(40.dp),
        singleLine = true,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = query,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = remember { MutableInteractionSource() },
                trailingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null)
                },
                placeholder = {
                    Text("Search...", style = MaterialTheme.typography.bodyMedium)
                },

                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = true,
                        isError = false,
                        interactionSource = remember { MutableInteractionSource() },
                        colors = OutlinedTextFieldDefaults.colors(),
                        shape = RoundedCornerShape(12.dp),
                        focusedBorderThickness = FocusedBorderThickness,
                        unfocusedBorderThickness = UnfocusedBorderThickness,
                    )
                }
            )
        }
    )
}

@Composable
fun ServiceList(
    assignedServices: List<HomeUiModel>,
    downloadedServices: List<HomeUiModel>,
    recentServices: List<HomeUiModel>,
    otherServices: List<HomeUiModel>,
    onStartAssigned: (HomeUiModel) -> Unit,
    onStartDownloaded: (HomeUiModel) -> Unit,
    onRestartRecentService: (HomeUiModel) -> Unit,
    onDownload: (HomeUiModel) -> Unit,
    canStartService: (String) -> Boolean,
    modifier: Modifier = Modifier,
    selectedTab: Int,
    isServiceLoading: Boolean,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    onLoadMoreAssigned: () -> Unit,
    isAssignedLoadingMore: Boolean,
    assignedHasMore: Boolean,
    startingServiceId: Int?,
    downloadingServiceId: Int?,
) {

    val currentList = when (selectedTab) {
        0 -> assignedServices
        1 -> downloadedServices
        2 -> recentServices
        else -> otherServices
    }

    if (isServiceLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center

        ) {
            val shimmerBrush = rememberShimmerBrush()

            Column {
                repeat(5) {
                    ServiceCardSkeleton(shimmerBrush = shimmerBrush)
                }
            }
        }
        return
    }

    if (currentList.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmptyServiceState(
                icon = Icons.Filled.NoTransfer,
                title = "No services available!",
                description = when (selectedTab) {
                    0 -> "You don’t have any assigned services right now. You can download services from the Other Services tab."
                    1 -> "You don’t have any downloaded services yet. Browse Other Services to download services you’d like to start."
                    2 -> "You don’t have any recent services right now. Once you complete a service, it will appear here."
                    else -> "No services are available right now. Please check back later."
                }
            )
        }
        return
    }

    when (selectedTab) {

        0 -> ServiceTabContent(
            services = assignedServices,
            cardType = CardType.ASSIGNED,
            onActionClick = onStartAssigned,
            canStartService = canStartService,
            modifier = modifier.padding(horizontal = 16.dp),
            downloadingServiceId = downloadingServiceId,
            onLoadMore = onLoadMoreAssigned,
            isLoadingMore = isAssignedLoadingMore,
            hasMore = assignedHasMore,
            startingServiceId = startingServiceId
        )

        1 -> ServiceTabContent(
            services = downloadedServices,
            cardType = CardType.DOWNLOADED,
            onActionClick = onStartDownloaded,
            canStartService = canStartService,
            modifier = modifier.padding(horizontal = 16.dp),
            downloadingServiceId = downloadingServiceId,
            startingServiceId = startingServiceId
        )

        2 -> ServiceTabContent(
            services = recentServices,
            cardType = CardType.RECENT,
            onActionClick = onRestartRecentService,
            canStartService = canStartService,
            modifier = modifier.padding(horizontal = 16.dp),
            downloadingServiceId = downloadingServiceId,
            startingServiceId = startingServiceId
        )

        3 -> ServiceTabContent(
            services = otherServices,
            cardType = CardType.OTHER,
            onActionClick = onDownload,
            canStartService = canStartService,
            modifier = modifier.padding(horizontal = 16.dp),
            onLoadMore = onLoadMore,
            isLoadingMore = isLoadingMore,
            downloadingServiceId = downloadingServiceId,
            hasMore = hasMore,
            startingServiceId = startingServiceId
        )
    }
}

@Composable
fun ServiceTabContent(
    services: List<HomeUiModel>,
    cardType: CardType,
    onActionClick: (HomeUiModel) -> Unit,
    canStartService: (String) -> Boolean,
    modifier: Modifier = Modifier,
    onLoadMore: (() -> Unit)? = null,
    isLoadingMore: Boolean = false,
    hasMore: Boolean = true,
    downloadingServiceId: Int?,
    startingServiceId: Int?
) {

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = WindowInsets.ime.asPaddingValues()
    ) {

        item { Spacer(modifier = Modifier.height(10.dp)) }

        items(services) { service ->

            val startDateTime = utcToIstConverter(service.startingAt)
            val isAnyDownloading = downloadingServiceId != null
            val isAnyStarting = startingServiceId != null

            ServiceCard(
                service = service,
                cardType = cardType,
                onActionClick = { onActionClick(service) },
                date = startDateTime?.first ?: "Date unavailable",
                time = startDateTime?.second ?: "Time unavailable",
                isDownloading = service.id == downloadingServiceId,
                isStarting = service.id == startingServiceId,
                isEnabled = when (cardType) {
                    CardType.ASSIGNED,
                    CardType.DOWNLOADED -> canStartService(service.startingAt)

                    else -> true
                },
                isAnyDownloading = isAnyDownloading,
                isAnyStarting = isAnyStarting
            )

            Spacer(modifier = Modifier.height(15.dp))
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun EmptyServiceState(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                icon,
                null,
                tint = DisabledButtonColor,
                modifier = Modifier
                    .size(110.dp)
                    .padding(bottom = 20.dp)
            )

            Text(
                text = title,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            if (description != null) {
                Text(
                    text = description,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
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
    isEnabled: Boolean,
    onFinishDutyClick: ((HomeUiModel) -> Unit)? = null,
    isDownloading: Boolean = false,
    isAnyDownloading: Boolean = false,
    isStarting: Boolean = false,
    isAnyStarting: Boolean = false
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
                text = "${service.startingLandmarkName} -> ${service.endingLandmarkName}",
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
                                service.registrationNumber,
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
                        val isActionEnabled = isEnabled && !isAnyDownloading && !isAnyStarting
                        SmallCardButton(
                            cardType,
                            onActionClick,
                            enabled = isActionEnabled,
                            isLoading = isDownloading || isStarting
                        )
                    }
                }

            }

            //-------------- RESUME BUTTON----------------------//
            if (cardType == CardType.ONGOING) {
                Spacer(Modifier.height(12.dp))
                OngoingActionButtons(
                    onResume = onActionClick,
                    onEnd = { onFinishDutyClick?.invoke(service) }
                )
            }
        }
    }

}

@Composable
fun SmallCardButton(
    cardType: CardType,
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean = false
) {

    val text = when (cardType) {
        CardType.ASSIGNED -> "Start"
        CardType.DOWNLOADED -> "Start"
        CardType.RECENT -> "Restart"
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
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = Color.White
            )
        } else {
            Text(
                text,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun OngoingActionButtons(
    onResume: () -> Unit,
    onEnd: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onResume,
            modifier = Modifier
                .weight(1f)
                .height(35.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Resume", color = Color.White, style = MaterialTheme.typography.titleMedium)
        }

        Button(
            onClick = onEnd,
            modifier = Modifier
                .weight(1f)
                .height(35.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Finish", color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
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
