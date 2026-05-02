package com.nixbug.entebus.domain.model

data class ServiceDetails(
    val service: DetailedService,
    val landmarks: List<DetailedLandmark>,
    val fare: DetailedFare,
    val vehicle: DetailedVehicle
)

data class DetailedService(
    val id: Int,
    val companyId: Int,
    val name: String,
    val status: Int,
    val localStatus: String,
    val registrationNumber: String,
    val startingLandmarkId: Int?,
    val endingLandmarkId: Int?,
    val ticketMode: Int,
    val remark: String?,
    val startingAt: String,
    val endingAt: String,
    val createdOn: String,
    val updatedOn: String?,
    val publicKey: String,
    val startingLandmarkName: String,
    val endingLandmarkName: String,
    val startingLandmarkAlias: String?,
    val endingLandmarkAlias: String?
)

data class DetailedLandmark(
    val landmarkId: Int,
    val landmarkName: String,
    val aliasName: String?,
    val distance: Int,
    val arrivalAt: String,
    val departureAt: String
)

data class DetailedFare(
    val fareId: Int,
    val name: String,
    val function: String,
    val currencyType: String,
    val distanceUnit: String,
    val version: Int,
    val ticketTypes: List<DetailedTicketType>,
    val dfVersion: Int
)

data class DetailedTicketType(

    val id: Int,
    val name: String,
    val price: Double? = null,
    val count: Int = 0
)

data class DetailedVehicle(
    val vehicleId: Int,
    val name: String,
    val registrationNumber: String,
    val capacity: Int,
    val version: Int
)
