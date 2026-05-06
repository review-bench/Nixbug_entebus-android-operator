package com.nixbug.entebus.di

import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.data.network.NetworkMonitor
import com.nixbug.entebus.domain.usecase.token.GetTokenUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AppInitializer @Inject constructor(
    private val tokenManager: TokenManager,
    private val getTokenUseCase: GetTokenUseCase,
    private val networkMonitor: NetworkMonitor
) {
    suspend fun init() {
        val token = getTokenUseCase().first()
        tokenManager.accessToken = token.accessToken
        tokenManager.refreshToken = token.refreshToken
        tokenManager.operatorId = token.operatorId
        tokenManager.expiryTime = token.expiryTime
        tokenManager.refreshBefore = token.refreshBefore
        networkMonitor.start()
    }
}
