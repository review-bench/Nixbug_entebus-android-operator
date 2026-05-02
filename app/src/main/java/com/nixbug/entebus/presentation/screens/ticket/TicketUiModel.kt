package com.nixbug.entebus.presentation.screens.ticket

data class Landmark(
    val id: Int,
    val name: String,
    val distanceFromStart: Float,
)

data class Passenger(
    val id: Int,
    val name: String,
    val passengerCount: Int
)

sealed class TicketUiEvent {
    data object EmitNavigateBack : TicketUiEvent()
}
