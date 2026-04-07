package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.OperatorAccountModel
import com.nixbug.entebus.domain.model.OperatorAccountUpdateRequest
import com.nixbug.entebus.utils.network.Resource

interface OperatorAccountRepository {
    suspend fun fetchOperatorAccount(): Resource<List<OperatorAccountModel>>
    suspend fun updateOperatorAccount(request: OperatorAccountUpdateRequest): Resource<OperatorAccountModel>
}
