package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.mapper.toOperatorAccountDomain
import com.nixbug.entebus.data.remote.operator.api.AccountApi
import com.nixbug.entebus.domain.repository.OperatorAccountRepository
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.domain.model.OperatorAccountModel
import com.nixbug.entebus.utils.network.apiHelper
import javax.inject.Inject

class OperatorAccountRepositoryImpl @Inject constructor(
    private val accountApi: AccountApi
) : OperatorAccountRepository {

    override suspend fun fetchOperatorAccount(): Resource<List<OperatorAccountModel>> {
        return apiHelper(
            apiCall = {
                accountApi.fetchAccountOperatorCompanyAccountGet(id = 1)
            },
            mapper = { responseList ->
                responseList.map { it.toOperatorAccountDomain() }
            }
        )
    }

}