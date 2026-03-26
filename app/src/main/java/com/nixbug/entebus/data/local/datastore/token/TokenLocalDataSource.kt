package com.nixbug.entebus.data.local.datastore.token

import com.nixbug.entebus.data.local.model.AuthTokenLocal
import com.nixbug.entebus.domain.model.CreateTokenModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class TokenLocalDataSource @Inject constructor(
    private val tokenDataStore: TokenLocalDataStore
) {

    suspend fun saveToken(token: CreateTokenModel) {
        val createdOnMillis = Instant.parse(token.createdOn).toEpochMilli()
        val refreshBeforeMillis = Instant.parse(token.refreshBefore).toEpochMilli()
        val expiryTime = createdOnMillis + token.expiresIn * 1000
        tokenDataStore.save(
            operatorId = token.operatorId,
            refreshBefore = refreshBeforeMillis,
            createdOn = createdOnMillis,
            expiryTime = expiryTime,
            accessToken = token.accessToken,
            refreshToken = token.refreshToken,
        )
    }

    fun getToken(): Flow<AuthTokenLocal?> {
        return tokenDataStore.get().map { prefs ->

            if (
                prefs.accessToken != null &&
                prefs.refreshToken != null &&
                prefs.expiryTime != null &&
                prefs.operatorId != null
            ) {

                AuthTokenLocal(
                    operatorId = prefs.operatorId,
                    expiryTime = prefs.expiryTime,
                    refreshBefore = prefs.refreshBefore,
                    createdOn = prefs.createdOn,
                    accessToken = prefs.accessToken,
                    refreshToken = prefs.refreshToken
                )
            } else null
        }
    }

    suspend fun clearToken() {
        tokenDataStore.clear()
    }
}
