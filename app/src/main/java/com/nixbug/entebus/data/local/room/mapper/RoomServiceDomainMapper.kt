package com.nixbug.entebus.data.local.room.mapper

import com.google.gson.Gson
import com.nixbug.entebus.data.local.room.db.entity.ServiceWithDetails
import com.nixbug.entebus.domain.model.MaskedServiceModel

fun ServiceWithDetails.toDomain(gson: Gson): MaskedServiceModel {
    return MaskedServiceModel(
        id = service.id,
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
        startingLandmarkName = service.startingLandmarkName,
        endingLandmarkName = service.endingLandmarkName,
        startingLandmarkAlias =
            service.startingLandmarkAlias
                ?.let { gson.fromJson(it, Array<String>::class.java).toList() }
                ?: emptyList(),

        endingLandmarkAlias =
            service.endingLandmarkAlias
                ?.let { gson.fromJson(it, Array<String>::class.java).toList() }
                ?: emptyList()
    )
}
