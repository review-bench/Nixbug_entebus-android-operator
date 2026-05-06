package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.local.CollectionStatus
import com.nixbug.entebus.domain.model.local.CollectionSummary
import com.nixbug.entebus.domain.model.local.OperatorInfo
import kotlinx.coroutines.flow.Flow

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

    fun observeCollections(): Flow<List<CollectionSummary>>

    suspend fun hasCollection(serviceId: Int, operatorId: Int): Boolean
}