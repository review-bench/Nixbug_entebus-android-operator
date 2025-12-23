package com.nixbug.entebus.presentation.screens.ticketScreen

data class TicketUiState(
    val landmarks: List<Landmark> = SampleLandmarks,
    val passengers: List<Passenger> = samplePassenger,

    val startingLandmark: Landmark? = null,
    val endingLandmark: Landmark? = null,

    val startingLandmarkIndex: Int? = null,
    val endingLandmarkIndex: Int? = null,

    val totalDistanceKm: Float = 0f
)

sealed class TicketUiEvent {
    data class ShowToast(val message: String) : TicketUiEvent()
}
