package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.mapper.toTokenDomain
import com.nixbug.entebus.domain.model.CreateTokenModel
import com.nixbug.entebus.domain.repository.TokenRepository
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.utils.network.apiHelper
import jakarta.inject.Inject
import org.openapitools.client.apis.TokenApi
import org.openapitools.client.models.PlatformType

class TokenRepositoryImpl @Inject constructor(
    private val tokenApi: TokenApi
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
}
