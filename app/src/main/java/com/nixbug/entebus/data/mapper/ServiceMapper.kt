package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.data.remote.operator.model.service.ServiceSchema
import com.nixbug.entebus.domain.model.MaskedServiceModel


fun ServiceSchema.toMaskedServiceDomain(): MaskedServiceModel {
    return MaskedServiceModel(
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
        updatedOn = updatedOn,
        createdOn = createdOn,

        startingLandmarkName = null,
        startingLandmarkAlias = null,
        endingLandmarkName = null,
        endingLandmarkAlias = null,
    )
}
