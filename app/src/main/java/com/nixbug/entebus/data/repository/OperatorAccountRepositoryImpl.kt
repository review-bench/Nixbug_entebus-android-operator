package com.nixbug.entebus.data.repository

import com.google.gson.Gson
import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.data.mapper.toOperatorAccountDomain
import com.nixbug.entebus.data.remote.operator.api.OperatorAccountApi
import com.nixbug.entebus.di.SerializeNullsGson
import com.nixbug.entebus.domain.model.FieldUpdate
import com.nixbug.entebus.domain.repository.OperatorAccountRepository
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.domain.model.OperatorAccountModel
import com.nixbug.entebus.domain.model.OperatorAccountUpdateRequest
import com.nixbug.entebus.utils.network.apiHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class OperatorAccountRepositoryImpl @Inject constructor(
    private val accountApi: OperatorAccountApi,
    private val tokenManager: TokenManager,
    @SerializeNullsGson private val gson: Gson
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

    override suspend fun updateOperatorAccount(request: OperatorAccountUpdateRequest): Resource<OperatorAccountModel> {

        val operatorId = tokenManager.operatorId
        if (operatorId == null) {
            return Resource.Error("Session expired. Please login again.")
        }

        val map = mutableMapOf<String, Any?>()

        when (request.fullName) {
            is FieldUpdate.Value -> map["full_name"] = request.fullName.value
            FieldUpdate.Unchanged -> {}
        }

        when (request.emailId) {
            is FieldUpdate.Value -> map["email_id"] = request.emailId.value
            FieldUpdate.Unchanged -> {}
        }

        when (request.phoneNumber) {
            is FieldUpdate.Value -> map["phone_number"] = request.phoneNumber.value
            FieldUpdate.Unchanged -> {}
        }

        when (request.gender) {
            is FieldUpdate.Value -> map["gender"] = request.gender.value
            FieldUpdate.Unchanged -> {}
        }

        val jsonString = gson.toJson(map)
        val body = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        return apiHelper(
            apiCall = {
                accountApi.updateAccountOperatorCompanyAccountIdPatch(
                    id = operatorId,
                    body = body
                )
            },
            mapper = { it.toOperatorAccountDomain() }
        )
    }
}
