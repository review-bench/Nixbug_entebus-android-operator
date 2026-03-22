package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.local.datastore.token.TokenLocalDataSource
import com.nixbug.entebus.data.mapper.toTokenDomain
import com.nixbug.entebus.data.remote.operator.api.TokenApi
import com.nixbug.entebus.data.remote.operator.model.PlatformType
import com.nixbug.entebus.domain.model.CreateTokenModel
import com.nixbug.entebus.domain.repository.TokenRepository
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.utils.network.apiHelper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class TokenRepositoryImpl @Inject constructor(
    private val tokenApi: TokenApi,
    private val localDataSource: TokenLocalDataSource
) : TokenRepository {

    override suspend fun createToken(
        companyId: Int,
        username: String,
        password: String,
        clientDetails: String
    ): Resource<CreateTokenModel> {

        return apiHelper(
            apiCall = {
                tokenApi.createTokenCompanyAccountTokenPost(
                    companyId = companyId,
                    username = username,
                    password = password,
                    grantType = "password",
                    platformType = PlatformType._3,
                    clientDetails = clientDetails
                )
            },
            mapper = { response ->
                response.toTokenDomain()
            }
        )
    }

    override suspend fun saveToken(token: CreateTokenModel) {
        localDataSource.saveToken(token)
    }

    override fun getToken(): Flow<CreateTokenModel?> {
        return localDataSource.getToken()
    }

    override suspend fun clearToken() {
        localDataSource.clearToken()
    }
}
