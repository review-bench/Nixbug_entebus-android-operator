package com.nixbug.entebus.presentation.screens.ticket

data class Landmark(
    val id: Int,
    val name: String,
    val distanceFromStart: Int,
)

data class Passenger(
    val id: Int,
    val name: String,
    val passengerCount: Int
)
