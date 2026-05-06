package com.nixbug.entebus.presentation.screens.home

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.domain.model.ServicePatchStatus
import com.nixbug.entebus.domain.model.ServiceUpdate
import com.nixbug.entebus.domain.model.local.CollectionStatus
import com.nixbug.entebus.domain.model.local.LocalServiceStatus
import com.nixbug.entebus.domain.model.local.OperatorInfo
import com.nixbug.entebus.domain.usecase.queue.EnqueueServicePatchUseCase
import com.nixbug.entebus.domain.usecase.service.CleanOldServicesUseCase
import com.nixbug.entebus.domain.usecase.service.DeleteAssignedServicesUseCase
import com.nixbug.entebus.domain.usecase.assignedservice.GetAssignedServiceUseCase
import com.nixbug.entebus.domain.usecase.collection.CreateServiceCollectionUseCase
import com.nixbug.entebus.domain.usecase.collection.HasServiceCollectionUseCase
import com.nixbug.entebus.domain.usecase.collection.UpdateServiceCollectionStatusUseCase
import com.nixbug.entebus.domain.usecase.service.ClearAssignedServicesUseCase
import com.nixbug.entebus.domain.usecase.service.GetLocalServicesByStatusUseCase
import com.nixbug.entebus.domain.usecase.service.GetServiceDetailUseCase
import com.nixbug.entebus.domain.usecase.service.GetServiceDetailsForHomeUseCase
import com.nixbug.entebus.domain.usecase.service.GetServiceUseCase
import com.nixbug.entebus.domain.usecase.service.UpdateLocalStatusUseCase
import com.nixbug.entebus.domain.usecase.token.GetTokenUseCase
import com.nixbug.entebus.domain.usecase.user.GetUserUseCase
import com.nixbug.entebus.utils.BluetoothPermissionHelper
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.utils.printer.PrinterManager
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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
    private val getServiceDetail: GetServiceDetailUseCase,
    private val getLocalServices: GetLocalServicesByStatusUseCase,
    private val getAssignedServices: GetAssignedServiceUseCase,
    private val updateLocalStatusUseCase: UpdateLocalStatusUseCase,
    private val cleanOldServicesUseCase: CleanOldServicesUseCase,
    private val getServiceDetailsForHomeUseCase: GetServiceDetailsForHomeUseCase,
    private val deleteAssignedServicesUseCase: DeleteAssignedServicesUseCase,
    private val clearAssignedServicesUseCase: ClearAssignedServicesUseCase,
    private val enqueueServicePatchUseCase: EnqueueServicePatchUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getTokenUseCase: GetTokenUseCase,
    private val createServiceCollectionUseCase: CreateServiceCollectionUseCase,
    private val updateServiceCollectionStatusUseCase: UpdateServiceCollectionStatusUseCase,
    private val hasServiceCollectionUseCase: HasServiceCollectionUseCase
) : ViewModel() {

    // STATE
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeUiEvent>()
    val events: SharedFlow<HomeUiEvent> = _events.asSharedFlow()

    private val searchQuery = MutableStateFlow("")
    private var searchJob: Job? = null

    enum class ServiceAction {
        START_ASSIGNED,
        START_DOWNLOADED,
        RESTART_RECENT,
        RESUME
    }

    private var pendingAction: ServiceAction? = null
    private var pendingService: HomeUiModel? = null
    private val fetchedAssignedServiceIds = mutableSetOf<Int>()
    private val finishServiceRequest = ServiceUpdate(status = ServicePatchStatus.FINISHED)
    private val restartServiceRequest = ServiceUpdate(status = ServicePatchStatus.RESTARTED)

    fun loadMoreAssignedServices() {
        fetchAssignedService(isPagination = true)
    }

    private fun clearAssignedServices() {
        viewModelScope.launch {
            try {
                clearAssignedServicesUseCase()
            } catch (e: Exception) {
                if (e is CancellationException) throw e

                SnackbarManager.show(
                    e.message ?: "Failed to clear assigned services",
                    SnackbarType.ERROR
                )
            }
        }
    }

    private fun deleteMissingAssignedServices(ids: List<Int>) {
        viewModelScope.launch {
            try {
                deleteAssignedServicesUseCase(ids)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(
                    e.message ?: "Failed to sync deleted services",
                    SnackbarType.ERROR
                )
            }
        }
    }

    private fun fetchAssignedService(isPagination: Boolean = false) {
        viewModelScope.launch {

            val currentState = _uiState.value

            if (isPagination && (currentState.isAssignedPaginationLoading || !currentState.assignedHasMore)) {
                return@launch
            }

            if (isPagination) {
                _uiState.update { it.copy(isAssignedPaginationLoading = true) }
            } else {
                fetchedAssignedServiceIds.clear()
                _uiState.update {
                    it.copy(
                        assignedOffset = 0,
                        assignedHasMore = true,
                        serviceLoading = true
                    )
                }
            }

            try {
                val result = getAssignedServices(
                    offset = if (isPagination) currentState.assignedOffset else 0,
                    limit = currentState.assignedLimit
                )

                when (result) {

                    is Resource.Success -> {
                        val services = result.data ?: emptyList()
                        if (services.isEmpty()) {
                            if (isPagination) {
                                _uiState.update { it.copy(assignedHasMore = false) }
                            } else {
                                clearAssignedServices()
                                _uiState.update {
                                    it.copy(
                                        assignedServiceList = emptyList(),
                                        assignedHasMore = false
                                    )
                                }
                            }
                            return@launch
                        }
                        val ids = services.map { it.serviceId }
                        fetchedAssignedServiceIds.addAll(ids)

                        val startedServiceIds = getLocalServices(LocalServiceStatus.STARTED)
                            .map { it.id }
                            .toSet()

                        val assignedOnlyServices = services.filter { service ->
                            service.serviceId !in startedServiceIds
                        }

                        val detailedServices = coroutineScope {
                            assignedOnlyServices.map { service ->
                                async {
                                    when (
                                        val detailResult = getServiceDetailsForHomeUseCase(
                                            service.serviceId,
                                            LocalServiceStatus.ASSIGNED
                                        )
                                    ) {
                                        is Resource.Success -> detailResult.data?.toHomeUiModel()
                                        else -> null
                                    }
                                }
                            }.awaitAll().filterNotNull()
                        }

                        val isLastPage = services.size < currentState.assignedLimit
                        _uiState.update {
                            it.copy(
                                assignedServiceList =
                                    if (isPagination)
                                        it.assignedServiceList + detailedServices
                                    else
                                        detailedServices,

                                assignedOffset =
                                    if (isPagination)
                                        it.assignedOffset + it.assignedLimit
                                    else
                                        it.assignedLimit,

                                assignedHasMore = services.size == it.assignedLimit
                            )
                        }

                        if (isLastPage && fetchedAssignedServiceIds.isNotEmpty()) {
                            deleteMissingAssignedServices(fetchedAssignedServiceIds.toList())
                        }
                    }

                    is Resource.Error -> {
                        if (isPagination) {
                            _uiState.update { state ->
                                state.copy(
                                    assignedHasMore = false
                                )
                            }
                        } else {
                            val services = getLocalServices(LocalServiceStatus.ASSIGNED)
                            _uiState.update { state ->
                                state.copy(
                                    assignedHasMore = false,
                                    assignedServiceList = services.map { service -> service.toHomeUiModel() }
                                )
                            }
                        }
                    }

                    is Resource.Empty -> {
                        _uiState.update {
                            if (isPagination) {
                                it.copy(assignedHasMore = false)
                            } else {
                                it.copy(
                                    assignedServiceList = emptyList(),
                                    assignedHasMore = false
                                )
                            }
                        }
                    }

                    else -> {}
                }

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                val services = getLocalServices(LocalServiceStatus.ASSIGNED)
                _uiState.update { state ->
                    state.copy(
                        assignedHasMore = false,
                        assignedServiceList = services.map { service -> service.toHomeUiModel() }
                    )
                }
            } finally {
                if (isPagination) {
                    _uiState.update { it.copy(isAssignedPaginationLoading = false) }
                } else {
                    _uiState.update { it.copy(serviceLoading = false) }
                }
            }
        }
    }

    init {
        _uiState.update { it.copy(serviceLoading = true) }
        viewModelScope.launch {
            try {
                cleanOldServicesUseCase()
                val currentServiceList = getLocalServices(LocalServiceStatus.STARTED)
                val currentService = currentServiceList.firstOrNull()
                _uiState.update { state -> state.copy(ongoingService = currentService?.toHomeUiModel()) }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(
                    e.message ?: "Failed to load ongoing service",
                    SnackbarType.ERROR
                )
            }
        }
        fetchAssignedService()
    }

    fun onTabSelected(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
        if (tab == 0) {
            fetchAssignedService()
        }
        if (tab == 1) {
            fetchDownloadedServices()
        }
        if (tab == 2) {
            fetchRecentServices()
        }
        if (tab == 3) {
            fetchOtherServices()
        }
    }


    fun onDownloadService(service: HomeUiModel) {
        _uiState.update { it.copy(downloadingServiceId = service.id) }
        viewModelScope.launch {
            try {
                val result = getServiceDetail(service.id, LocalServiceStatus.DOWNLOADED)
                when (result) {
                    is Resource.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                otherServiceList = state.otherServiceList.filter { it.id != service.id }
                            )
                        }
                        if (_uiState.value.otherServiceList.isEmpty() && _uiState.value.hasMore) {
                            loadMoreServices()
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
            } finally {
                _uiState.update { it.copy(downloadingServiceId = null) }
            }
        }
    }

    private fun fetchDownloadedServices() {
        viewModelScope.launch {
            _uiState.update { it.copy(serviceLoading = true) }
            try {
                val services = getLocalServices(LocalServiceStatus.DOWNLOADED)
                _uiState.update {
                    it.copy(
                        downloadedServiceList = services.map { service ->
                            service.toHomeUiModel()
                        }
                    )
                }
            } catch (e: Exception) {
                SnackbarManager.show(
                    e.message ?: "Failed to load downloaded services",
                    SnackbarType.ERROR
                )
            } finally {
                _uiState.update { it.copy(serviceLoading = false) }
            }
        }
    }

    private fun fetchRecentServices() {
        viewModelScope.launch {
            _uiState.update { it.copy(serviceLoading = true) }
            try {
                val services = getLocalServices(LocalServiceStatus.RECENT)
                _uiState.update {
                    it.copy(
                        recentServiceList = services.map { service ->
                            service.toHomeUiModel()
                        }
                    )
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(
                    e.message ?: "Failed to load recent services",
                    SnackbarType.ERROR
                )
            } finally {
                _uiState.update { it.copy(serviceLoading = false) }
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

    private suspend fun fetchOtherServicesInternal(isPagination: Boolean = false) {
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
                    serviceLoading = true
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
                _uiState.update { it.copy(serviceLoading = false) }
            }
        }

    }

    // MAIN UNIFIED ENTRY POINT

    fun cancelPendingServiceAction() {
        pendingAction = null
        pendingService = null

        _uiState.update {
            it.copy(
                startingServiceId = null
            )
        }
    }

    fun handleServiceAction(
        action: ServiceAction,
        service: HomeUiModel? = null,
        hasPermission: Boolean,
        isBluetoothEnabled: Boolean
    ) {

        if (action != ServiceAction.RESUME && checkIfServiceAlreadyRunning()) return

        pendingAction = action
        pendingService = service

        if (service != null && action != ServiceAction.RESUME) {
            _uiState.update {
                it.copy(
                    startingServiceId = service.id,
                )
            }
        }

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
        _uiState.update {
            it.copy(
                showPrinterPicker = false,
                startingServiceId = null,
            )
        }
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
                _uiState.update {
                    it.copy(
                        isConnectingPrinter = false,
                        showPrinterPicker = false,
                        startingServiceId = null,
                    )
                }
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

        viewModelScope.launch {
            try {
                if (
                    pendingAction == ServiceAction.START_DOWNLOADED ||
                    pendingAction == ServiceAction.START_ASSIGNED
                ) {
                    val user = getUserUseCase().first()
                    val token = getTokenUseCase().first()
                    createServiceCollectionUseCase(
                        serviceId = service.id,
                        serviceName = "${
                            service.startingLandmarkName.replaceFirstChar {
                                if (it.isLowerCase()) {
                                    it.titlecase()
                                } else {
                                    it.toString()
                                }
                            }
                        } -> ${
                            service.endingLandmarkName.replaceFirstChar {
                                if (it.isLowerCase()) {
                                    it.titlecase()
                                } else {
                                    it.toString()
                                }
                            }
                        }",
                        regNo = service.registrationNumber,
                        startedAt = service.startingAt,
                        finishOn = service.endingAt,
                        operators = listOf(
                            OperatorInfo(
                                id = token.operatorId ?: 0,
                                name = user.username ?: ""
                            ),
                        )
                    )
                }

                if (pendingAction == ServiceAction.RESTART_RECENT) {
                    enqueueServicePatchUseCase(
                        serviceId = service.id,
                        request = restartServiceRequest
                    )
                    updateServiceCollectionStatusUseCase(
                        serviceId = service.id,
                        status = CollectionStatus.WORKING
                    )
                }
                updateLocalStatusUseCase(service.id, LocalServiceStatus.STARTED)
                _uiState.update { state ->
                    state.copy(
                        ongoingService = service,
                        assignedServiceList = state.assignedServiceList.filter { it.id != service.id },
                        downloadedServiceList = state.downloadedServiceList.filter { it.id != service.id },
                        recentServiceList = state.recentServiceList.filter { it.id != service.id }
                    )
                }
                navigateToTicketScreen()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                SnackbarManager.show(
                    "Failed to start service. Please try again.",
                    SnackbarType.ERROR
                )
            } finally {
                _uiState.update { state -> state.copy(startingServiceId = null) }
            }
        }
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


    fun onFinishService(service: HomeUiModel) {
        printerManager.closeConnection()

        viewModelScope.launch {
            try {
                val token = getTokenUseCase().first()
                if (hasServiceCollectionUseCase(
                        serviceId = service.id,
                        operatorId = token.operatorId ?: 0
                    )
                ) {
                    enqueueServicePatchUseCase(
                        serviceId = service.id,
                        request = finishServiceRequest
                    )
                }
                updateLocalStatusUseCase(service.id, LocalServiceStatus.RECENT)
                updateServiceCollectionStatusUseCase(
                    serviceId = service.id,
                    status = CollectionStatus.FINISHED
                )
                _uiState.update { state ->
                    state.copy(
                        recentServiceList = state.recentServiceList + service,
                        ongoingService = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                SnackbarManager.show(
                    "Failed to finish service. Please try again.",
                    SnackbarType.ERROR
                )
            }
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
