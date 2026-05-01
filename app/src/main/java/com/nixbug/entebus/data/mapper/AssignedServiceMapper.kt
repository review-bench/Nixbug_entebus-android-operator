package com.nixbug.entebus.data.mapper

import com.nixbug.entebus.data.remote.operator.model.serviceassignment.ServiceAssignmentSchema
import com.nixbug.entebus.domain.model.AssignedServiceModel

fun ServiceAssignmentSchema.toDomain(): AssignedServiceModel {
    return AssignedServiceModel(
        id = id,
        companyId = companyId,
        serviceId = serviceId,
        operatorId = operatorId,
        createdOn = createdOn,
        updatedOn = updatedOn
    )
}
