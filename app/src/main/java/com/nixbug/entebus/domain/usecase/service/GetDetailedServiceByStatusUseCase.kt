package com.nixbug.entebus.domain.usecase.service

import com.nixbug.entebus.domain.model.ServiceDetails
import com.nixbug.entebus.domain.repository.ServiceRepository
import javax.inject.Inject


class GetDetailedServiceByStatusUseCase @Inject constructor(
    private val repository: ServiceRepository
) {

    suspend operator fun invoke(
        status: String
    ): List<ServiceDetails> {

        return repository.getServicesWithDetailsByStatus(status)
    }
}