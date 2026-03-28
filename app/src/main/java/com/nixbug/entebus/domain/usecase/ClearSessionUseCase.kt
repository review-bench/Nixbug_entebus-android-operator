package com.nixbug.entebus.domain.usecase

import com.nixbug.entebus.data.manager.SessionManager
import com.nixbug.entebus.data.local.datastore.token.TokenLocalDataSource
import com.nixbug.entebus.data.local.datastore.user.UserLocalDataSource
import javax.inject.Inject

class ClearSessionUseCase @Inject constructor(
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke() {
        tokenLocalDataSource.clearToken()
        userLocalDataSource.clearUser()
        sessionManager.notifySessionExpired()
    }
}
