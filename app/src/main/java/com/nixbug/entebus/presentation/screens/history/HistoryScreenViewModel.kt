package com.nixbug.entebus.presentation.screens.history

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import java.time.Duration
import java.time.Instant

@HiltViewModel
class HistoryScreenViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()


    fun calculateDuration(startedOn: String?, finishedOn: String?): String {
        if (startedOn == null || finishedOn == null) return "Not finished yet"

        val startInstant = Instant.parse(startedOn)
        val endInstant = Instant.parse(finishedOn)

        val duration = Duration.between(startInstant, endInstant)

        val hours = duration.toHours()
        val minutes = duration.minusHours(hours).toMinutes()

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "Less than a minute"
        }
    }
}
