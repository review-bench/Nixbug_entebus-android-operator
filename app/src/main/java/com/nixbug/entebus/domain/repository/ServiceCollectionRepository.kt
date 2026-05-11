package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.local.CollectionStatus
import com.nixbug.entebus.domain.model.local.CollectionSummary
import com.nixbug.entebus.domain.model.local.OperatorInfo

interface ServiceCollectionRepository {

    suspend fun createSession(
        serviceId: Int,
        serviceName: String,
        regNo: String,
        startedAt: String,
        finishOn: String,
        operators: List<OperatorInfo>
    )

    suspend fun addCollection(
        serviceId: Int,
        operatorId: Int,
        amount: Double
    )

    suspend fun updateStatus(
        serviceId: Int,
        status: CollectionStatus
    )

    suspend fun getCollections(): List<CollectionSummary>

    suspend fun hasCollection(serviceId: Int, operatorId: Int): Boolean

    suspend fun deleteOldCollections()

    suspend fun getNextTicketSequence(serviceId: Int, operatorId: Int): Int
}
