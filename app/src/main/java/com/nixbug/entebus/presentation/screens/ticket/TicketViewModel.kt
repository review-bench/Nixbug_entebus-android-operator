package com.nixbug.entebus.presentation.screens.ticket

import androidx.lifecycle.ViewModel
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val MAX_PASSENGER_COUNT = 99
private const val METERS_PER_KILOMETER = 1000f
private val SortedSampleLandmarks = SampleLandmarks.sortedBy { it.distanceFromStart }

@HiltViewModel
class TicketViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TicketUiState(landmarks = SortedSampleLandmarks))
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()

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
}
