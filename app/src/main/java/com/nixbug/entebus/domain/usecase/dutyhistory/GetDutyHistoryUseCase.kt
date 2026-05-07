package com.nixbug.entebus.domain.usecase.dutyhistory

import com.nixbug.entebus.domain.model.DutyHistoryModel
import com.nixbug.entebus.domain.repository.DutyHistoryRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject

class GetDutyHistoryUseCase @Inject constructor(private val repository: DutyHistoryRepository) {
    suspend operator fun invoke(
        offset: Int,
        limit: Int,
        dutyId: String
    ): Resource<List<DutyHistoryModel>> {
        return repository.getDutyHistory(offset, limit, dutyId)
    }
}
