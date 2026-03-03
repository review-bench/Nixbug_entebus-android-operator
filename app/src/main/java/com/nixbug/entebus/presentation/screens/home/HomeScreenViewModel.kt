package com.nixbug.entebus.presentation.screens.home

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.utils.BluetoothPermissionHelper
import com.nixbug.entebus.utils.printer.PrinterManager
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val printerManager: PrinterManager) :
    ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeUiEvent>()
    val events: SharedFlow<HomeUiEvent> = _events.asSharedFlow()

    private var pendingAction: PendingServiceAction? = null
    private var pendingService: HomeUiModel? = null

    private fun confirmServiceStart() {
        val service = pendingService ?: return

        _uiState.update { state ->
            state.copy(
                ongoingService = service,
                assignedServiceList = state.assignedServiceList.filter { it.id != service.id },
                downloadedServiceList = state.downloadedServiceList.filter { it.id != service.id },
                recentServiceList = state.recentServiceList.filter { it.id != service.id }
            )
        }
        pendingService = null
        navigateToTicketScreen()
    }

    fun onPrinterSelected(address: String) {
        _uiState.update { it.copy(showPrinterPicker = false) }

        viewModelScope.launch {
            val success = printerManager.connectToPrinter(address)
            if (success) {
                if (pendingService != null) {
                    confirmServiceStart()
                } else {
                    navigateToTicketScreen()
                }
            } else {
                SnackbarManager.show("Failed to connect to printer", SnackbarType.ERROR)
            }
        }
    }

    private fun checkAndEmitIfServiceAlreadyRunning(): Boolean {
        return if (_uiState.value.ongoingService != null) {
            SnackbarManager.show(
                message = "A service is already running",
                type = SnackbarType.ERROR,
            )
            true
        } else {
            false
        }
    }

    private fun emitPermissionRequest() {
        viewModelScope.launch {
            _events.emit(HomeUiEvent.RequestBluetoothPermission)
        }
    }

    fun onPermissionGranted(context: Context) {

        val hasPermission = BluetoothPermissionHelper.hasBluetoothPermissions(context)
        val isEnabled = BluetoothPermissionHelper.isBluetoothEnabled(context)

        when (val action = pendingAction) {

            is PendingServiceAction.StartAssigned -> {
                onStartAssigned(
                    service = action.service,
                    hasPermission = hasPermission,
                    isBluetoothEnabled = isEnabled
                )
            }

            is PendingServiceAction.StartDownloaded -> {
                onStartDownloaded(
                    service = action.service,
                    hasPermission = hasPermission,
                    isBluetoothEnabled = isEnabled
                )
            }

            is PendingServiceAction.RestartRecent -> {
                onRestartRecentService(
                    service = action.service,
                    hasPermission = hasPermission,
                    isBluetoothEnabled = isEnabled
                )
            }

            else -> {
                onResumeService(hasPermission, isEnabled)
            }
        }

        pendingAction = null
    }

    private fun emitEnableBluetoothRequest() {
        viewModelScope.launch {
            _events.emit(HomeUiEvent.RequestEnableBluetooth)
        }
    }

    private fun emitOpenPrinterPicker() {
        viewModelScope.launch {
            _events.emit(HomeUiEvent.OpenPrinterPicker)
        }
    }

    fun showPrinterPicker(devices: List<BluetoothDevice>) {
        _uiState.update {
            it.copy(
                pairedPrinters = devices,
                showPrinterPicker = true
            )
        }
    }

    private fun navigateToTicketScreen() {
        viewModelScope.launch {
            _events.emit(HomeUiEvent.NavigateToTicketScreen)
        }
    }

    fun hidePrinterPicker() {
        _uiState.update {
            it.copy(showPrinterPicker = false)
        }
        pendingService = null
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onTabSelected(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onResumeService(hasPermission: Boolean, isBluetoothEnabled: Boolean) {
        if (!hasPermission) {
            emitPermissionRequest()
            return
        }
        if (!isBluetoothEnabled) {
            emitEnableBluetoothRequest()
            return
        }
        if (printerManager.isConnected()) {
            navigateToTicketScreen()
        } else {
            emitOpenPrinterPicker()
        }
    }

    fun onStartAssigned(service: HomeUiModel, hasPermission: Boolean, isBluetoothEnabled: Boolean) {
        if (checkAndEmitIfServiceAlreadyRunning()) return
        pendingService = service
        if (!hasPermission) {
            pendingAction = PendingServiceAction.StartAssigned(service)
            emitPermissionRequest()
            return
        }
        if (!isBluetoothEnabled) {
            pendingAction = PendingServiceAction.StartAssigned(service)
            emitEnableBluetoothRequest()
            return
        }
        startAssignedInternal()
    }

    private fun startAssignedInternal() {
        if (printerManager.isConnected()) {
            confirmServiceStart()
        } else {
            emitOpenPrinterPicker()
        }
    }

    fun onStartDownloaded(
        service: HomeUiModel,
        hasPermission: Boolean,
        isBluetoothEnabled: Boolean
    ) {
        if (checkAndEmitIfServiceAlreadyRunning()) return
        pendingService = service
        if (!hasPermission) {
            pendingAction = PendingServiceAction.StartDownloaded(service)
            emitPermissionRequest()
            return
        }
        if (!isBluetoothEnabled) {
            pendingAction = PendingServiceAction.StartDownloaded(service)
            emitEnableBluetoothRequest()
            return
        }
        startDownloadedInternal()
    }

    private fun startDownloadedInternal() {
        if (printerManager.isConnected()) {
            confirmServiceStart()
        } else {
            emitOpenPrinterPicker()
        }
    }

    fun onRestartRecentService(
        service: HomeUiModel,
        hasPermission: Boolean,
        isBluetoothEnabled: Boolean
    ) {
        if (checkAndEmitIfServiceAlreadyRunning()) return
        pendingService = service
        if (!hasPermission) {
            pendingAction = PendingServiceAction.RestartRecent(service)
            emitPermissionRequest()
            return
        }
        if (!isBluetoothEnabled) {
            pendingAction = PendingServiceAction.RestartRecent(service)
            emitEnableBluetoothRequest()
            return
        }
        restartRecentInternal()
    }

    private fun restartRecentInternal() {
        if (printerManager.isConnected()) {
            confirmServiceStart()
        } else {
            emitOpenPrinterPicker()
        }
    }

    fun onDownloadService(service: HomeUiModel) {
        _uiState.update { state ->
            val newOtherServices = state.otherServiceList.filter { it.id != service.id }
            state.copy(
                downloadedServiceList = state.downloadedServiceList + service,
                otherServiceList = newOtherServices,
            )
        }
    }

    fun onFinishDuty(service: HomeUiModel) {
        printerManager.closeConnection()
        _uiState.update { state ->
            state.copy(
                recentServiceList = state.recentServiceList + service,
                ongoingService = null,
            )
        }
    }

    /**
     * Extracts a clean route name from a service name string.
     *
     * This function removes:
     * 1. A leading date and time prefix in the format:
     *    `yyyy-MM-dd hh:mm AM/PM`
     * 2. Any trailing registration or metadata enclosed in parentheses.
     *
     * Example:
     * Input:  "2024-12-10 08:30 AM City Center -> Airport (KL-01-AB-1234)"
     * Output: "City Center -> Airport"
     *
     * @param fullName The full service name received from the backend.
     * @return A trimmed, user-friendly route name.
     */
    fun extractRouteName(fullName: String): String {
        val noDateTime = fullName.replace(
            Regex("""^\d{4}-\d{2}-\d{2}\s+\d{1,2}:\d{2}\s*(AM|PM)\s*"""), ""
        )

        val noReg = noDateTime.replace(
            Regex("""\s*\([^)]*\)\s*$"""), ""
        )

        return noReg.trim()
    }

    /**
     * Determines whether a service can be started based on its scheduled start time.
     *
     * A service is eligible to start if its IST start time is within
     * 60 minutes (or less) from the current IST time.
     *
     * This logic ensures that services cannot be started too early.
     *
     * @param startingAtUtc ISO-8601 formatted UTC start time of the service.
     * @return `true` if the service can be started, `false` otherwise.
     */
    fun canStartService(startingAtUtc: String): Boolean {
        val instant = Instant.parse(startingAtUtc)

        val istZone = ZoneId.of("Asia/Kolkata")
        val serviceTimeIst = instant.atZone(istZone)

        val nowIst = Instant.now().atZone(istZone)

        val diffMinutes = java.time.Duration.between(nowIst, serviceTimeIst).toMinutes()

        return diffMinutes <= 60
    }
}
