package com.nixbug.entebus.presentation.screens.ticketScreen

data class TicketUiState(
    val landmarks: List<Landmark> = SampleLandmarks,
    val passengers: List<Passenger> = samplePassenger
)
