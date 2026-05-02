package com.nixbug.entebus.domain.model

data class AssignedServiceModel(
    val id: Int,
    val companyId: Int,
    val serviceId: Int,
    val operatorId: Int,
    val createdOn: String,
    val updatedOn: String?
)
