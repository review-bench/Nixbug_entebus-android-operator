package com.nixbug.entebus.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.nixbug.entebus.data.local.datastore.token.TokenLocalDataSource
import com.nixbug.entebus.data.local.datastore.user.UserLocalDataSource
import com.nixbug.entebus.data.remote.operator.api.TokenApi
import com.nixbug.entebus.data.remote.publicapi.api.CompanyApi
import com.nixbug.entebus.data.repository.PublicCompanyRepositoryImpl
import com.nixbug.entebus.data.repository.TokenRepositoryImpl
import com.nixbug.entebus.data.repository.UserRepositoryImpl
import com.nixbug.entebus.domain.repository.PublicCompanyRepository
import com.nixbug.entebus.domain.repository.TokenRepository
import com.nixbug.entebus.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
}
