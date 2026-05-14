package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.local.room.db.dao.ServiceCollectionDao
import com.nixbug.entebus.data.local.room.db.entity.ServiceCollectionEntity
import com.nixbug.entebus.domain.model.local.CollectionStatus
import com.nixbug.entebus.domain.model.local.CollectionSummary
import com.nixbug.entebus.domain.model.local.OperatorInfo
import com.nixbug.entebus.domain.repository.ServiceCollectionRepository
import javax.inject.Inject

class ServiceCollectionRepositoryImpl @Inject constructor(
    private val dao: ServiceCollectionDao
) : ServiceCollectionRepository {

    override suspend fun createSession(
        serviceId: Int,
        serviceName: String,
        regNo: String,
        startedAt: String,
        finishOn: String,
        operators: List<OperatorInfo>
    ) {

        val entities = operators.map { operator ->
            ServiceCollectionEntity(
                serviceId = serviceId,
                serviceName = serviceName,
                regNo = regNo,
                operatorId = operator.id,
                operatorName = operator.name,
                startedAt = startedAt,
                finishOn = finishOn
            )
        }

        dao.insertAll(entities)
    }

    override suspend fun addCollection(
        serviceId: Int,
        operatorId: Int,
        amount: Double
    ) {
        dao.incrementCollection(
            serviceId = serviceId,
            operatorId = operatorId,
            amount = amount
        )
        dao.updateStatus(
            serviceId = serviceId,
            status = CollectionStatus.WORKING
        )
    }

    override suspend fun updateStatus(serviceId: Int, status: CollectionStatus) {
        dao.updateStatus(serviceId = serviceId, status = status)
    }

    override suspend fun getCollections(): List<CollectionSummary> {

        return dao.getCollections()
            .map { entity ->

                CollectionSummary(
                    serviceId = entity.serviceId,
                    serviceName = entity.serviceName,
                    registrationNumber = entity.regNo,
                    startedAt = entity.startedAt,
                    finishOn = entity.finishOn,
                    status = entity.status,
                    operatorId = entity.operatorId,
                    operatorName = entity.operatorName,
                    collection = entity.totalCollection
                )
            }
    }

    override suspend fun hasCollection(serviceId: Int, operatorId: Int): Boolean {
        return dao.hasCollection(serviceId = serviceId, operatorId = operatorId)
    }

    override suspend fun deleteOldCollections() {
        dao.deleteCollectionsOlderThan7Days()
    }

    override suspend fun getNextTicketSequence(serviceId: Int, operatorId: Int): Int {
        return dao.getAndIncrementTicketSequence(serviceId = serviceId, operatorId = operatorId)
    }

    override suspend fun getLastSequenceByServiceId(serviceId: Int, operatorId: Int): Int {
        return dao.getLastSequenceByServiceId(serviceId, operatorId) ?: 0
    }
}
