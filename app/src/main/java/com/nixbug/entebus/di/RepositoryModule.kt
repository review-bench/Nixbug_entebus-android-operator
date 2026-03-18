package com.nixbug.entebus.di

import com.nixbug.entebus.data.remote.operator.api.TokenApi
import com.nixbug.entebus.data.remote.publicapi.api.CompanyApi
import com.nixbug.entebus.data.repository.PublicCompanyRepositoryImpl
import com.nixbug.entebus.data.repository.TokenRepositoryImpl
import com.nixbug.entebus.domain.repository.PublicCompanyRepository
import com.nixbug.entebus.domain.repository.TokenRepository
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
    fun provideTokenRepository(api: TokenApi): TokenRepository {
        return TokenRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun providePublicCompanyRepository(api: CompanyApi): PublicCompanyRepository {
        return PublicCompanyRepositoryImpl(api)
    }
}
