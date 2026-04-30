package com.nixbug.entebus.domain.usecase.service

import com.nixbug.entebus.data.remote.operator.model.serviceassignment.ServiceAssignmentSchema
import com.nixbug.entebus.domain.repository.AssignedServiceRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject


class GetAssignedServiceUseCase @Inject constructor(
    private val repository: AssignedServiceRepository
) {
    suspend operator fun invoke(): Resource<List<ServiceAssignmentSchema>> {
        return repository.fetchAssignedServices()
    }
}