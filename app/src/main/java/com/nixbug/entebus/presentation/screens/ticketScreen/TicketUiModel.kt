package com.nixbug.entebus.presentation.screens.ticketScreen

data class Landmark(
    val id: Int,
    val name: String,
    val distanceFromStart: Int,
)

data class Passenger(
    val id: Int,
    val name: String
)