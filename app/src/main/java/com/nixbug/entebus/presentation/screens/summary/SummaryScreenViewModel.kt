package com.nixbug.entebus.presentation.screens.summary

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.utils.BluetoothPermissionHelper
import com.nixbug.entebus.utils.printer.PrinterManager
import com.nixbug.entebus.utils.printer.TicketBuilder
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import com.nixbug.entebus.utils.utcToIstConverter
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
import javax.inject.Inject

@HiltViewModel
class SummaryScreenViewModel @Inject constructor(private val printerManager: PrinterManager) :
    ViewModel() {

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SummaryUiEvent>()
    val events: SharedFlow<SummaryUiEvent> = _events.asSharedFlow()


    private var pendingPrintData: PrintSummaryData? = null

    init {
        autoSelectTodayServices()
    }

    private fun autoSelectTodayServices() {
        val today = Instant.now().toString()

        val todayServiceIds = _uiState.value.summary
            .groupBy { it.serviceId }
            .filter { (_, group) ->
                val first = group.first()
                val (date, _) = utcToIstConverter(first.startedOn)
                    ?: return@filter false
                val (currentDate, _) = utcToIstConverter(today)
                    ?: return@filter false

                currentDate == date
            }
            .keys

        _uiState.update { currentState ->
            currentState.copy(
                selectedServiceIds = todayServiceIds
            )
        }
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


    // ----------------------------------------------------------------
    // EVENTS
    // ----------------------------------------------------------------

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

    // ----------------------------------------------------------------

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
            printCollection()
        } else {
            emitOpenPrinterPicker()
        }
    }

    fun showSummaryPreview() {
        _uiState.update {
            it.copy(
                showSummaryPreviewDialog = true
            )
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
                    "Unable to connect to printer,make sure your printer is powered on",
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

        if (!printerManager.isConnected()) {
            SnackbarManager.show(
                message = "Printer not connected. Please connect in settings.",
                type = SnackbarType.ERROR
            )
            return
        }

        val data = pendingPrintData ?: return

        try {
            val builder = TicketBuilder()

            builder.addCommand(PrinterManager.Commands.ALIGN_CENTER)
            builder.text("------${data.printedAt}------")
            builder.addCommand(PrinterManager.Commands.BOLD_ON)
            builder.addCommand(PrinterManager.Commands.SIZE_MEDIUM_TALL)
            builder.text("Krishna")
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
                builder.addCommand(PrinterManager.Commands.FONT_B)
                builder.text(service.dateTimeRange)
                builder.addCommand(PrinterManager.Commands.LINE_SPACE_DEFAULT)
                builder.addCommand(PrinterManager.Commands.FONT_A)
                service.operators.forEach {
                    builder.rowLeftRight(it.first, "${it.second} Rs")
                }

                builder.addCommand(PrinterManager.Commands.BOLD_ON)
                builder.addCommand(PrinterManager.Commands.ALIGN_CENTER)
                builder.text("Sub Total: ${service.serviceTotal} Rs")
                builder.addCommand(PrinterManager.Commands.BOLD_OFF)

                builder.divider()
            }

            builder.addCommand(PrinterManager.Commands.FONT_A)
            builder.addCommand(PrinterManager.Commands.SIZE_MEDIUM_TALL)
            builder.addCommand(PrinterManager.Commands.ALIGN_CENTER)
            builder.text("GRAND TOTAL : ${data.grandTotal} Rs")
            builder.addCommand(PrinterManager.Commands.FONT_A)
            builder.addCommand(PrinterManager.Commands.SIZE_NORMAL)

            builder.text("--Nixbug Softwares OPC Pvt Ltd--")

            printerManager.sendData(builder.build())
            pendingPrintData = null
        } catch (e: Exception) {
            SnackbarManager.show("Printing failed: ${e.message}", SnackbarType.ERROR)
        }
    }
}
