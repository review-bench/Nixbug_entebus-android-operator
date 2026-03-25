package com.nixbug.entebus.di

import com.nixbug.entebus.data.manger.TokenManager
import com.nixbug.entebus.data.local.datastore.token.TokenLocalDataSource
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AppInitializer @Inject constructor(
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val tokenManager: TokenManager
) {
    suspend fun init() {
        val token = tokenLocalDataSource.getToken().first()
        tokenManager.accessToken = token?.accessToken
    }
}
