package com.nixbug.entebus.presentation.screens.homeScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HomeScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: MutableStateFlow<HomeUIState> = _uiState

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

    fun extractRouteName(fullName: String): String {
        val noDateTime = fullName.replace(
            Regex("""^\d{4}-\d{2}-\d{2}\s+\d{1,2}:\d{2}\s*(AM|PM)\s*"""), ""
        )

        val noReg = noDateTime.replace(
            Regex("""\s*\([^)]*\)\s*$"""), ""
        )

        return noReg.trim()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun canStartService(startingAtUtc: String): Boolean {
        val instant = Instant.parse(startingAtUtc)

        val istZone = ZoneId.of("Asia/Kolkata")
        val serviceTimeIst = instant.atZone(istZone)

        val nowIst = Instant.now().atZone(istZone)

        val diffMinutes = java.time.Duration.between(nowIst, serviceTimeIst).toMinutes()

        return diffMinutes <= 60
    }
}
