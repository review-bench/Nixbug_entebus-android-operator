package com.nixbug.entebus.domain.repository

interface FareCalculatorRepository {

    fun initialize(
        fareFunction: String
    )

    fun calculate(
        ticketType: String,
        distanceInMeters: Double,
        extras: Map<String, Any> = emptyMap()
    ): Double
}
