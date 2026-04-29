package com.nixbug.entebus.domain.usecase.service

import com.nixbug.entebus.domain.model.ServiceDetailModel
import com.nixbug.entebus.domain.repository.ServiceRepository
import javax.inject.Inject

class GetLocalServicesUseCase @Inject constructor(
    private val repository: ServiceRepository
) {
    suspend operator fun invoke(status: String): List<ServiceDetailModel> {
        return repository.getLocalServicesByStatus(status)
    }
}
