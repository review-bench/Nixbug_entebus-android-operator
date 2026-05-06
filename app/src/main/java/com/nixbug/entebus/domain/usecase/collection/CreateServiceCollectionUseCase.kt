package com.nixbug.entebus.domain.usecase.collection

import com.nixbug.entebus.domain.model.local.OperatorInfo
import com.nixbug.entebus.domain.repository.ServiceCollectionRepository
import javax.inject.Inject

class CreateServiceCollectionUseCase @Inject constructor(
    private val repository: ServiceCollectionRepository
) {

    suspend operator fun invoke(
        serviceId: Int,
        serviceName: String,
        regNo: String,
        startedAt: String,
        finishOn: String,
        operators: List<OperatorInfo>
    ) {

        repository.createSession(
            serviceId = serviceId,
            serviceName = serviceName,
            regNo = regNo,
            startedAt = startedAt,
            finishOn = finishOn,
            operators = operators
        )
    }
}
