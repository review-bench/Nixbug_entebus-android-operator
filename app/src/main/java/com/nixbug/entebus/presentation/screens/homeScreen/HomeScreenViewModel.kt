package com.nixbug.entebus.presentation.screens.homeScreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HomeScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: MutableStateFlow<HomeUIState> = _uiState

    fun onResumeService(service: HomeUiModel){
        Log.d("HomeViewModel", "Navigate to Ticket Screen")
    }

    fun onStartAssigned(service: HomeUiModel){
        if (_uiState.value.ongoingService != null) {
            Log.d("HomeViewModel", "Service already running")
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

    fun onStartDownloaded(service: HomeUiModel){
        if (_uiState.value.ongoingService != null) {
            Log.d("HomeViewModel", "Service already running")
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

    fun onDownloadService(service: HomeUiModel){
        _uiState.update { state ->
            val newOtherServices = state.otherServiceList.filter { it.id != service.id }
            state.copy(
                downloadedServiceList = state.downloadedServiceList + service,
                otherServiceList = newOtherServices,
            )
        }
    }

    fun extractRouteName(fullName: String): String {
        // Remove prefix date + time
        val noDateTime = fullName.replace(
            Regex("""^\d{4}-\d{2}-\d{2}\s+\d{1,2}:\d{2}\s*(AM|PM)\s*"""),
            ""
        )

        // Remove last bracket (reg no)
        val noReg = noDateTime.replace(
            Regex("""\s*\([^)]*\)\s*$"""),
            ""
        )

        return noReg.trim()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertUtcToIst(utcTimestamp: String): Pair<String, String> {
        val instant = Instant.parse(utcTimestamp)
        val istZone = ZoneId.of("Asia/Kolkata")
        val dateTime = instant.atZone(istZone)

        val date = dateTime.toLocalDate()
        val time = dateTime.toLocalTime()
            .format(DateTimeFormatter.ofPattern("hh:mm a"))

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


}
