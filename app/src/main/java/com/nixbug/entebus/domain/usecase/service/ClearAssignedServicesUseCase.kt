package com.nixbug.entebus.domain.usecase.service

import com.nixbug.entebus.domain.repository.ServiceRepository
import javax.inject.Inject

class ClearAssignedServicesUseCase @Inject constructor(
    private val repository: ServiceRepository
) {
    suspend operator fun invoke() {
        repository.deleteAssignedServices()
    }
}
