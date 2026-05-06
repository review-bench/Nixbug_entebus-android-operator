package com.nixbug.entebus.data.local.room.mapper

import com.nixbug.entebus.data.local.room.db.entity.ServiceCollectionEntity
import com.nixbug.entebus.domain.model.local.CollectionSummary

private fun ServiceCollectionEntity.toSummary(): CollectionSummary {

    return CollectionSummary(
        serviceId = serviceId,
        serviceName = serviceName,
        registrationNumber = regNo,
        startedAt = startedAt,
        finishOn = finishOn,
        status = status,
        operatorId = operatorId,
        operatorName = operatorName,
        collection = totalCollection
    )
}
