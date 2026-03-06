package com.nixbug.entebus.presentation.screens.ticket

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.utils.printer.PrinterManager
import com.nixbug.entebus.utils.printer.TicketBuilder
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val MAX_PASSENGER_COUNT = 99
private const val METERS_PER_KILOMETER = 1000f
private val SortedSampleLandmarks = SampleLandmarks.sortedBy { it.distanceFromStart }

@HiltViewModel
class TicketViewModel @Inject constructor(private val printerManager: PrinterManager) :
    ViewModel() {

    private val _uiState = MutableStateFlow(TicketUiState(landmarks = SortedSampleLandmarks))
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TicketUiEvent>()
    val events: SharedFlow<TicketUiEvent> = _events.asSharedFlow()

    private fun emitNavigateBack() {
        viewModelScope.launch {
            _events.emit(TicketUiEvent.EmitNavigateBack)
        }
    }

    private fun calculateDistanceKm(start: Landmark?, end: Landmark?): Float {
        return if (start != null && end != null) {
            kotlin.math.abs(end.distanceFromStart - start.distanceFromStart) / METERS_PER_KILOMETER
        } else {
            0f
        }
    }

    fun toggleLandmarkSelection(landmark: Landmark, index: Int) {
        // Retap start → deselect
        if (_uiState.value.startingLandmark?.id == landmark.id) {
            _uiState.update { state ->
                state.copy(
                    startingLandmark = null,
                    startingLandmarkIndex = null,
                    totalDistanceKm = calculateDistanceKm(null, state.endingLandmark)
                )
            }
            return
        }

        // Retap end → deselect
        if (_uiState.value.endingLandmark?.id == landmark.id) {
            _uiState.update { state ->
                state.copy(
                    endingLandmark = null,
                    endingLandmarkIndex = null,
                    totalDistanceKm = calculateDistanceKm(state.startingLandmark, null)
                )
            }
            return
        }

        // Invalid starting (below ending)
        if (_uiState.value.startingLandmark == null && index > (_uiState.value.endingLandmarkIndex
                ?: Int.MAX_VALUE)
        ) {
            SnackbarManager.show(
                message = "Starting point cannot be after ending point",
                type = SnackbarType.ERROR,
            )
            return
        }

        // Selecting starting landmark
        if (_uiState.value.startingLandmark == null) {
            _uiState.update { state ->
                val newStart = landmark
                state.copy(
                    startingLandmark = newStart,
                    startingLandmarkIndex = index,
                    totalDistanceKm = calculateDistanceKm(newStart, state.endingLandmark)
                )
            }
            return
        }

        // Invalid ending (above starting)
        if (index < (_uiState.value.startingLandmarkIndex ?: 0)) {
            SnackbarManager.show(
                message = "Ending point must come after starting point",
                type = SnackbarType.ERROR,
            )
            return
        }

        // Selecting Ending landmark
        _uiState.update { state ->
            val newEnd = landmark
            state.copy(
                endingLandmark = newEnd,
                endingLandmarkIndex = index,
                totalDistanceKm = calculateDistanceKm(state.startingLandmark, newEnd)
            )
        }
    }

    fun updatePassengerCount(id: Int, count: Int) {
        when {
            count > MAX_PASSENGER_COUNT -> {
                SnackbarManager.show(
                    message = "Maximum $MAX_PASSENGER_COUNT passengers allowed",
                    type = SnackbarType.ERROR,
                )
                _uiState.update { state ->
                    state.copy(
                        passengers = state.passengers.map {
                            if (it.id == id) it.copy(passengerCount = MAX_PASSENGER_COUNT) else it
                        }
                    )
                }
            }

            else -> {
                _uiState.update { state ->
                    state.copy(
                        passengers = state.passengers.map {
                            if (it.id == id) it.copy(passengerCount = count) else it
                        }
                    )
                }
            }
        }
    }

    fun formatText(input: String): String {
        return if (input.length > 14) {
            input.take(12) + ".."
        } else {
            input
        }
    }

    fun printTicket() {
        viewModelScope.launch {
            if (!printerManager.checkConnection()) {
                SnackbarManager.show(
                    message = "Printer connection failed. Please reconnect the printer and resume the service.",
                    type = SnackbarType.ERROR,
                    duration = SnackbarDuration.Long
                )
                emitNavigateBack()
                return@launch
            }
            val state = _uiState.value

            val startingLandmark = state.startingLandmark
            val endingLandmark = state.endingLandmark
            val passengers = state.passengers
            val totalDistanceKm = state.totalDistanceKm
            if (startingLandmark == null || endingLandmark == null) {
                SnackbarManager.show(
                    message = "Please select starting and ending points",
                    type = SnackbarType.ERROR
                )
                return@launch
            }

            val selectedPassengers = passengers.filter { it.passengerCount > 0 }

            if (selectedPassengers.isEmpty()) {
                SnackbarManager.show(
                    message = "Please select at least one passenger",
                    type = SnackbarType.ERROR
                )
                return@launch
            }
            val calendar = java.util.Calendar.getInstance()
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault())
            val timeFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())

            val dateStr = dateFormat.format(calendar.time)
            val timeStr = timeFormat.format(calendar.time)
            val dateTimeCombined = "$dateStr $timeStr"

            val totalFare = totalDistanceKm * 10.0

            try {
                val ticketData = withContext(Dispatchers.Default) {
                    TicketBuilder()
                        .addCommand(PrinterManager.Commands.ALIGN_CENTER)
                        .addCommand(PrinterManager.Commands.BOLD_ON)
                        .text("RKV")
                        .addCommand(PrinterManager.Commands.BOLD_OFF)

                        .addCommand(PrinterManager.Commands.SIZE_NORMAL)
                        .addCommand(PrinterManager.Commands.FONT_B)
                        .threeColumnRow("000123", dateTimeCombined, "ORDINARY")

                        .addCommand(PrinterManager.Commands.FONT_A)
                        .addCommand(PrinterManager.Commands.BOLD_ON)
                        .routeRow(
                            formatText(startingLandmark.name),
                            "To",
                            formatText(endingLandmark.name)
                        )
                        .addCommand(PrinterManager.Commands.BOLD_OFF)

                        .addCommand(PrinterManager.Commands.FONT_B)
                        .addCommand(PrinterManager.Commands.LINE_SPACE_TIGHT)
                        .apply {
                            selectedPassengers.forEach { passenger ->
                                val lineFare = passenger.passengerCount * 10.0
                                val line = "%-10s %2d x 10.00 = %6.2f".format(
                                    passenger.name.take(10),
                                    passenger.passengerCount,
                                    lineFare
                                )
                                text(line)
                            }
                        }

                        .addCommand(PrinterManager.Commands.SIZE_BIG)
                        .text("RS .  %.2f".format(totalFare))
                        .addCommand(PrinterManager.Commands.SIZE_NORMAL)

                        .divider()

                        .addCommand(PrinterManager.Commands.FONT_A)
                        .addCommand(PrinterManager.Commands.ALIGN_CENTER)
                        .text("Not Transferable")
                        .text("Nixbug softwares")
                        .addCommand(PrinterManager.Commands.LINE_SPACE_DEFAULT)
                        .addCommand(PrinterManager.Commands.BOLD_OFF)
                        .build()
                }
                val result = printerManager.sendData(ticketData)

                if (result.isSuccess) {
                    SnackbarManager.show("Ticket printed successfully!", SnackbarType.SUCCESS)
                } else {
                    throw result.exceptionOrNull() ?: Exception("Unknown error")
                }
            } catch (e: Exception) {
                SnackbarManager.show(
                    "Printer connection failed. Please reconnect the printer and resume the service.",
                    SnackbarType.ERROR,
                    SnackbarDuration.Long
                )
                emitNavigateBack()
            }
        }
    }
}
