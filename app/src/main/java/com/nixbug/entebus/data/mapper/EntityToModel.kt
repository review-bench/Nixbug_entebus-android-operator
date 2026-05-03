package com.nixbug.entebus.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nixbug.entebus.data.local.room.db.entity.FareEntity
import com.nixbug.entebus.data.local.room.db.entity.LandmarkEntity
import com.nixbug.entebus.data.local.room.db.entity.ServiceEntity
import com.nixbug.entebus.data.local.room.db.entity.ServiceWithDetails
import com.nixbug.entebus.data.local.room.db.entity.VehicleEntity
import com.nixbug.entebus.domain.model.DetailedFare
import com.nixbug.entebus.domain.model.DetailedLandmark
import com.nixbug.entebus.domain.model.DetailedService
import com.nixbug.entebus.domain.model.DetailedTicketType
import com.nixbug.entebus.domain.model.DetailedVehicle
import com.nixbug.entebus.domain.model.ServiceDetails

private val gson = Gson()


fun ServiceWithDetails.toDomain(
    gson: Gson
) = ServiceDetails(
    service = service.toDomain(),
    landmarks = landmarks.map { it.toDomain() },
    fare = fare.toDomain(gson),
    vehicle = vehicle.toDomain()
)

fun ServiceEntity.toDomain() = DetailedService(
    id = id,
    companyId = companyId,
    name = name,
    status = status,
    localStatus = localStatus,
    registrationNumber = registrationNumber,
    startingLandmarkId = startingLandmarkId,
    endingLandmarkId = endingLandmarkId,
    ticketMode = ticketMode,
    remark = remark,
    startingAt = startingAt,
    endingAt = endingAt,
    createdOn = createdOn,
    updatedOn = updatedOn,
    publicKey = publicKey,
    startingLandmarkName = startingLandmarkName,
    endingLandmarkName = endingLandmarkName,
    startingLandmarkAlias = startingLandmarkAlias,
    endingLandmarkAlias = endingLandmarkAlias
)

fun LandmarkEntity.toDomain() = DetailedLandmark(
    landmarkId = landmarkId,
    landmarkName = landmarkName,
    aliasName = aliasName,
    distance = distance,
    arrivalAt = arrivalAt,
    departureAt = departureAt
)

fun FareEntity.toDomain(
    gson: Gson
) = DetailedFare(
    fareId = fareId,
    name = name,
    function = function,
    currencyType = currencyType,
    distanceUnit = distanceUnit,
    version = version,
    ticketTypes = ticketTypesJson.toTicketTypes(gson),
    dfVersion = dfVersion
)

fun VehicleEntity.toDomain() = DetailedVehicle(
    vehicleId = vehicleId,
    name = name,
    registrationNumber = registrationNumber,
    capacity = capacity,
    version = version
)

fun String.toTicketTypes(gson: Gson): List<DetailedTicketType> {

    return try {

        val type = object : TypeToken<List<DetailedTicketType>>() {}.type

        gson.fromJson(this, type) ?: emptyList()

    } catch (e: Exception) {
        if (e is ClassCastException) {
            throw e
        }
        emptyList()
    }
}
