package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.OperatorAccountModel
import com.nixbug.entebus.utils.network.Resource

interface OperatorAccountRepository {
    suspend fun fetchOperatorAccount(): Resource<List<OperatorAccountModel>>
}
