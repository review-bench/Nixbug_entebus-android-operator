package com.nixbug.entebus.di

import com.nixbug.entebus.data.local.datastore.token.TokenLocalDataSource
import com.nixbug.entebus.data.local.datastore.user.UserLocalDataSource
import com.nixbug.entebus.data.manager.SessionManager
import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.data.remote.operator.api.AccountApi
import com.nixbug.entebus.data.remote.operator.api.TokenApi
import com.nixbug.entebus.data.remote.publicapi.api.CompanyApi
import com.nixbug.entebus.data.repository.OperatorAccountRepositoryImpl
import com.nixbug.entebus.data.repository.PublicCompanyRepositoryImpl
import com.nixbug.entebus.data.repository.SessionRepositoryImpl
import com.nixbug.entebus.data.repository.TokenRepositoryImpl
import com.nixbug.entebus.data.repository.UserRepositoryImpl
import com.nixbug.entebus.domain.repository.OperatorAccountRepository
import com.nixbug.entebus.domain.repository.PublicCompanyRepository
import com.nixbug.entebus.domain.repository.SessionRepository
import com.nixbug.entebus.domain.repository.TokenRepository
import com.nixbug.entebus.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object RepositoryModule {

    @Provides
    @Singleton
    fun provideTokenRepository(
        api: TokenApi,
        localDataSource: TokenLocalDataSource
    ): TokenRepository {
        return TokenRepositoryImpl(api, localDataSource)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        localDataSource: UserLocalDataSource
    ): UserRepository {
        return UserRepositoryImpl(localDataSource)
    }

    @Provides
    @Singleton
    fun providePublicCompanyRepository(api: CompanyApi): PublicCompanyRepository {
        return PublicCompanyRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideSessionRepository(
        sessionManager: SessionManager
    ): SessionRepository {
        return SessionRepositoryImpl(sessionManager)
    }

    @Provides
    @Singleton
    fun provideOperatorAccountRepository(
        api: AccountApi,
        tokenManager: TokenManager
    ): OperatorAccountRepository {
        return OperatorAccountRepositoryImpl(api,tokenManager)
    }
}
