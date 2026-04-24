package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.data.mapper.toOperatorAccountImageDomain
import com.nixbug.entebus.data.remote.operator.api.OperatorAccountImageApi
import com.nixbug.entebus.domain.model.OperatorAccountImageModel
import com.nixbug.entebus.domain.repository.OperatorAccountImageRepository
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.data.remote.utils.apiHelper
import javax.inject.Inject

class OperatorAccountImageRepositoryImpl @Inject constructor(
    private val accountImageApi: OperatorAccountImageApi,
    private val tokenManager: TokenManager,
) : OperatorAccountImageRepository {
    override suspend fun fetchOperatorAccountImage(): Resource<List<OperatorAccountImageModel>> {
        val operatorId = tokenManager.operatorId
        if (operatorId == null) {
            return Resource.Error(
                message = "Session expired. Please login again."
            )
        }
        return apiHelper(
            apiCall = {
                accountImageApi.fetchOperatorAccountImage(operatorId = operatorId)
            },
            mapper = { responseList ->
                responseList.map { it.toOperatorAccountImageDomain() }
            }
        )
    }
}
