package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.data.remote.operator.model.service.AppApiServiceFareSchema
import com.nixbug.entebus.data.remote.operator.model.service.AppApiServiceRouteSchema
import com.nixbug.entebus.data.remote.operator.model.service.AppApiServiceVehicleSchema
import com.nixbug.entebus.data.remote.operator.model.service.FareAttributes
import com.nixbug.entebus.data.remote.operator.model.service.PrivateServiceSchema
import com.nixbug.entebus.data.remote.operator.model.service.TicketTypesInAttribute
import com.nixbug.entebus.domain.model.Fare
import com.nixbug.entebus.domain.model.FareAttributesModel
import com.nixbug.entebus.domain.model.LandmarkModel
import com.nixbug.entebus.domain.model.Route
import com.nixbug.entebus.domain.model.ServiceDetailModel
import com.nixbug.entebus.domain.model.TicketType
import com.nixbug.entebus.domain.model.Vehicle

fun PrivateServiceSchema.toDomainWithLandmarks(
    landmarkMap: Map<Int, LandmarkModel>
): ServiceDetailModel {

    val start = startingLandmarkId?.let { landmarkMap[it] }
    val end = endingLandmarkId?.let { landmarkMap[it] }

    return ServiceDetailModel(
        id = id,
        name = name,
        status = status,
        registrationNumber = registrationNumber,
        startingLandmarkId = startingLandmarkId,
        endingLandmarkId = endingLandmarkId,
        startingLandmarkName = start?.name ?: "N/A",
        startingLandmarkAlias = start?.aliasNames ?: emptyList(),
        endingLandmarkName = end?.name ?: "N/A",
        endingLandmarkAlias = end?.aliasNames ?: emptyList(),

        ticketMode = ticketMode,
        remark = remark,
        startingAt = startingAt,
        endingAt = endingAt,
        createdOn = createdOn,
        fare = fare.toDomain(),
        vehicle = vehicle.toDomain(),
        route = route.map { it.toDomainWithLandmarks(landmarkMap) },
        publicKey = publicKey
    )
}

fun AppApiServiceFareSchema.toDomain(): Fare {
    return Fare(
        id = id,
        fareId = fareId,
        version = version,
        name = name,
        attributes = attributes.toDomain(),
        function = function
    )
}

fun FareAttributes.toDomain(): FareAttributesModel {
    return FareAttributesModel(
        dfVersion = dfVersion,
        ticketTypes = ticketTypes.map { it.toDomain() },
        currencyType = currencyType,
        distanceUnit = distanceUnit,
        extra = extra
    )
}

fun TicketTypesInAttribute.toDomain(): TicketType {
    return TicketType(
        id = id,
        name = name
    )
}

fun AppApiServiceRouteSchema.toDomainWithLandmarks(
    landmarkMap: Map<Int, LandmarkModel>
): Route {

    val landmark = landmarkMap[landmarkId]

    return Route(
        serviceId = serviceId,
        landmarkId = landmarkId,
        landmarkName = landmark?.name ?: "N/A",
        landmarkAlias = landmark?.aliasNames ?: emptyList(),
        distanceFromStart = distanceFromStart,
        arrivalAt = arrivalAt,
        departureAt = departureAt
    )
}

fun AppApiServiceVehicleSchema.toDomain(): Vehicle {
    return Vehicle(
        id = id,
        vehicleId = vehicleId,
        version = version,
        registrationNumber = registrationNumber,
        name = name,
        capacity = capacity
    )
}
