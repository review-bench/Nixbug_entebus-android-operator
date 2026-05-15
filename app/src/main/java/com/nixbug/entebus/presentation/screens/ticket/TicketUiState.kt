package com.nixbug.entebus.presentation.screens.ticket

import com.nixbug.entebus.domain.model.checker.PassengerTypeCount

data class TicketUiState(
    val landmarks: List<Landmark> = emptyList(),
    val passengers: List<Passenger> = emptyList(),
    val startingLandmark: Landmark? = null,
    val endingLandmark: Landmark? = null,
    val startingLandmarkIndex: Int? = null,
    val endingLandmarkIndex: Int? = null,
    val totalDistanceKm: Float = 0f,
    val isPrinting: Boolean = false,
    val isLoading: Boolean = false,
    val fareFunctionString: String = "",
    val totalFare: Double = 0.0,
    val serviceId: Int? = null,
    val companyName: String = "",
    val operatorName: String = "",
    val operatorId: Int? = null,
    val passengerStats: List<PassengerTypeCount> = emptyList(),
    val isLoadingPassengerStats: Boolean = false
)
