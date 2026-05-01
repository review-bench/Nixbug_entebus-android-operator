package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.remote.operator.api.OperatorServiceAssignmentApi
import com.nixbug.entebus.data.remote.operator.model.serviceassignment.ServiceAssignmentSchema
import com.nixbug.entebus.data.remote.utils.apiHelper
import com.nixbug.entebus.domain.repository.AssignedServiceRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject

class AssignedServiceRepositoryImpl @Inject constructor(
    private val serviceAssignmentApi: OperatorServiceAssignmentApi
) : AssignedServiceRepository {

    override suspend fun fetchAssignedServices(
        offset: Int,
        limit: Int,
    ): Resource<List<ServiceAssignmentSchema>> {

        return apiHelper(
            apiCall = {
                serviceAssignmentApi.getAssignedServices(
                    offset = offset,
                    limit = limit
                )
            },
            mapper = { responseList ->
                responseList.map { it }
            }
        )
    }

}