package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.data.remote.operator.model.service.AppApiServiceFareSchema
import com.nixbug.entebus.data.remote.operator.model.service.AppApiServiceRouteSchema
import com.nixbug.entebus.data.remote.operator.model.service.AppApiServiceVehicleSchema
import com.nixbug.entebus.data.remote.operator.model.service.FareAttributes
import com.nixbug.entebus.data.remote.operator.model.service.PrivateServiceSchema
import com.nixbug.entebus.data.remote.operator.model.service.TicketTypesInAttribute
import com.nixbug.entebus.domain.model.Fare
import com.nixbug.entebus.domain.model.FareAttributesModel
import com.nixbug.entebus.domain.model.Route
import com.nixbug.entebus.domain.model.ServiceDetailModel
import com.nixbug.entebus.domain.model.TicketType
import com.nixbug.entebus.domain.model.Vehicle

fun PrivateServiceSchema.toDomain(): ServiceDetailModel {
    return ServiceDetailModel(
        id = id,
        name = name,
        status = status,
        registrationNumber = registrationNumber,
        startingLandmarkId = startingLandmarkId,
        endingLandmarkId = endingLandmarkId,
        ticketMode = ticketMode,
        remark = remark,
        startingAt = startingAt,
        endingAt = endingAt,
        createdOn = createdOn,
        fare = fare.toDomain(),
        vehicle = vehicle.toDomain(),
        route = route.map { it.toDomain() },
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

fun AppApiServiceRouteSchema.toDomain(): Route {
    return Route(
        serviceId = serviceId,
        landmarkId = landmarkId,
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
