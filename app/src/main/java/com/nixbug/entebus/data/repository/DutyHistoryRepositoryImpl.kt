package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.data.mapper.toDomain
import com.nixbug.entebus.data.remote.operator.api.OperatorDutyApi
import com.nixbug.entebus.data.remote.operator.model.duty.DutyStatus
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
        dutyId: String,
        status: Int?,
        date: String?
    ): Resource<List<DutyHistoryModel>> {

        val operatorId = tokenManager.operatorId
            ?: return Resource.Error(
                "Session expired. Please login again."
            )

        val statusList = status
            ?.toDutyStatus()
            ?.let(::listOf)

        val createdOnGe = date?.startOfDayUtc()
        val createdOnLe = date?.endOfDayUtc()

        return apiHelper(
            apiCall = {
                dutyHistoryApi.getDuty(
                    operatorId = operatorId,
                    offset = offset,
                    limit = limit,

                    id = dutyId.toIntOrNull(),

                    statusList = statusList,

                    createdOnGe = createdOnGe,
                    createdOnLe = createdOnLe
                )
            },
            mapper = { response ->
                response.map { it.toDomain() }
            }
        )
    }

    private fun Int.toDutyStatus(): DutyStatus? {
        return when (this) {
            1 -> DutyStatus._1
            2 -> DutyStatus._2
            3 -> DutyStatus._3
            else -> null
        }
    }
    private fun String.startOfDayUtc(): String {
        return "${this}T00:00:00Z"
    }
    private fun String.endOfDayUtc(): String {
        return "${this}T23:59:59Z"
    }
}
