package com.nixbug.entebus.data.local.datastore.token

import com.nixbug.entebus.data.local.model.AuthTokenLocal
import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.domain.model.CreateTokenModel
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject

class TokenLocalDataSource @Inject constructor(
    private val tokenDataStore: TokenLocalDataStore,
    private val tokenManager: TokenManager
) {

    suspend fun saveToken(token: CreateTokenModel) {
        val createdOnMillis = Instant.parse(token.createdOn).toEpochMilli()
        val refreshBeforeMillis = Instant.parse(token.refreshBefore).toEpochMilli()
        val expiryTime = createdOnMillis + token.expiresIn.toLong() * 1000L
        tokenDataStore.save(
            operatorId = token.operatorId,
            refreshBefore = refreshBeforeMillis,
            createdOn = createdOnMillis,
            expiryTime = expiryTime,
            accessToken = token.accessToken,
            refreshToken = token.refreshToken,
        )
        tokenManager.accessToken = token.accessToken
        tokenManager.refreshToken = token.refreshToken
        tokenManager.operatorId = token.operatorId
        tokenManager.expiryTime = expiryTime
        tokenManager.refreshBefore = refreshBeforeMillis
        tokenManager.createdOn = createdOnMillis
    }

    fun getToken(): Flow<AuthTokenLocal> {
        return tokenDataStore.get()
    }

    suspend fun clearToken() {
        tokenDataStore.clear()
        tokenManager.clearToken()
    }
}
