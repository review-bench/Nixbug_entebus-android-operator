package com.nixbug.entebus.presentation.screens.ticketScreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class TicketViewModel @Inject constructor() : ViewModel() {

    private val _uiState =
        MutableStateFlow(TicketUiState(landmarks = SampleLandmarks.sortedBy { it.distanceFromStart }))
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TicketUiEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private fun emitToast(message: String) {
        _events.tryEmit(TicketUiEvent.ShowToast(message))
    }

    fun handleLandmarkSelection(landmark: Landmark, index: Int) {
        // Retap start → deselect
        if (_uiState.value.startingLandmark?.id == landmark.id) {
            _uiState.update { state ->
                state.copy(
                    startingLandmark = null,
                    startingLandmarkIndex = null,
                    totalDistanceKm = 0f
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
                    totalDistanceKm = 0f
                )
            }
            return
        }

        // Invalid starting (below ending)
        if (_uiState.value.startingLandmark == null && index > (_uiState.value.endingLandmarkIndex
                ?: Int.MAX_VALUE)
        ) {
            emitToast("Starting point must be above ending point")
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
            emitToast("Ending point must be below starting point")
            return
        }

        // Selecting Ending landmark
        _uiState.update { state ->
            val newEnd = landmark
            state.copy(
                endingLandmark = newEnd,
                endingLandmarkIndex = index,
                totalDistanceKm = calculateDistanceKm(state.startingLandmark,newEnd)
            )
        }
    }

    private fun calculateDistanceKm(start: Landmark?, end: Landmark?): Float {
        return if (start != null && end != null) {
            kotlin.math.abs(end.distanceFromStart - start.distanceFromStart) / 1000f
        } else {
            0f
        }
    }

    fun updatePassengerCount(id: Int, count: Int) {
        _uiState.update { state ->
            state.copy(
                passengers = state.passengers.map {
                    if (it.id == id) it.copy(count = count) else it
                }
            )
        }
    }

}
