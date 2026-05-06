package com.nixbug.entebus.domain.usecase

import com.nixbug.entebus.domain.model.ServiceUpdate
import com.nixbug.entebus.domain.repository.SyncQueueRepository
import javax.inject.Inject

class EnqueueServicePatchUseCase @Inject constructor(
    private val repository: SyncQueueRepository
) {
    suspend operator fun invoke(serviceId: Int, request: ServiceUpdate) {
        repository.enqueueServicePatch(
            serviceId = serviceId,
            request = request
        )
    }
}
