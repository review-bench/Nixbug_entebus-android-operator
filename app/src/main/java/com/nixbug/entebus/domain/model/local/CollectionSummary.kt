package com.nixbug.entebus.domain.model.local

data class CollectionSummary(
    val serviceId: Int,
    val serviceName: String,
    val registrationNumber: String,
    val startedAt: String,
    val finishOn: String,
    val status: CollectionStatus,
    val operatorId: Int,
    val operatorName: String,
    val collection: Double
)

data class OperatorInfo(
    val id: Int,
    val name: String
)
