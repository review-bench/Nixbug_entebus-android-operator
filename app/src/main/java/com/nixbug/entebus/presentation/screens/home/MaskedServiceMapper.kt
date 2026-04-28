package com.nixbug.entebus.presentation.screens.home

import com.nixbug.entebus.domain.model.MaskedServiceModel

fun MaskedServiceModel.toHomeUiModel(): HomeUiModel {
    return HomeUiModel(
        id = id,
        name = name,
        status = status,
        ticketMode = ticketMode,
        remark = remark,
        startingAt = startingAt,
        endingAt = endingAt,
        updatedOn = updatedOn,
        createdOn = createdOn,
        registrationNumber = registrationNumber,
        startingLandmarkId = startingLandmarkId,
        endingLandmarkId = endingLandmarkId,
        startingLandmarkName = startingLandmarkName,
        startingLandmarkAlias = startingLandmarkAlias,
        endingLandmarkName = endingLandmarkName,
        endingLandmarkAlias = endingLandmarkAlias
    )
}
