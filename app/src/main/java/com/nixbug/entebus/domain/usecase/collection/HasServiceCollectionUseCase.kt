package com.nixbug.entebus.domain.usecase.collection

import com.nixbug.entebus.domain.repository.ServiceCollectionRepository
import javax.inject.Inject

class HasServiceCollectionUseCase @Inject constructor(
    private val repository: ServiceCollectionRepository
) {
    suspend operator fun invoke(serviceId: Int, operatorId: Int): Boolean {
        return repository.hasCollection(serviceId = serviceId, operatorId = operatorId)
    }
}