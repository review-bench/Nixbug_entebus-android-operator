package com.nixbug.entebus.presentation.screens.ticket.uimapper

import com.nixbug.entebus.domain.model.DetailedLandmark
import com.nixbug.entebus.presentation.screens.ticket.Landmark

fun DetailedLandmark.toUiModel(): Landmark {
    return Landmark(
        id = landmarkId,
        name = landmarkName,
        distanceFromStart = distance.toFloat()
    )
}
