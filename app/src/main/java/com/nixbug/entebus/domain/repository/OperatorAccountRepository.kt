package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.OperatorAccountModel
import com.nixbug.entebus.utils.network.Resource
import okhttp3.RequestBody

interface OperatorAccountRepository {
    suspend fun fetchOperatorAccount(): Resource<List<OperatorAccountModel>>
    suspend fun updateOperatorAccount(requestBody: RequestBody): Resource<OperatorAccountModel>
}
