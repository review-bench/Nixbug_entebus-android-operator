package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.data.mapper.toDomain
import com.nixbug.entebus.data.remote.operator.api.OperatorDutyApi
import com.nixbug.entebus.data.remote.utils.apiHelper
import com.nixbug.entebus.domain.model.DutyHistoryModel
import com.nixbug.entebus.domain.repository.DutyHistoryRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject

class DutyHistoryRepositoryImpl @Inject constructor(
    private val dutyHistoryApi: OperatorDutyApi,
    private val tokenManager: TokenManager
) : DutyHistoryRepository {
    override suspend fun getDutyHistory(
        offset: Int,
        limit: Int,
        dutyId: String
    ): Resource<List<DutyHistoryModel>> {
        val operatorId = tokenManager.operatorId
        if (operatorId == null) {
            return Resource.Error(
                message = "Session expired. Please login again."
            )
        }
        return apiHelper(
            apiCall = {
                dutyHistoryApi.getDuty(
                    operatorId = operatorId,
                    offset = offset,
                    limit = limit,
                    id = dutyId.toIntOrNull()
                )
            },
            mapper = { response ->
                response.map { it.toDomain() }
            }
        )
    }
}
