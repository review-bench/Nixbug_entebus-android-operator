package com.nixbug.entebus.domain.usecase.AssignedService

import com.nixbug.entebus.domain.model.AssignedServiceModel
import com.nixbug.entebus.domain.repository.AssignedServiceRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject

class GetAssignedServiceUseCase @Inject constructor(
    private val repository: AssignedServiceRepository
) {
    suspend operator fun invoke(
        offset: Int = 0,
        limit: Int = 10
    ): Resource<List<AssignedServiceModel>> {
        return repository.fetchAssignedServices(offset, limit)
    }
}