package com.nixbug.entebus.presentation.screens.summary

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.utils.PrinterManager
import com.nixbug.entebus.utils.TicketBuilder
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

    private fun emitOpenPrinterPicker() {
        viewModelScope.launch {
            _events.emit(SummaryUiEvent.OpenPrinterPicker)
        }
    }

    fun onPrinterSelected(address: String) {
        _uiState.update { it.copy(showPrinterPicker = false) }

        viewModelScope.launch {
            val success = printerManager.connectToPrinter(address)
            if (success) {
                pendingPrintData?.let {
                    printCollection(it)
                    pendingPrintData = null   // clear after use
                }
            } else {
                SnackbarManager.show(
                    "Failed to connect to printer",
                    SnackbarType.ERROR
                )
            }
        }
    }

    fun hidePrinterPicker() {
        _uiState.update {
            it.copy(showPrinterPicker = false)
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

    fun resumePendingPrint() {
        pendingPrintData?.let {
            generateReceipt(it)
        }
    }

    fun generateReceipt(data: PrintSummaryData) {
        if (printerManager.isConnected()) {
            printCollection(data)
        } else {
            pendingPrintData = data
            emitOpenPrinterPicker()
        }
    }

    fun printCollection(data: PrintSummaryData) {

        if (!printerManager.isConnected()) {
            SnackbarManager.show(
                message = "Printer not connected. Please connect in settings.",
                type = SnackbarType.ERROR
            )
            return
        }

        try {
            val builder = TicketBuilder()

            builder.addCommand(PrinterManager.Commands.ALIGN_CENTER)
            builder.text("------${data.printedAt}------")
            builder.addCommand(PrinterManager.Commands.BOLD_ON)
            builder.addCommand(PrinterManager.Commands.SIZE_MEDIUM_TALL)
            builder.text("RKV ENTERPRISES")
            builder.addCommand(PrinterManager.Commands.SIZE_NORMAL)
            builder.addCommand(PrinterManager.Commands.BOLD_OFF)
            builder.text("--------")

            data.services.forEachIndexed { index, service ->

                builder.addCommand(PrinterManager.Commands.BOLD_ON)
                builder.addCommand(PrinterManager.Commands.ALIGN_LEFT)
                builder.text("${index +1}.${service.serviceName}")
                builder.addCommand(PrinterManager.Commands.BOLD_OFF)

                builder.addCommand(PrinterManager.Commands.LINE_SPACE_TIGHT)
                builder.text(service.regNo)
                builder.addCommand(PrinterManager.Commands.FONT_B)
                builder.text(service.dateTimeRange)
                builder.addCommand(PrinterManager.Commands.LINE_SPACE_DEFAULT)
                builder.addCommand(PrinterManager.Commands.FONT_A)
                service.operators.forEach {
                    builder.rowLeftRight(it.first , "${it.second} Rs")
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

        } catch (e: Exception) {
            SnackbarManager.show("Printing failed: ${e.message}", SnackbarType.ERROR)
        }
    }

}
