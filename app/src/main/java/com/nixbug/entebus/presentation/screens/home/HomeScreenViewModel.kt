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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val printerManager: PrinterManager
) : ViewModel() {

    // ----------------------------------------------------------------
    // STATE
    // ----------------------------------------------------------------

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeUiEvent>()
    val events: SharedFlow<HomeUiEvent> = _events.asSharedFlow()

    // ----------------------------------------------------------------
    // SERVICE ACTION ENUM
    // ----------------------------------------------------------------

    enum class ServiceAction {
        START_ASSIGNED,
        START_DOWNLOADED,
        RESTART_RECENT,
        RESUME
    }

    private var pendingAction: ServiceAction? = null
    private var pendingService: HomeUiModel? = null

    // ----------------------------------------------------------------
    // MAIN UNIFIED ENTRY POINT
    // ----------------------------------------------------------------

    fun handleServiceAction(
        action: ServiceAction,
        service: HomeUiModel? = null,
        hasPermission: Boolean,
        isBluetoothEnabled: Boolean
    ) {

        if (action != ServiceAction.RESUME && checkIfServiceAlreadyRunning()) return

        pendingAction = action
        pendingService = service

        if (!hasPermission) {
            emitPermissionRequest()
            return
        }

        if (!isBluetoothEnabled) {
            emitEnableBluetoothRequest()
            return
        }

        proceedAfterBluetoothReady()
    }

    // ----------------------------------------------------------------
    // PERMISSION RESULT CALLBACK
    // ----------------------------------------------------------------

    fun onPermissionGranted(context: Context) {
        val hasPermission = BluetoothPermissionHelper.hasBluetoothPermissions(context)
        val isEnabled = BluetoothPermissionHelper.isBluetoothEnabled(context)

        if (hasPermission && isEnabled) {
            proceedAfterBluetoothReady()
        } else {
            emitEnableBluetoothRequest()
            return
        }
    }

    // ----------------------------------------------------------------
    // CORE FLOW CONTINUATION
    // ----------------------------------------------------------------

    private fun proceedAfterBluetoothReady() {
        if (printerManager.isConnected()) {
            completeServiceFlow()
        } else {
            emitOpenPrinterPicker()
        }
    }

    private fun completeServiceFlow() {
        when (pendingAction) {

            ServiceAction.START_ASSIGNED,
            ServiceAction.START_DOWNLOADED,
            ServiceAction.RESTART_RECENT -> confirmServiceStart()

            ServiceAction.RESUME -> navigateToTicketScreen()

            null -> {}
        }

        pendingAction = null
        pendingService = null
    }

    // ----------------------------------------------------------------
    // PRINTER SELECTION
    // ----------------------------------------------------------------

    fun showPrinterPicker(devices: List<BluetoothDevice>) {
        _uiState.update {
            it.copy(
                pairedPrinters = devices,
                showPrinterPicker = true
            )
        }
    }

    fun hidePrinterPicker() {
        _uiState.update { it.copy(showPrinterPicker = false) }
        pendingService = null
        pendingAction = null
    }

    fun onPrinterSelected(address: String) {
        _uiState.update { it.copy(isConnectingPrinter = true) }

        viewModelScope.launch {
            val success = printerManager.connectToPrinter(address)
            if (success) {
                completeServiceFlow()
                _uiState.update { it.copy(isConnectingPrinter = false) }
                _uiState.update { it.copy(showPrinterPicker = false) }
            } else {
                _uiState.update { it.copy(isConnectingPrinter = false) }
                _uiState.update { it.copy(showPrinterPicker = false) }
                SnackbarManager.show(
                    "Unable to connect to printer,make sure your printer is powered on",
                    SnackbarType.ERROR
                )
            }
        }
    }

    // ----------------------------------------------------------------
    // SERVICE CONFIRMATION
    // ----------------------------------------------------------------

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

        navigateToTicketScreen()
    }

    private fun checkIfServiceAlreadyRunning(): Boolean {
        return if (_uiState.value.ongoingService != null) {
            SnackbarManager.show(
                "A service is already running",
                SnackbarType.ERROR
            )
            true
        } else false
    }

    // ----------------------------------------------------------------
    // EVENTS
    // ----------------------------------------------------------------

    private fun emitPermissionRequest() {
        viewModelScope.launch {
            _events.emit(HomeUiEvent.RequestBluetoothPermission)
        }
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

    private fun navigateToTicketScreen() {
        viewModelScope.launch {
            _events.emit(HomeUiEvent.NavigateToTicketScreen)
        }
    }

    // ----------------------------------------------------------------
    // OTHER UI STATE HANDLERS
    // ----------------------------------------------------------------

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onTabSelected(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onDownloadService(service: HomeUiModel) {
        _uiState.update { state ->
            state.copy(
                downloadedServiceList = state.downloadedServiceList + service,
                otherServiceList = state.otherServiceList.filter { it.id != service.id }
            )
        }
    }

    fun onFinishDuty(service: HomeUiModel) {
        printerManager.closeConnection()

        _uiState.update { state ->
            state.copy(
                recentServiceList = state.recentServiceList + service,
                ongoingService = null
            )
        }
    }

    // ----------------------------------------------------------------
    // HELPERS
    // ----------------------------------------------------------------

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