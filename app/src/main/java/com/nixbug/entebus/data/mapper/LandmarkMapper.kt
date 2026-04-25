package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.data.remote.operator.model.landmark.LandmarkSchema
import com.nixbug.entebus.domain.model.LandmarkModel

fun LandmarkSchema.toLandmarkDomain(): LandmarkModel {
    return LandmarkModel(
        id = id,
        name = name,
        aliasNames = aliasNames

    )
}