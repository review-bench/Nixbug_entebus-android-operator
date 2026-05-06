package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.data.mapper.toDomain
import com.nixbug.entebus.data.remote.operator.api.OperatorServiceAssignmentApi
import com.nixbug.entebus.data.remote.utils.apiHelper
import com.nixbug.entebus.domain.repository.AssignedServiceRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject
import com.nixbug.entebus.domain.model.AssignedServiceModel

class AssignedServiceRepositoryImpl @Inject constructor(
    private val serviceAssignmentApi: OperatorServiceAssignmentApi,
    private val tokenManager: TokenManager,
) : AssignedServiceRepository {

    override suspend fun fetchAssignedServices(
        offset: Int,
        limit: Int,
    ): Resource<List<AssignedServiceModel>> {
        val operatorId = tokenManager.operatorId
        if (operatorId == null) {
            return Resource.Error(
                message = "Session expired. Please login again."
            )
        }
        return apiHelper(
            apiCall = {
                serviceAssignmentApi.getAssignedServices(
                    offset = offset,
                    limit = limit,
                    operatorId = operatorId
                )
            },
            mapper = { responseList -> responseList.map { it.toDomain() } }
        )
    }
}
