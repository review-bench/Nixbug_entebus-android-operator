package com.nixbug.entebus.domain.usecase.collection

import com.nixbug.entebus.domain.model.local.CollectionStatus
import com.nixbug.entebus.domain.repository.ServiceCollectionRepository
import javax.inject.Inject

class UpdateServiceCollectionStatusUseCase @Inject constructor(
    private val repository: ServiceCollectionRepository
) {

    suspend operator fun invoke(
        serviceId: Int,
        status: CollectionStatus
    ) {

        repository.updateStatus(
            serviceId = serviceId,
            status = status
        )
    }
}
