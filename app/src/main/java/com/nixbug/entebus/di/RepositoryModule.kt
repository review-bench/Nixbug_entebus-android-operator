package com.nixbug.entebus.di

import com.nixbug.entebus.data.repository.TokenRepositoryImpl
import com.nixbug.entebus.domain.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import org.openapitools.client.apis.TokenApi

@Module
@InstallIn(SingletonComponent::class)

object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(api: TokenApi): TokenRepository {
        return TokenRepositoryImpl(api)
    }
}
