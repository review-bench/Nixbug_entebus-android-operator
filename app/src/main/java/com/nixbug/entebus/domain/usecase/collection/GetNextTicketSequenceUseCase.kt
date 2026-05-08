package com.nixbug.entebus.domain.usecase.collection

import com.nixbug.entebus.domain.repository.ServiceCollectionRepository
import javax.inject.Inject

class GetNextTicketSequenceUseCase @Inject constructor(
    private val repository: ServiceCollectionRepository
) {
    suspend operator fun invoke(serviceId: Int, operatorId: Int): Int {
        return repository.getNextTicketSequence(serviceId, operatorId)
    }
}
