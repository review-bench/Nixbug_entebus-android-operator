package com.nixbug.entebus.domain.usecase

import com.nixbug.entebus.domain.repository.SyncQueueRepository
import javax.inject.Inject

class GetNextTicketSequenceUseCase @Inject constructor(
    private val repository: SyncQueueRepository
) {
    suspend operator fun invoke(serviceId: Int): Int {
        return repository.getNextTicketSequence(serviceId)
    }
}
