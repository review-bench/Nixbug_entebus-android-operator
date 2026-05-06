package com.nixbug.entebus.data.repository

import com.google.gson.Gson
import com.nixbug.entebus.data.local.room.db.dao.SyncQueueDao
import com.nixbug.entebus.data.local.room.db.entity.SyncQueueEntity
import com.nixbug.entebus.data.mapper.toDto
import com.nixbug.entebus.data.sync.SyncQueueProcessor
import com.nixbug.entebus.domain.model.PaperTicket
import com.nixbug.entebus.domain.model.ServiceUpdate
import com.nixbug.entebus.domain.model.local.SyncOperationType
import com.nixbug.entebus.domain.repository.SyncQueueRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class SyncQueueRepositoryImpl @Inject constructor(
    private val syncQueueDao: SyncQueueDao,
    private val gson: Gson,
    private val processor: SyncQueueProcessor
) : SyncQueueRepository {

    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override suspend fun enqueuePaperTicket(request: PaperTicket) {

        val entity = SyncQueueEntity(
            operationType = SyncOperationType.PAPER_TICKET,
            serviceId = request.ticket.serviceId,
            businessId = request.ticket.id,
            payload = gson.toJson(
                request.toDto()
            )
        )

        syncQueueDao.insert(entity)

        triggerSync()
    }

    override suspend fun enqueueServicePatch(serviceId: Int, request: ServiceUpdate) {

        val entity = SyncQueueEntity(
            operationType = SyncOperationType.SERVICE_PATCH,
            serviceId = serviceId,
            businessId = null,
            payload = gson.toJson(
                request.toDto()
            )
        )

        syncQueueDao.insert(entity)

        triggerSync()
    }

    override suspend fun triggerSync() {
        syncScope.launch { processor.processQueue() }
    }

    override suspend fun getNextTicketSequence(serviceId: Int): Int {
        val lastSequence = syncQueueDao.getLastTicketSequence(serviceId)
        return (lastSequence ?: 0) + 1
    }

    override suspend fun hasPendingItems(): Boolean {
        return syncQueueDao.hasPendingItems()
    }

    override fun observeSyncEvents() = processor.events
}