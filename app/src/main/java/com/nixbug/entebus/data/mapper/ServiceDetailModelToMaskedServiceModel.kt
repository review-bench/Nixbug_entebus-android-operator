package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.domain.model.MaskedServiceModel
import com.nixbug.entebus.domain.model.ServiceDetailModel

fun ServiceDetailModel.toMaskedModel(): MaskedServiceModel {
    return MaskedServiceModel(
        id = id,
        name = name,
        status = status,
        registrationNumber = registrationNumber,
        startingLandmarkId = startingLandmarkId,
        endingLandmarkId = endingLandmarkId,

        startingLandmarkName = startingLandmarkName,
        startingLandmarkAlias = startingLandmarkAlias,
        endingLandmarkName = endingLandmarkName,
        endingLandmarkAlias = endingLandmarkAlias,

        ticketMode = ticketMode,
        remark = remark,
        startingAt = startingAt,
        endingAt = endingAt,
        updatedOn = updatedOn,
        createdOn = createdOn
    )
}
