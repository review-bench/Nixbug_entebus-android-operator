package com.nixbug.entebus.presentation.screens.ticket

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.domain.model.local.LocalServiceStatus
import com.nixbug.entebus.domain.usecase.fare.CalculateTicketFareUseCase
import com.nixbug.entebus.domain.usecase.service.GetDetailedServiceByStatusUseCase
import com.nixbug.entebus.presentation.screens.ticket.uimapper.toUiModel
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
import kotlin.coroutines.cancellation.CancellationException

private const val MAX_PASSENGER_COUNT = 99
private const val METERS_PER_KILOMETER = 1000f

@HiltViewModel
class TicketViewModel @Inject constructor(
    private val printerManager: PrinterManager,
    private val getDetailedServiceByStatusUseCase: GetDetailedServiceByStatusUseCase,
    private val calculateTicketFareUseCase: CalculateTicketFareUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TicketUiState())
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TicketUiEvent>()
    val events: SharedFlow<TicketUiEvent> = _events.asSharedFlow()


    init {
        loadStartedService()
    }

    private fun loadStartedService() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            try {

                val services = getDetailedServiceByStatusUseCase(LocalServiceStatus.STARTED)
                val currentService = services.firstOrNull()

                if (currentService == null) {
                    SnackbarManager.show(
                        message = "No started service found",
                        type = SnackbarType.ERROR
                    )
                    return@launch
                }

                _uiState.update { state ->
                    state.copy(
                        landmarks = currentService.landmarks
                            .sortedBy { it.distance }
                            .map { it.toUiModel() },

                        passengers = currentService.fare.ticketTypes
                            .map { it.toUiModel() },
                        fareFunctionString = currentService.fare.function
                    )
                }

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(
                    message = "Failed to load service data",
                    type = SnackbarType.ERROR
                )
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    private fun recalculateFare() {

        val state = _uiState.value
        val start = state.startingLandmark
        val end = state.endingLandmark

        if (start == null || end == null
        ) {
            _uiState.update {
                it.copy(
                    totalFare = 0.0
                )
            }
            return
        }

        val fare = calculateTicketFareUseCase(
            fareFunction = state.fareFunctionString,
            passengers = state.passengers,
            distanceInMeters = state.totalDistanceKm * 1000.0
        )

        _uiState.update {
            it.copy(
                totalFare = fare
            )
        }
    }

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

    fun toggleLandmarkSelection(
        landmark: Landmark,
        index: Int
    ) {

        val currentState = _uiState.value

        // Retap start → deselect
        if (currentState.startingLandmark?.id == landmark.id) {

            _uiState.update { state ->
                state.copy(
                    startingLandmark = null,
                    startingLandmarkIndex = null,
                    totalDistanceKm = calculateDistanceKm(
                        start = null,
                        end = state.endingLandmark
                    )
                )
            }

            recalculateFare()
            return
        }

        // Retap end → deselect
        if (currentState.endingLandmark?.id == landmark.id) {

            _uiState.update { state ->
                state.copy(
                    endingLandmark = null,
                    endingLandmarkIndex = null,
                    totalDistanceKm = calculateDistanceKm(
                        start = state.startingLandmark,
                        end = null
                    )
                )
            }

            recalculateFare()
            return
        }

        // Invalid starting
        if (
            currentState.startingLandmark == null &&
            index > (currentState.endingLandmarkIndex ?: Int.MAX_VALUE)
        ) {

            SnackbarManager.show(
                message = "Starting point cannot be after ending point",
                type = SnackbarType.ERROR
            )

            return
        }

        // Select starting landmark
        if (currentState.startingLandmark == null) {

            _uiState.update { state ->

                val newStart = landmark

                state.copy(
                    startingLandmark = newStart,
                    startingLandmarkIndex = index,
                    totalDistanceKm = calculateDistanceKm(
                        start = newStart,
                        end = state.endingLandmark
                    )
                )
            }

            recalculateFare()
            return
        }

        // Invalid ending
        if (index < (currentState.startingLandmarkIndex ?: 0)) {

            SnackbarManager.show(
                message = "Ending point must come after starting point",
                type = SnackbarType.ERROR
            )

            return
        }

        // Select ending landmark
        _uiState.update { state ->

            val newEnd = landmark

            state.copy(
                endingLandmark = newEnd,
                endingLandmarkIndex = index,
                totalDistanceKm = calculateDistanceKm(
                    start = state.startingLandmark,
                    end = newEnd
                )
            )
        }

        recalculateFare()
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
                recalculateFare()
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
            _uiState.update {
                it.copy(isPrinting = true)
            }
            val state = _uiState.value

            val startingLandmark = state.startingLandmark
            val endingLandmark = state.endingLandmark
            val passengers = state.passengers

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

            if (!printerManager.checkConnection()) {
                SnackbarManager.show(
                    message = "Printer connection failed. Please reconnect the printer and resume the service.",
                    type = SnackbarType.ERROR,
                    duration = SnackbarDuration.Long
                )
                emitNavigateBack()
                return@launch
            }

            try {

                val lineItems = selectedPassengers.map { passenger ->

                    val singleFare = calculateTicketFareUseCase(
                        fareFunction = state.fareFunctionString,
                        passengers = listOf(passenger.copy(passengerCount = 1)),
                        distanceInMeters = state.totalDistanceKm * 1000.0
                    )

                    val lineFare = singleFare * passenger.passengerCount

                    Triple(
                        passenger.name,
                        passenger.passengerCount,
                        lineFare
                    )
                }

                val totalFare = state.totalFare

                val ticketData = withContext(Dispatchers.Default) {

                    TicketBuilder()

                        .addCommand(PrinterManager.Commands.ALIGN_CENTER)

                        .addCommand(PrinterManager.Commands.BOLD_ON)
                        .text("RKV")
                        .addCommand(PrinterManager.Commands.BOLD_OFF)

                        .addCommand(PrinterManager.Commands.SIZE_NORMAL)
                        .threeColumnRow(
                            dateStr,
                            timeStr,
                            "TKT-000123"
                        )

                        .addCommand(PrinterManager.Commands.BOLD_ON)
                        .routeRow(
                            formatText(startingLandmark.name),
                            "To",
                            formatText(endingLandmark.name)
                        )
                        .addCommand(PrinterManager.Commands.BOLD_OFF)

                        .addCommand(PrinterManager.Commands.LINE_SPACE_TIGHT)

                        .apply {

                            lineItems.forEach { item ->

                                val name = item.first
                                val count = item.second
                                val lineFare = item.third

                                val singleFare = lineFare / count

                                val line =
                                    "%-10s %2d x %6.2f = %6.2f".format(
                                        name.take(10),
                                        count,
                                        singleFare,
                                        lineFare
                                    )

                                text(line)
                            }
                        }

                        .addCommand(PrinterManager.Commands.SIZE_BIG)
                        .text("RS . $totalFare")
                        .addCommand(PrinterManager.Commands.SIZE_NORMAL)

                        .divider()

                        .addCommand(PrinterManager.Commands.ALIGN_CENTER)
                        .text("Not Transferable")
                        .text("Nixbug softwares")

                        .addCommand(PrinterManager.Commands.LINE_SPACE_DEFAULT)
                        .addCommand(PrinterManager.Commands.BOLD_OFF)

                        .build()
                }

                val result = printerManager.sendData(ticketData)

                if (!result.isSuccess) {
                    throw result.exceptionOrNull()
                        ?: Exception("Unknown error")
                }

                resetPassengerCounts()

            } catch (e: Exception) {
                SnackbarManager.show(
                    "Printer connection failed. Please reconnect the printer and resume the service.",
                    SnackbarType.ERROR,
                    SnackbarDuration.Long
                )
                emitNavigateBack()
            } finally {
                _uiState.update {
                    it.copy(isPrinting = false)
                }
            }
        }
    }

    private fun resetPassengerCounts() {
        _uiState.update { state ->
            state.copy(
                passengers = state.passengers.map { passenger ->
                    passenger.copy(
                        passengerCount = if (
                            passenger.name.equals("Adult", ignoreCase = true)
                        ) {
                            1
                        } else {
                            0
                        }
                    )
                }
            )
        }
        recalculateFare()

    }
}
