package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.data.remote.operator.model.duty.DutySchema
import com.nixbug.entebus.domain.model.DutyHistoryModel

fun DutySchema.toDomain(): DutyHistoryModel {
    return DutyHistoryModel(
        id = id,
        companyId = companyId,
        operatorId = operatorId,
        serviceId = serviceId,
        status = status,
        startedOn = startedOn,
        finishedOn = finishedOn,
        collection = collection,
        updatedOn = updatedOn,
        createdOn = createdOn
    )
}
