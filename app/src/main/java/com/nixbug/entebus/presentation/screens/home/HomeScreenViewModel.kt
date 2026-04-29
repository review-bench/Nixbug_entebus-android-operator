package com.nixbug.entebus.presentation.screens.home

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.domain.usecase.service.GetServiceDetailUseCase
import com.nixbug.entebus.domain.usecase.service.GetServiceUseCase
import com.nixbug.entebus.utils.BluetoothPermissionHelper
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.utils.printer.PrinterManager
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val printerManager: PrinterManager,
    private val getMaskedServices: GetServiceUseCase,
    private val getServiceDetail: GetServiceDetailUseCase
) : ViewModel() {

    // STATE
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeUiEvent>()
    val events: SharedFlow<HomeUiEvent> = _events.asSharedFlow()

    private val searchQuery = MutableStateFlow("")
    private var searchJob: Job? = null
    // SERVICE ACTION ENUM

    enum class ServiceAction {
        START_ASSIGNED,
        START_DOWNLOADED,
        RESTART_RECENT,
        RESUME
    }

    private var pendingAction: ServiceAction? = null
    private var pendingService: HomeUiModel? = null

    fun onDownloadService(service: HomeUiModel) {
        viewModelScope.launch {
            try {
                val result = getServiceDetail(service.id)
                when (result) {
                    is Resource.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                otherServiceList = state.otherServiceList.filter { it.id != service.id }
                            )
                        }
                    }

                    is Resource.Error -> {
                        SnackbarManager.show(
                            result.message ?: "Failed to download service",
                            SnackbarType.ERROR
                        )
                    }

                    is Resource.Empty<*> -> {
                        SnackbarManager.show(
                            "Service details not found",
                            SnackbarType.ERROR
                        )
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(e.message ?: "Something went wrong", SnackbarType.ERROR)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchQuery.value = query

        if (searchJob == null) {
            observeSearch()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest {
                    fetchOtherServicesInternal(isPagination = false)
                }
        }
    }

    fun onTabSelected(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
        if (tab == 3) {
            fetchOtherServices()
        }
    }

    private fun fetchOtherServices() {
        viewModelScope.launch {
            fetchOtherServicesInternal()
        }
    }

    fun loadMoreServices() {
        viewModelScope.launch {
            fetchOtherServicesInternal(isPagination = true)
        }
    }

    private suspend fun fetchOtherServicesInternal(
        isPagination: Boolean = false,
    ) {
        val currentState = _uiState.value

        if (isPagination && (currentState.isPaginationLoading || !currentState.hasMore)) return

        if (isPagination) {
            _uiState.update { it.copy(isPaginationLoading = true) }
        } else {
            _uiState.update {
                it.copy(
                    offset = 0,
                    hasMore = true,
                    otherServiceList = emptyList(),
                    otherServiceLoading = true
                )
            }
        }

        try {
            val result = getMaskedServices(
                offset = if (isPagination) currentState.offset else 0,
                limit = currentState.limit,
                search = searchQuery.value
            )

            when (result) {

                is Resource.Success -> {
                    val newList = result.data ?: emptyList()

                    _uiState.update {
                        it.copy(
                            otherServiceList = if (isPagination)
                                it.otherServiceList + newList.map { service -> service.toHomeUiModel() }
                            else
                                newList.map { service -> service.toHomeUiModel() },

                            offset = if (isPagination)
                                it.offset + it.limit
                            else
                                it.limit,

                            hasMore = newList.size == it.limit
                        )
                    }
                }

                is Resource.Error -> {
                    SnackbarManager.show(
                        result.message ?: "Failed to fetch services",
                        SnackbarType.ERROR
                    )
                }

                is Resource.Empty -> {
                    _uiState.update {
                        if (isPagination) {
                            it.copy(
                                hasMore = false
                            )
                        } else {
                            it.copy(
                                otherServiceList = emptyList(),
                                hasMore = false
                            )
                        }
                    }
                }

                else -> {}
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            SnackbarManager.show(
                e.message ?: "Something went wrong",
                SnackbarType.ERROR
            )
        } finally {
            if (isPagination) {
                _uiState.update { it.copy(isPaginationLoading = false) }
            } else {
                _uiState.update { it.copy(otherServiceLoading = false) }
            }
        }

    }


    // MAIN UNIFIED ENTRY POINT

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

    // PERMISSION RESULT CALLBACK

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

    // CORE FLOW CONTINUATION

    private fun proceedAfterBluetoothReady() {
        viewModelScope.launch {
            if (printerManager.checkConnection()) {
                completeServiceFlow()
            } else {
                emitOpenPrinterPicker()
            }
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

    // PRINTER SELECTION

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
                    "Unable to connect to printer, make sure your printer is powered on",
                    SnackbarType.ERROR
                )
            }
        }
    }

    // SERVICE CONFIRMATION

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

    // EVENTS

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

    // OTHER UI STATE HANDLERS


    fun onFinishDuty(service: HomeUiModel) {
        printerManager.closeConnection()

        _uiState.update { state ->
            state.copy(
                recentServiceList = state.recentServiceList + service,
                ongoingService = null
            )
        }
    }

    // HELPERS

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
