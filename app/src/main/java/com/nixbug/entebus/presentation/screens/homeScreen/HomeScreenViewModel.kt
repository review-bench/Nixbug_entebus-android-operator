package com.nixbug.entebus.presentation.screens.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: MutableStateFlow<HomeUiState> = _uiState

    private val _events = MutableSharedFlow<HomeUiEvent>()
    val events = _events

    fun onResumeService(service: HomeUiModel) {
        /* TODO: Handle Logic To Navigate To Ticket Page*/
    }

    fun onStartAssigned(service: HomeUiModel) {
        if (_uiState.value.ongoingService != null) {
            viewModelScope.launch {
                _events.emit(HomeUiEvent.ServiceAlreadyRunning)
            }
            return
        }
        _uiState.update { state ->
            val newAssigned = state.assignedServiceList.filter { it.id != service.id }
            state.copy(
                ongoingService = service,
                assignedServiceList = newAssigned,
            )
        }
    }

    fun onStartDownloaded(service: HomeUiModel) {
        if (_uiState.value.ongoingService != null) {
            viewModelScope.launch {
                _events.emit(HomeUiEvent.ServiceAlreadyRunning)
            }
            return
        }
        _uiState.update { state ->
            val newDownload = state.downloadedServiceList.filter { it.id != service.id }

            state.copy(
                ongoingService = service,
                downloadedServiceList = newDownload,
            )
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
     * Converts a UTC timestamp into IST (Asia/Kolkata) date and time.
     *
     * The date is mapped to a user-friendly label when applicable:
     * - "Today" if the date matches the current IST date
     * - "Yesterday" if it is one day before today
     * - "Tomorrow" if it is one day after today
     * - Otherwise, the date is formatted as "MMM dd yyyy"
     *
     * The time is always formatted in 12-hour format (hh:mm a).
     *
     * Example:
     * Input:  "2024-12-10T03:00:00Z"
     * Output: ("Today", "08:30 AM")
     *
     * @param utcTimestamp ISO-8601 formatted UTC timestamp.
     * @return A Pair containing the date label and formatted time.
     */

    fun convertUtcToIst(utcTimestamp: String): Pair<String, String> {
        val instant = Instant.parse(utcTimestamp)
        val istZone = ZoneId.of("Asia/Kolkata")
        val dateTime = instant.atZone(istZone)

        val date = dateTime.toLocalDate()
        val time = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm a"))

        val today = LocalDate.now(istZone)
        val yesterday = today.minusDays(1)
        val tomorrow = today.plusDays(1)

        val dateLabel = when (date) {
            today -> "Today"
            yesterday -> "Yesterday"
            tomorrow -> "Tomorrow"
            else -> date.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
        }
        return Pair(dateLabel, time)
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
