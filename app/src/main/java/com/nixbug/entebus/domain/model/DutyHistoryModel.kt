package com.nixbug.entebus.domain.model

data class DutyHistoryModel(
    val id: Int,
    val companyId: Int,
    val operatorId: Int?,
    val serviceId: Int,
    val status: Int,
    val startedOn: String?,
    val finishedOn: String?,
    val collection: String?,
    val updatedOn: String?,
    val createdOn: String
)
