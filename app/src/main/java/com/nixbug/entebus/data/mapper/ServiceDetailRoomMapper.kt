package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.data.local.room.db.entity.FareEntity
import com.nixbug.entebus.data.local.room.db.entity.LandmarkEntity
import com.nixbug.entebus.data.local.room.db.entity.ServiceEntity
import com.nixbug.entebus.data.local.room.db.entity.VehicleEntity
import com.nixbug.entebus.domain.model.ServiceDetailModel
import com.google.gson.Gson

fun ServiceDetailModel.toServiceEntity() = ServiceEntity(
    id = id,
    companyId = companyId,
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
    updatedOn = updatedOn,
    publicKey = publicKey,
    startingLandmarkName = startingLandmarkName,
    endingLandmarkName = endingLandmarkName,
    startingLandmarkAlias = startingLandmarkAlias.firstOrNull(),
    endingLandmarkAlias = endingLandmarkAlias.firstOrNull()
)

fun ServiceDetailModel.toLandmarkEntities(): List<LandmarkEntity> {
    return route.map {
        LandmarkEntity(
            serviceId =it.serviceId ,
            landmarkId = it.landmarkId,
            landmarkName = it.landmarkName,
            aliasName = it.landmarkAlias.firstOrNull(),
            distance = it.distanceFromStart,
            arrivalAt = it.arrivalAt,
            departureAt = it.departureAt,
        )
    }
}

fun ServiceDetailModel.toFareEntity(gson: Gson) = FareEntity(
    id = fare.id,
    fareId = fare.fareId,
    serviceId = id,
    name = fare.name,
    function = fare.function,
    currencyType = fare.attributes.currencyType,
    distanceUnit = fare.attributes.distanceUnit,
    version = fare.version,
    ticketTypesJson = gson.toJson(fare.attributes.ticketTypes),
    dfVersion = fare.attributes.dfVersion
)

fun ServiceDetailModel.toVehicleEntity() = VehicleEntity(
    id = vehicle.id,
    vehicleId = vehicle.vehicleId,
    serviceId = id,
    name = vehicle.name,
    registrationNumber = vehicle.registrationNumber,
    capacity = vehicle.capacity,
    version = vehicle.version
)
