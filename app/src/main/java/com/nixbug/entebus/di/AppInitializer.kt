package com.nixbug.entebus.di

import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.domain.usecase.token.GetTokenUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AppInitializer @Inject constructor(
    private val tokenManager: TokenManager,
    private val getTokenUseCase: GetTokenUseCase
) {
    suspend fun init() {
        val token = getTokenUseCase().first()
        tokenManager.accessToken = token.accessToken
        tokenManager.refreshToken = token.refreshToken
        tokenManager.operatorId = token.operatorId
        tokenManager.expiryTime = token.expiryTime
        tokenManager.refreshBefore = token.refreshBefore
    }
}
