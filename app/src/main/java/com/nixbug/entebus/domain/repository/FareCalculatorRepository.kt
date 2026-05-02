package com.nixbug.entebus.domain.repository

interface FareCalculatorRepository {

    suspend fun initialize(
        fareFunction: String
    )

    suspend fun calculate(
        ticketType: String,
        distanceInMeters: Double,
        extras: Map<String, Any> = emptyMap()
    ): Double
}