package com.nixbug.entebus.data.local.room.mapper

import com.google.gson.Gson
import com.nixbug.entebus.data.local.room.db.entity.ServiceEntity
import com.nixbug.entebus.domain.model.MaskedServiceModel

fun ServiceEntity.toDomain(gson: Gson): MaskedServiceModel {
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
        createdOn = createdOn,
        updatedOn = updatedOn,
        startingLandmarkName = startingLandmarkName,
        endingLandmarkName = endingLandmarkName,

        startingLandmarkAlias =
            startingLandmarkAlias
                ?.let { gson.fromJson(it, Array<String>::class.java).toList() }
                ?: emptyList(),

        endingLandmarkAlias =
            endingLandmarkAlias
                ?.let { gson.fromJson(it, Array<String>::class.java).toList() }
                ?: emptyList()
    )
}
