package com.nixbug.entebus.data.local.room.mapper

import com.google.gson.Gson
import com.nixbug.entebus.data.local.room.db.entity.ServiceWithDetails
import com.nixbug.entebus.domain.model.Fare
import com.nixbug.entebus.domain.model.FareAttributesModel
import com.nixbug.entebus.domain.model.Route
import com.nixbug.entebus.domain.model.ServiceDetailModel
import com.nixbug.entebus.domain.model.TicketType
import com.nixbug.entebus.domain.model.Vehicle

fun ServiceWithDetails.toDomain(gson: Gson): ServiceDetailModel {
    return ServiceDetailModel(
        id = service.id,
        companyId = service.companyId,
        name = service.name,
        status = service.status,
        registrationNumber = service.registrationNumber,
        startingLandmarkId = service.startingLandmarkId,
        endingLandmarkId = service.endingLandmarkId,
        ticketMode = service.ticketMode,
        remark = service.remark,
        startingAt = service.startingAt,
        endingAt = service.endingAt,
        createdOn = service.createdOn,
        updatedOn = service.updatedOn,
        publicKey = service.publicKey,

        startingLandmarkName = service.startingLandmarkName,
        endingLandmarkName = service.endingLandmarkName,
        startingLandmarkAlias = service.startingLandmarkAlias
            ?.takeIf { it.isNotEmpty() }
            ?.split(",")
            ?: emptyList(),

        endingLandmarkAlias = service.endingLandmarkAlias
            ?.takeIf { it.isNotEmpty() }
            ?.split(",")
            ?: emptyList(),

        route = landmarks.map { landmark ->
            Route(
                serviceId = landmark.serviceId,
                landmarkId = landmark.landmarkId,
                landmarkName = landmark.landmarkName,
                landmarkAlias = landmark.aliasName
                    ?.takeIf { it.isNotEmpty() }
                    ?.split(",")
                    ?: emptyList(),
                distanceFromStart = landmark.distance,
                arrivalAt = landmark.arrivalAt,
                departureAt = landmark.departureAt
            )
        },

        fare = Fare(
            id = fare.id,
            fareId = fare.fareId,
            version = fare.version,
            name = fare.name,
            function = fare.function,
            attributes = FareAttributesModel(
                dfVersion = fare.dfVersion,
                currencyType = fare.currencyType,
                distanceUnit = fare.distanceUnit,
                ticketTypes = try {
                    fare.ticketTypesJson
                        .takeIf { it.isNotBlank() }
                        ?.let {
                            gson.fromJson(it, Array<TicketType>::class.java).toList()
                        } ?: emptyList()
                } catch (e: Exception) {
                    emptyList()
                },
                extras = emptyMap()
            )
        ),

        vehicle = Vehicle(
            id = vehicle.id,
            vehicleId = vehicle.vehicleId,
            version = vehicle.version,
            registrationNumber = vehicle.registrationNumber,
            name = vehicle.name,
            capacity = vehicle.capacity
        )
    )
}
