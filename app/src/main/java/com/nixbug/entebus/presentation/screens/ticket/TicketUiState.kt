package com.nixbug.entebus.presentation.screens.ticket

data class TicketUiState(
    val landmarks: List<Landmark> = SampleLandmarks,
    val passengers: List<Passenger> = samplePassengers,
    val startingLandmark: Landmark? = null,
    val endingLandmark: Landmark? = null,
    val startingLandmarkIndex: Int? = null,
    val endingLandmarkIndex: Int? = null,
    val totalDistanceKm: Float = 0f
)
