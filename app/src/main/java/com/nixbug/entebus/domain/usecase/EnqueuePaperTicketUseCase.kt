package com.nixbug.entebus.domain.usecase

import com.nixbug.entebus.domain.model.PaperTicket
import com.nixbug.entebus.domain.repository.SyncQueueRepository
import javax.inject.Inject

class EnqueuePaperTicketUseCase @Inject constructor(
    private val repository: SyncQueueRepository
) {
    suspend operator fun invoke(request: PaperTicket) {
        repository.enqueuePaperTicket(request)
    }
}
