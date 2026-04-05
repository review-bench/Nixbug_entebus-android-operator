package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.data.mapper.toOperatorAccountDomain
import com.nixbug.entebus.data.remote.operator.api.AccountApi
import com.nixbug.entebus.data.retro.CustomAccountApi
import com.nixbug.entebus.domain.repository.OperatorAccountRepository
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.domain.model.OperatorAccountModel
import com.nixbug.entebus.utils.network.apiHelper
import okhttp3.RequestBody
import javax.inject.Inject

class OperatorAccountRepositoryImpl @Inject constructor(
    private val accountApi: AccountApi,
    private val tokenManager: TokenManager,
    private val customAccountApi: CustomAccountApi
) : OperatorAccountRepository {

    override suspend fun fetchOperatorAccount(): Resource<List<OperatorAccountModel>> {
        val operatorId = tokenManager.operatorId

        if (operatorId == null) {
            return Resource.Error(
                message = "Session expired. Please login again."
            )
        }
        return apiHelper(
            apiCall = {
                accountApi.fetchAccountOperatorCompanyAccountGet(id = operatorId)
            },
            mapper = { responseList ->
                responseList.map { it.toOperatorAccountDomain() }
            }
        )
    }

    override suspend fun updateOperatorAccount(requestBody: RequestBody): Resource<OperatorAccountModel> {

        val operatorId = tokenManager.operatorId

        if (operatorId == null) {
            return Resource.Error("Session expired. Please login again.")
        }

        return apiHelper(
            apiCall = {
                customAccountApi.updateAccount(
                    id = operatorId,
                    body = requestBody
                )
            },
            mapper = { response ->
                response.toOperatorAccountDomain()
            }
        )
    }

}
