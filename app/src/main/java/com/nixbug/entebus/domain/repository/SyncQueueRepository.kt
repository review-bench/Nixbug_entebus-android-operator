package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.PaperTicket
import com.nixbug.entebus.domain.model.ServiceUpdate
import com.nixbug.entebus.domain.model.local.SyncEvent
import kotlinx.coroutines.flow.Flow

interface SyncQueueRepository {

    suspend fun enqueuePaperTicket(request: PaperTicket)

    suspend fun enqueueServicePatch(serviceId: Int, request: ServiceUpdate)

    suspend fun triggerSync()

    suspend fun hasPendingItems(): Boolean

    fun observeSyncEvents(): Flow<SyncEvent>
}
