package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.DutyHistoryModel
import com.nixbug.entebus.utils.network.Resource

interface DutyHistoryRepository {
    suspend fun getDutyHistory(
        offset: Int,
        limit: Int,
        dutyId: String
    ): Resource<List<DutyHistoryModel>>
}
