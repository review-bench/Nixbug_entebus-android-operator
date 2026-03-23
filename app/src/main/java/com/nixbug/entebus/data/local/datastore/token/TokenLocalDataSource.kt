package com.nixbug.entebus.data.local.datastore.token

import com.nixbug.entebus.domain.model.CreateTokenModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenLocalDataSource @Inject constructor(
    private val tokenDataStore: TokenLocalDataStore
) {

    suspend fun saveToken(token: CreateTokenModel) {
        val expiryTime = System.currentTimeMillis() + (token.expiresIn * 1000L)
        tokenDataStore.save(
            operatorId = token.operatorId,
            refreshBefore = token.refreshBefore,
            createdOn = token.createdOn,
            expiryTime = expiryTime,
            accessToken = token.accessToken,
            refreshToken = token.refreshToken,
        )
    }

    fun getToken(): Flow<CreateTokenModel?> {
        return tokenDataStore.get().map { prefs ->

            if (
                prefs.accessToken != null &&
                prefs.refreshToken != null &&
                prefs.expiryTime != null &&
                prefs.operatorId != null
            ) {

                val remainingTime = (prefs.expiryTime - System.currentTimeMillis()) / 1000

                CreateTokenModel(
                    operatorId = prefs.operatorId,
                    expiresIn = remainingTime.toInt(),
                    refreshBefore = prefs.refreshBefore ?: "",
                    createdOn = prefs.createdOn ?: "",
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