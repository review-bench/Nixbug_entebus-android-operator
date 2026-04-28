package com.nixbug.entebus.domain.model


data class ServiceDetailModel(
    val id: Int,
    val companyId: Int,
    val name: String,
    val status: Int,
    val registrationNumber: String,
    val startingLandmarkId: Int?,
    val endingLandmarkId: Int?,
    val ticketMode: Int,
    val remark: String?,
    val startingAt: String,
    val endingAt: String,
    val createdOn: String,
    val updatedOn: String?,
    val fare: Fare,
    val vehicle: Vehicle,
    val route: List<Route>,
    val publicKey: String,
    val startingLandmarkName: String,
    val startingLandmarkAlias: List<String>,
    val endingLandmarkName: String,
    val endingLandmarkAlias: List<String>,
)

data class Fare(
    val id: Int,
    val fareId: Int,
    val version: Int,
    val name: String,
    val attributes: FareAttributesModel,
    val function: String
)

data class FareAttributesModel(
    val dfVersion: Int,
    val ticketTypes: List<TicketType>,
    val currencyType: String,
    val distanceUnit: String,
    val extra: Map<String, Any>
)

data class TicketType(
    val id: Int,
    val name: String
)

data class Route(
    val serviceId: Int,
    val landmarkId: Int,
    val distanceFromStart: Int,
    val arrivalAt: String,
    val departureAt: String,
    val landmarkName: String,
    val landmarkAlias: List<String>,
)

data class Vehicle(
    val id: Int,
    val vehicleId: Int,
    val version: Int,
    val registrationNumber: String,
    val name: String,
    val capacity: Int
)
