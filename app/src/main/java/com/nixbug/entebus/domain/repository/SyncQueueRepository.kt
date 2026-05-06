package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.PaperTicket
import com.nixbug.entebus.domain.model.ServiceUpdate

interface SyncQueueRepository {

    suspend fun enqueuePaperTicket(request: PaperTicket)

    suspend fun enqueueServicePatch(serviceId: Int, request: ServiceUpdate)

    suspend fun getNextTicketSequence(serviceId: Int): Int

    suspend fun hasPendingItems(): Boolean
}
