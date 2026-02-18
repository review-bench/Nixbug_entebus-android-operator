package com.nixbug.entebus.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
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
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeUiEvent>()
    val events: SharedFlow<HomeUiEvent> = _events.asSharedFlow()

    private fun checkAndEmitIfServiceAlreadyRunning(): Boolean {
        return if (_uiState.value.ongoingService != null) {
            SnackbarManager.show(
                message = "A service is already running",
                type = SnackbarType.ERROR,
            )
            true
        } else {
            false
        }
    }

    fun onResumeService() {
        viewModelScope.launch {
            _events.emit(HomeUiEvent.NavigateToTicketScreen)
        }
    }

    fun onStartAssigned(service: HomeUiModel) {
        if (checkAndEmitIfServiceAlreadyRunning()) return

        _uiState.update { state ->
            val newAssigned = state.assignedServiceList.filter { it.id != service.id }
            state.copy(
                ongoingService = service,
                assignedServiceList = newAssigned,
            )
        }
        viewModelScope.launch {
            _events.emit(HomeUiEvent.NavigateToTicketScreen)
        }
    }

    fun onStartDownloaded(service: HomeUiModel) {
        if (checkAndEmitIfServiceAlreadyRunning()) return

        _uiState.update { state ->
            val newDownload = state.downloadedServiceList.filter { it.id != service.id }
            state.copy(
                ongoingService = service,
                downloadedServiceList = newDownload,
            )
        }
        viewModelScope.launch {
            _events.emit(HomeUiEvent.NavigateToTicketScreen)
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

    fun onRestartRecentService(service: HomeUiModel) {
        if (checkAndEmitIfServiceAlreadyRunning()) return
        _uiState.update { state ->
            val newRecentServices = state.recentServiceList.filter { it.id != service.id }
            state.copy(
                ongoingService = service,
                recentServiceList = newRecentServices,
            )
        }
    }

    fun onFinishDuty(service: HomeUiModel) {
        _uiState.update { state ->
            state.copy(
                recentServiceList = state.recentServiceList + service,
                ongoingService = null,
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
