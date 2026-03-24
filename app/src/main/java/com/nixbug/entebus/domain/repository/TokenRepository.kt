package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.CreateTokenModel
import com.nixbug.entebus.utils.network.Resource
import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    suspend fun createToken(
        companyId: Int,
        username: String,
        password: String,
        clientDetails: String
    ): Resource<CreateTokenModel>

    suspend fun saveToken(token: CreateTokenModel)

    fun getToken(): Flow<CreateTokenModel?>

    suspend fun clearToken()

    suspend fun revokeToken(token: String): Resource<Any>
}
