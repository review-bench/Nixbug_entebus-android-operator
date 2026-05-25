package com.nixbug.entebus.presentation.screens.ticket

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.domain.model.DetailedTicketType
import com.nixbug.entebus.domain.model.PaperTicket
import com.nixbug.entebus.domain.model.PaperTicketType
import com.nixbug.entebus.domain.model.Ticket
import com.nixbug.entebus.domain.model.local.LocalServiceStatus
import com.nixbug.entebus.domain.usecase.GetPassengerStatsUseCase
import com.nixbug.entebus.domain.usecase.queue.EnqueuePaperTicketUseCase
import com.nixbug.entebus.domain.usecase.collection.GetNextTicketSequenceUseCase
import com.nixbug.entebus.domain.usecase.collection.AddServiceCollectionUseCase
import com.nixbug.entebus.domain.usecase.collection.GetLastTicketSequenceUseCase
import com.nixbug.entebus.domain.usecase.fare.CalculateTicketFareUseCase
import com.nixbug.entebus.domain.usecase.service.GetDetailedServiceByStatusUseCase
import com.nixbug.entebus.domain.usecase.token.GetTokenUseCase
import com.nixbug.entebus.domain.usecase.user.GetUserUseCase
import com.nixbug.entebus.presentation.screens.ticket.uimapper.PassengerDefaults
import com.nixbug.entebus.presentation.screens.ticket.uimapper.toUiModel
import com.nixbug.entebus.utils.printer.PrinterManager
import com.nixbug.entebus.utils.printer.TicketBuilder
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import com.nixbug.entebus.utils.titleCaseFirstChar
import com.nixbug.entebus.utils.toCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
import java.security.MessageDigest
import java.time.Instant
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

private const val MAX_PASSENGER_COUNT = 99
private const val METERS_PER_KILOMETER = 1000f
private val SYMBOLS = charArrayOf('-', '*', '+', '#')

@HiltViewModel
class TicketViewModel @Inject constructor(
    private val printerManager: PrinterManager,
    private val getDetailedServiceByStatusUseCase: GetDetailedServiceByStatusUseCase,
    private val calculateTicketFareUseCase: CalculateTicketFareUseCase,
    private val enqueuePaperTicketUseCase: EnqueuePaperTicketUseCase,
    private val getNextTicketSequenceUseCase: GetNextTicketSequenceUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getTokenUseCase: GetTokenUseCase,
    private val addServiceCollectionUseCase: AddServiceCollectionUseCase,
    private val getPassengerStatsUseCase: GetPassengerStatsUseCase,
    private val getLastTicketSequenceUseCase: GetLastTicketSequenceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TicketUiState())
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TicketUiEvent>()
    val events: SharedFlow<TicketUiEvent> = _events.asSharedFlow()

    init {
        loadStartedService()
        loadUserData()
    }

    private fun buildPaperTicketRequest(ticketId: Int, lineItems: List<LineItem>): PaperTicket {

        val state = _uiState.value

        val start = requireNotNull(state.startingLandmark)
        val end = requireNotNull(state.endingLandmark)

        val serviceId = requireNotNull(state.serviceId) { "Service id missing" }
        val ticketTypes = lineItems.map {
            PaperTicketType(
                id = it.id,
                count = it.count,
                price = it.singleFare
            )
        }

        return PaperTicket(
            ticket = Ticket(
                id = ticketId,
                serviceId = serviceId,
                createdOn = Instant.now().toString(),
                ticketTypes = ticketTypes,
                amount = ticketTypes.sumOf { it.price * it.count },
                pickupPoint = start.id,
                droppingPoint = end.id,
                distance = (state.totalDistanceKm * METERS_PER_KILOMETER).toInt(),
                extras = emptyMap()
            )
        )
    }

    private fun loadUserData() {
        viewModelScope.launch {

            val user = getUserUseCase().first()
            val token = getTokenUseCase().first()

            _uiState.update {
                it.copy(
                    companyName = user.companyName ?: "",
                    operatorName = user.username ?: "",
                    operatorId = token.operatorId
                )
            }
        }
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

                val landmarks = currentService.landmarks
                    .sortedBy { it.distance }
                    .map { it.toUiModel() }
                _uiState.update { state ->
                    state.copy(
                        serviceId = currentService.service.id,
                        landmarks = landmarks,
                        startingLandmark = landmarks.firstOrNull(),
                        startingLandmarkIndex = 0,
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

        viewModelScope.launch {

            val state = _uiState.value

            val start = state.startingLandmark
            val end = state.endingLandmark

            if (start == null || end == null) {
                _uiState.update { it.copy(totalFare = 0.0) }
                return@launch
            }

            val fare = try {

                withContext(Dispatchers.Default) {

                    calculateTicketFareUseCase(
                        fareFunction = state.fareFunctionString,
                        passengers = state.passengers.map {
                            DetailedTicketType(
                                id = it.id,
                                name = it.name,
                                count = it.passengerCount
                            )
                        },
                        distanceInMeters = state.totalDistanceKm * METERS_PER_KILOMETER
                    )
                }

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(
                    message = "Fare calculation failed. Please check service configuration.",
                    type = SnackbarType.ERROR
                )
                0.0
            }

            _uiState.update {
                it.copy(totalFare = fare)
            }
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

            _uiState.update { it.copy(isPrinting = true) }
            try {
                val state = _uiState.value
                val startingLandmark = state.startingLandmark
                val endingLandmark = state.endingLandmark
                val passengers = state.passengers
                val serviceId = state.serviceId
                val operatorId = state.operatorId
                if (serviceId == null) {
                    SnackbarManager.show(
                        message = "Service data is still loading. Please try again.",
                        type = SnackbarType.ERROR
                    )
                    return@launch
                }
                if (operatorId == null) {
                    SnackbarManager.show(
                        message = "Operator session expired. Please login again.",
                        type = SnackbarType.ERROR,
                        duration = SnackbarDuration.Long
                    )
                    return@launch
                }

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

                if (!printerManager.checkConnection()) {
                    SnackbarManager.show(
                        message = "Printer connection failed. Please reconnect the printer and resume the service.",
                        type = SnackbarType.ERROR,
                        duration = SnackbarDuration.Long
                    )
                    emitNavigateBack()
                    return@launch
                }

                val calendar = java.util.Calendar.getInstance()

                val dateFormat =
                    java.text.SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault())

                val timeFormat =
                    java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())

                val dateStr = dateFormat.format(calendar.time)

                val timeStr = timeFormat.format(calendar.time)

                val lineItems = try {
                    withContext(Dispatchers.Default) {
                        selectedPassengers.map { passenger ->
                            val singleFare = calculateTicketFareUseCase(
                                fareFunction = state.fareFunctionString,
                                passengers = listOf(
                                    DetailedTicketType(
                                        id = passenger.id,
                                        name = passenger.name,
                                        count = 1
                                    )
                                ),
                                distanceInMeters = state.totalDistanceKm * METERS_PER_KILOMETER
                            )

                            LineItem(
                                id = passenger.id,
                                name = passenger.name,
                                count = passenger.passengerCount,
                                singleFare = singleFare
                            )
                        }
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    SnackbarManager.show(
                        message = "Fare calculation failed. Please check service configuration.",
                        type = SnackbarType.ERROR
                    )
                    return@launch
                }

                val totalFare = lineItems.sumOf { it.singleFare * it.count }

                val ticketId = getNextTicketSequenceUseCase(
                    serviceId = serviceId,
                    operatorId = operatorId
                )
                val serviceIdDivider = generateServiceVisualPattern(serviceId)

                val ticketData = withContext(Dispatchers.Default) {

                    TicketBuilder()
                        .addCommand(PrinterManager.Commands.ALIGN_CENTER)
                        .addCommand(PrinterManager.Commands.BOLD_ON)
                        .text(state.companyName)
                        .addCommand(PrinterManager.Commands.BOLD_OFF)
                        .addCommand(PrinterManager.Commands.SIZE_NORMAL)
                        .threeColumnRow(
                            dateStr,
                            timeStr,
                            "T-${serviceId}-${ticketId}"
                        )
                        .addCommand(PrinterManager.Commands.BOLD_ON)
                        .routeRow(
                            formatText(startingLandmark.name.titleCaseFirstChar()),
                            "To",
                            formatText(endingLandmark.name.titleCaseFirstChar())
                        )
                        .addCommand(PrinterManager.Commands.BOLD_OFF)
                        .addCommand(PrinterManager.Commands.LINE_SPACE_TIGHT)

                        .apply {
                            lineItems.forEach { item ->
                                val name = item.name
                                val count = item.count
                                val lineFare = item.singleFare * item.count

                                text(
                                    "%-10s %2d x %6.2f = %6.2f".format(
                                        name.take(10),
                                        count,
                                        item.singleFare,
                                        lineFare
                                    )
                                )
                            }
                        }

                        .addCommand(PrinterManager.Commands.SIZE_BIG)
                        .text("RS . ${totalFare.toCurrency()}")
                        .addCommand(PrinterManager.Commands.SIZE_NORMAL)
                        .text(serviceIdDivider)
                        .addCommand(PrinterManager.Commands.ALIGN_CENTER)
                        .text("Not Transferable")
                        .text("Entebus")
                        .addCommand(PrinterManager.Commands.LINE_SPACE_DEFAULT)
                        .addCommand(PrinterManager.Commands.BOLD_OFF)
                        .build()
                }

                try {
                    val result = printerManager.sendData(ticketData)
                    if (!result.isSuccess) {
                        throw (
                                result.exceptionOrNull()
                                    ?: Exception("Unknown error")
                                )
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    SnackbarManager.show(
                        "Printing failed. Please check the printer and try again.",
                        SnackbarType.ERROR,
                        SnackbarDuration.Long
                    )
                    emitNavigateBack()
                    return@launch
                }
                val request = buildPaperTicketRequest(ticketId, lineItems = lineItems)
                enqueuePaperTicketUseCase(request)
                addServiceCollectionUseCase(
                    serviceId = serviceId,
                    operatorId = operatorId,
                    amount = totalFare
                )
                delay(500)
                resetTicketState()
            } catch (e: Exception) {

                SnackbarManager.show(
                    message = e.message ?: "Something went wrong",
                    type = SnackbarType.ERROR
                )

            } finally {
                _uiState.update { it.copy(isPrinting = false) }
            }
        }
    }

    private fun resetTicketState() {
        _uiState.update { state ->
            state.copy(
                passengers = state.passengers.map { passenger ->
                    passenger.copy(
                        passengerCount = PassengerDefaults.defaultCount(passenger.name)
                    )
                }
            )
        }
        recalculateFare()
    }

    fun generateServiceVisualPattern(serviceId: Int): String {
        // Hash the service ID
        val hash = MessageDigest
            .getInstance("SHA-256")
            .digest(serviceId.toString().toByteArray())

        // Determine symbol order from hash
        val shuffled = SYMBOLS.clone()
        for (i in shuffled.indices) {
            val swapIndex = (hash[i].toInt() and 0xFF) % shuffled.size
            val temp = shuffled[i]
            shuffled[i] = shuffled[swapIndex]
            shuffled[swapIndex] = temp
        }

        // Build final string: 4 segments × 8 characters = 32 characters
        val result = StringBuilder()
        for (i in 0 until 4) {
            repeat(8) {
                result.append(shuffled[i])
            }
        }

        return result.toString()
    }

    //LEGACY TICKET SCREEN

    fun loadPassengerStats() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isLoadingPassengerStats = true) }
            val serviceId = state.serviceId
            val operatorId = state.operatorId
            val start = state.startingLandmark?.id
            val end = state.endingLandmark?.id
            if (serviceId == null) {
                SnackbarManager.show(
                    message = "Service data is still loading. Please try again.",
                    type = SnackbarType.ERROR
                )
                _uiState.update { it.copy(isLoadingPassengerStats = false) }
                return@launch
            }
            if (start == null || end == null) {
                SnackbarManager.show(
                    message = "Please select starting and ending points",
                    type = SnackbarType.ERROR
                )
                _uiState.update { it.copy(isLoadingPassengerStats = false) }
                return@launch
            }
            if (operatorId == null) {
                SnackbarManager.show(
                    message = "Operator session expired. Please login again.",
                    type = SnackbarType.ERROR,
                    duration = SnackbarDuration.Long
                )
                _uiState.update { it.copy(isLoadingPassengerStats = false) }
                return@launch
            }
            try {
                val lastSequence = getLastTicketSequenceUseCase(serviceId, operatorId)
                if (!printerManager.checkConnection()) {
                    SnackbarManager.show(
                        message = "Printer connection failed. Please reconnect the printer and resume the service.",
                        type = SnackbarType.ERROR,
                        duration = SnackbarDuration.Long
                    )
                    emitNavigateBack()
                    _uiState.update { it.copy(isLoadingPassengerStats = false) }
                    return@launch
                }
                val result =
                    getPassengerStatsUseCase(
                        serviceId = serviceId,
                        fromLandmarkId = start,
                        toLandmarkId = end
                    )
                if (result.isEmpty()) {
                    SnackbarManager.show(
                        message = "Issue at least one ticket to print passenger statistics.",
                        type = SnackbarType.WARNING
                    )
                    _uiState.update { it.copy(isLoadingPassengerStats = false) }
                    return@launch
                }
                _uiState.update { it.copy(passengerStats = result) }
                val serviceIdDivider = generateServiceVisualPattern(serviceId)
                val ticketData = withContext(Dispatchers.Default) {
                    TicketBuilder()
                        .addCommand(PrinterManager.Commands.ALIGN_CENTER)
                        .addCommand(PrinterManager.Commands.BOLD_ON)
                        .addCommand(PrinterManager.Commands.SIZE_DOUBLE_HEIGHT)
                        .addCommand(PrinterManager.Commands.LINE_SPACE_TIGHT)
                        .text("Passenger Statistics")
                        .addCommand(PrinterManager.Commands.BOLD_OFF)
                        .addCommand(PrinterManager.Commands.SIZE_NORMAL)
                        .text("------------------")
                        .addCommand(PrinterManager.Commands.LINE_SPACE_DEFAULT)
                        .addCommand(PrinterManager.Commands.ALIGN_LEFT)
                        .text("From : ${state.startingLandmark.name}")
                        .text("To   : ${state.endingLandmark.name}")
                        .addCommand(PrinterManager.Commands.ALIGN_CENTER)
                        .addCommand(PrinterManager.Commands.LINE_SPACE_TIGHT)
                        .text("Expected count(approximate)")
                        .text("--------------")
                        .addCommand(PrinterManager.Commands.LINE_SPACE_DEFAULT)
                        .addCommand(PrinterManager.Commands.ALIGN_LEFT)
                        .apply {
                            result.forEach {
                                leftRightRow("   ${it.typeName}", "${it.count}   ")
                            }
                        }
                        .addCommand(PrinterManager.Commands.ALIGN_CENTER)
                        .addCommand(PrinterManager.Commands.LINE_SPACE_TIGHT)
                        .text("Ticket IDs")
                        .text("--------")
                        .addCommand(PrinterManager.Commands.LINE_SPACE_DEFAULT)
                        .addCommand(PrinterManager.Commands.ALIGN_LEFT)
                        .text("First Ticket ID : T-${state.serviceId}-1")
                        .text("Final Ticket ID : T-${state.serviceId}-${lastSequence}")
                        .text(serviceIdDivider)
                        .text("Remark : ")
                        .text("")
                        .text("")
                        .text("")
                        .build()
                }

                try {
                    val result = printerManager.sendData(ticketData)
                    if (!result.isSuccess) {
                        throw (
                                result.exceptionOrNull()
                                    ?: Exception("Unknown error")
                                )
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    SnackbarManager.show(
                        "Printing failed. Please check the printer and try again.",
                        SnackbarType.ERROR,
                        SnackbarDuration.Long
                    )
                    emitNavigateBack()
                    return@launch
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(
                    message = "Failed to load passenger stats",
                    type = SnackbarType.ERROR
                )
            } finally {
                _uiState.update { it.copy(isLoadingPassengerStats = false) }
            }
        }
    }

    fun selectPreviousStartingLandmark() {
        val currentIndex = _uiState.value.startingLandmarkIndex ?: return
        if (currentIndex <= 0) return
        val newIndex = currentIndex - 1
        val landmark = _uiState.value.landmarks[newIndex]
        selectStartingLandmark(landmark = landmark, index = newIndex)
    }

    fun selectNextStartingLandmark() {
        val currentIndex = _uiState.value.startingLandmarkIndex ?: return
        val landmarks = _uiState.value.landmarks
        if (currentIndex >= landmarks.lastIndex) return
        val newIndex = currentIndex + 1
        val landmark = landmarks[newIndex]
        selectStartingLandmark(landmark = landmark, index = newIndex)
    }

    fun selectPreviousEndingLandmark() {
        val currentIndex = _uiState.value.endingLandmarkIndex ?: return
        val startIndex = _uiState.value.startingLandmarkIndex ?: -1
        if (currentIndex - 1 <= startIndex) return
        val newIndex = currentIndex - 1
        val landmark = _uiState.value.landmarks[newIndex]
        selectEndingLandmark(landmark = landmark, index = newIndex)
    }

    fun selectNextEndingLandmark() {
        val currentIndex = _uiState.value.endingLandmarkIndex ?: return
        val landmarks = _uiState.value.landmarks
        if (currentIndex >= landmarks.lastIndex) return
        val newIndex = currentIndex + 1
        val landmark = landmarks[newIndex]
        selectEndingLandmark(landmark = landmark, index = newIndex)
    }

    fun selectStartingLandmark(landmark: Landmark, index: Int) {
        val endIndex = _uiState.value.endingLandmarkIndex
        if (endIndex != null && index >= endIndex) {
            SnackbarManager.show(
                message = "Starting point cannot be after ending point",
                type = SnackbarType.ERROR
            )
            return
        }
        _uiState.update { state ->
            state.copy(
                startingLandmark = landmark,
                startingLandmarkIndex = index,
                totalDistanceKm = calculateDistanceKm(landmark, state.endingLandmark)
            )
        }
        recalculateFare()
    }

    fun selectEndingLandmark(landmark: Landmark, index: Int) {
        val startIndex = _uiState.value.startingLandmarkIndex
        if (startIndex != null && index <= startIndex) {
            SnackbarManager.show(
                message = "Ending point must come after starting point",
                type = SnackbarType.ERROR
            )
            return
        }
        _uiState.update { state ->
            state.copy(
                endingLandmark = landmark,
                endingLandmarkIndex = index,
                totalDistanceKm = calculateDistanceKm(state.startingLandmark, landmark)
            )
        }
        recalculateFare()
    }

    fun clearStartingLandmark() {
        _uiState.update {
            it.copy(
                startingLandmark = null,
                startingLandmarkIndex = null,
                totalDistanceKm = 0f,
                totalFare = 0.0
            )
        }
    }

    fun clearEndingLandmark() {
        _uiState.update {
            it.copy(
                endingLandmark = null,
                endingLandmarkIndex = null,
                totalDistanceKm = 0f,
                totalFare = 0.0
            )
        }
    }
}
