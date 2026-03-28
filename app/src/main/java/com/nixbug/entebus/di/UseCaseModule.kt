package com.nixbug.entebus.di

import com.nixbug.entebus.data.manager.SessionManager
import com.nixbug.entebus.data.local.datastore.token.TokenLocalDataSource
import com.nixbug.entebus.data.local.datastore.user.UserLocalDataSource
import com.nixbug.entebus.domain.usecase.ClearSessionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideClearSessionUseCase(
        tokenLocalDataSource: TokenLocalDataSource,
        userLocalDataSource: UserLocalDataSource,
        sessionManager: SessionManager
    ): ClearSessionUseCase {
        return ClearSessionUseCase(tokenLocalDataSource, userLocalDataSource, sessionManager)
    }
}
