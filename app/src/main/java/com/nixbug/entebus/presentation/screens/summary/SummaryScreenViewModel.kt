package com.nixbug.entebus.presentation.screens.summary

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.domain.model.local.SyncEvent
import com.nixbug.entebus.domain.usecase.queue.HasPendingSyncItemsUseCase
import com.nixbug.entebus.domain.usecase.queue.ObserveSyncEventsUseCase
import com.nixbug.entebus.domain.usecase.queue.TriggerSyncUseCase
import com.nixbug.entebus.domain.usecase.collection.DeleteOldCollectionsUseCase
import com.nixbug.entebus.domain.usecase.collection.GetServiceCollectionsUseCase
import com.nixbug.entebus.domain.usecase.service.HasServiceWithStartedStatusUseCase
import com.nixbug.entebus.domain.usecase.user.GetUserUseCase
import com.nixbug.entebus.utils.BluetoothPermissionHelper
import com.nixbug.entebus.utils.printer.PrinterManager
import com.nixbug.entebus.utils.printer.TicketBuilder
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import com.nixbug.entebus.utils.titleCaseFirstChar
import com.nixbug.entebus.utils.utcToIstConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class SummaryScreenViewModel @Inject constructor(
    private val printerManager: PrinterManager,
    private val hasPendingSyncItemsUseCase: HasPendingSyncItemsUseCase,
    private val getServiceCollectionsUseCase: GetServiceCollectionsUseCase,
    private val hasServiceWithStatusStartedUseCase: HasServiceWithStartedStatusUseCase,
    private val triggerSyncUseCase: TriggerSyncUseCase,
    private val observeSyncEventsUseCase: ObserveSyncEventsUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val deleteOldCollectionsUseCase: DeleteOldCollectionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SummaryUiEvent>()
    val events: SharedFlow<SummaryUiEvent> = _events.asSharedFlow()

    private var pendingPrintData: PrintSummaryData? = null

    fun onScreenVisible() {
        viewModelScope.launch {
            try {
                deleteOldCollectionsUseCase()
            } catch (e: Exception) {
                SnackbarManager.show(
                    "Failed to clean old collections",
                    SnackbarType.ERROR
                )
            }
        }
        loadCollections()
        observeUser()
    }

    private fun observeUser() {
        viewModelScope.launch {
            try {
                val user = getUserUseCase().first()
                _uiState.update { it.copy(companyName = user.companyName) }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(
                    "Error loading user data: ${e.message}",
                    SnackbarType.ERROR
                )
            }
        }
    }

    private fun loadCollections() {

        viewModelScope.launch {

            try {

                _uiState.update {
                    it.copy(isLoading = true)
                }

                val collections = getServiceCollectionsUseCase()

                val summary = collections.map { collection ->

                    SummaryUiModel(
                        serviceId = collection.serviceId,
                        serviceName = collection.serviceName,
                        startedOn = collection.startedAt,
                        finishedOn = collection.finishOn,
                        regNo = collection.registrationNumber,
                        operatorId = collection.operatorId,
                        operatorName = collection.operatorName,
                        collection = collection.collection
                    )
                }

                val selectedIds = getTodayServiceIds(summary)

                _uiState.update {
                    it.copy(
                        summary = summary,
                        selectedServiceIds = selectedIds,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {

                if (e is CancellationException) throw e

                _uiState.update {
                    it.copy(isLoading = false)
                }

                SnackbarManager.show(
                    "Error loading collections: ${e.message}",
                    SnackbarType.ERROR
                )
            }
        }
    }

    private fun getTodayServiceIds(
        summary: List<SummaryUiModel>
    ): Set<Int> {

        val today = Instant.now().toString()

        return summary
            .groupBy { it.serviceId }
            .filter { (_, group) ->

                val first = group.first()

                val (serviceDate, _) =
                    utcToIstConverter(first.startedOn)
                        ?: return@filter false

                val (currentDate, _) =
                    utcToIstConverter(today)
                        ?: return@filter false

                serviceDate == currentDate
            }
            .keys
    }

    fun toggleSelection(serviceId: Int) {
        _uiState.update { currentState ->

            val updatedSelection = currentState.selectedServiceIds.toMutableSet()

            if (updatedSelection.contains(serviceId)) {
                updatedSelection.remove(serviceId)
            } else {
                updatedSelection.add(serviceId)
            }

            currentState.copy(
                selectedServiceIds = updatedSelection
            )
        }
    }

    fun generateReceipt(
        data: PrintSummaryData,
        hasPermission: Boolean,
        isBluetoothEnabled: Boolean
    ) {
        pendingPrintData = data

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

    // EVENTS
    private fun emitPermissionRequest() {
        viewModelScope.launch {
            _events.emit(SummaryUiEvent.RequestBluetoothPermission)
        }
    }

    private fun emitEnableBluetoothRequest() {
        viewModelScope.launch {
            _events.emit(SummaryUiEvent.RequestEnableBluetooth)
        }
    }

    private fun emitOpenPrinterPicker() {
        viewModelScope.launch {
            _events.emit(SummaryUiEvent.OpenPrinterPicker)
        }
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
                printCollection()
            } else {
                emitOpenPrinterPicker()
            }
        }
    }

    fun showSummaryPreview() {
        viewModelScope.launch {
            val hasActiveService = hasServiceWithStatusStartedUseCase()
            if (hasActiveService) {
                SnackbarManager.show(
                    "You have an active service. Please end it to preview summary.",
                    SnackbarType.ERROR
                )
                return@launch
            }
            val hasPending = hasPendingSyncItemsUseCase()
            if (hasPending) {
                SnackbarManager.show(
                    message = "Unsynced data found in storage. Please sync to continue.",
                    type = SnackbarType.ERROR,
                    actionLabel = "Sync",
                    onAction = { viewModelScope.launch { syncNow() } }
                )
                return@launch
            }
            _uiState.update {
                it.copy(
                    showSummaryPreviewDialog = true
                )
            }
        }

    }

    fun syncNow() {
        viewModelScope.launch {

            val eventDeferred = async {
                observeSyncEventsUseCase()
                    .first {
                        it is SyncEvent.Success || it is SyncEvent.Failed
                    }
            }

            triggerSyncUseCase()

            when (val event = eventDeferred.await()) {

                is SyncEvent.Failed -> {
                    SnackbarManager.show(
                        event.message,
                        SnackbarType.ERROR
                    )
                }

                is SyncEvent.Success -> {
                    SnackbarManager.show(
                        "Sync completed",
                        SnackbarType.SUCCESS
                    )
                }

                else -> Unit
            }
        }
    }

    fun hideSummaryPreview() {
        _uiState.update {
            it.copy(
                showSummaryPreviewDialog = false
            )
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

    fun onPrinterSelected(address: String) {
        _uiState.update { it.copy(isConnectingPrinter = true) }

        viewModelScope.launch {
            val success = printerManager.connectToPrinter(address)
            if (success) {
                _uiState.update { it.copy(isConnectingPrinter = false) }
                _uiState.update { it.copy(showPrinterPicker = false) }
                if (pendingPrintData != null) {
                    printCollection()
                }
            } else {
                SnackbarManager.show(
                    "Unable to connect to printer, make sure your printer is powered on",
                    SnackbarType.ERROR
                )
                _uiState.update { it.copy(isConnectingPrinter = false) }
                _uiState.update { it.copy(showPrinterPicker = false) }
            }
        }
    }

    fun hidePrinterPicker() {
        _uiState.update {
            it.copy(showPrinterPicker = false)
        }
    }

    fun printCollection() {
        hideSummaryPreview()
        viewModelScope.launch {
            // 1. Check connection on IO thread
            if (!printerManager.checkConnection()) {
                SnackbarManager.show(
                    message = "Printer not connected. Please connect in settings.",
                    type = SnackbarType.ERROR
                )
                return@launch
            }

            val data = pendingPrintData ?: return@launch

            try {
                // 2. Build ticket on Default dispatcher (CPU intensive)
                val receiptBytes = withContext(Dispatchers.Default) {
                    val builder = TicketBuilder()

                    builder.addCommand(PrinterManager.Commands.ALIGN_CENTER)
                    builder.text("-----${data.printedAt}-----")
                    builder.addCommand(PrinterManager.Commands.BOLD_ON)
                    builder.addCommand(PrinterManager.Commands.SIZE_DOUBLE_HEIGHT)
                    builder.text(uiState.value.companyName?.titleCaseFirstChar() ?: "")
                    builder.addCommand(PrinterManager.Commands.SIZE_NORMAL)
                    builder.addCommand(PrinterManager.Commands.BOLD_OFF)
                    builder.text("--------")

                    data.services.forEachIndexed { index, service ->
                        builder.addCommand(PrinterManager.Commands.BOLD_ON)
                        builder.addCommand(PrinterManager.Commands.ALIGN_LEFT)
                        builder.text("${index + 1}.${service.serviceName}")
                        builder.addCommand(PrinterManager.Commands.BOLD_OFF)

                        builder.addCommand(PrinterManager.Commands.LINE_SPACE_TIGHT)
                        builder.text(service.regNo)
                        builder.text(service.dateTimeRange)
                        builder.addCommand(PrinterManager.Commands.LINE_SPACE_DEFAULT)

                        service.operators.forEach {
                            builder.leftRightRow(it.first, "${it.second} Rs")
                        }

                        builder.addCommand(PrinterManager.Commands.BOLD_ON)
                        builder.addCommand(PrinterManager.Commands.ALIGN_CENTER)
                        builder.text("Sub Total: ${service.serviceTotal} Rs")
                        builder.addCommand(PrinterManager.Commands.BOLD_OFF)
                        builder.divider()
                    }

                    builder.addCommand(PrinterManager.Commands.SIZE_DOUBLE_HEIGHT)
                    builder.addCommand(PrinterManager.Commands.ALIGN_CENTER)
                    builder.text("GRAND TOTAL : ${data.grandTotal} Rs")
                    builder.addCommand(PrinterManager.Commands.SIZE_NORMAL)
                    builder.text("-----------Entebus-----------")

                    builder.build() // Return the final ByteArray
                }

                // 3. Send data to printer and handle result
                val result = printerManager.sendData(receiptBytes)

                if (result.isSuccess) {
                    SnackbarManager.show("Collection report printed!", SnackbarType.SUCCESS)
                    pendingPrintData = null
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown printer error"
                    SnackbarManager.show("Printing failed: $error", SnackbarType.ERROR)
                }

            } catch (e: Exception) {
                SnackbarManager.show("Printing error: ${e.message}", SnackbarType.ERROR)
            }
        }
    }
}
