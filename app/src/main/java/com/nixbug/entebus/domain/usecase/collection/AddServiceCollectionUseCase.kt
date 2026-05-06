package com.nixbug.entebus.domain.usecase.collection

import com.nixbug.entebus.domain.repository.ServiceCollectionRepository
import javax.inject.Inject

class AddServiceCollectionUseCase @Inject constructor(
    private val repository: ServiceCollectionRepository
) {

    suspend operator fun invoke(
        serviceId: Int,
        operatorId: Int,
        amount: Double
    ) {

        repository.addCollection(
            serviceId = serviceId,
            operatorId = operatorId,
            amount = amount
        )
    }
}