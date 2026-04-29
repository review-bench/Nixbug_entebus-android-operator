package com.nixbug.entebus.data.local.room.mapper

import com.nixbug.entebus.data.local.room.db.entity.ServiceWithDetails
import com.nixbug.entebus.domain.model.MaskedServiceModel

fun ServiceWithDetails.toDomain(): MaskedServiceModel {
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
        startingLandmarkAlias = service.startingLandmarkAlias.toString()
            .takeIf { it.isNotBlank() }
            ?.split(",")
            ?: emptyList(),
        endingLandmarkAlias = service.endingLandmarkAlias.toString()
            .takeIf { it.isNotBlank() }
            ?.split(",")
            ?: emptyList()
    )
}