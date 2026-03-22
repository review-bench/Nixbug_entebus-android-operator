package com.nixbug.entebus.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.nixbug.entebus.data.local.datastore.token.TokenLocalDataSource
import com.nixbug.entebus.data.remote.operator.api.TokenApi
import com.nixbug.entebus.data.remote.publicapi.api.CompanyApi
import com.nixbug.entebus.data.repository.PublicCompanyRepositoryImpl
import com.nixbug.entebus.data.repository.TokenRepositoryImpl
import com.nixbug.entebus.domain.repository.PublicCompanyRepository
import com.nixbug.entebus.domain.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object RepositoryModule {

    @Module
    @InstallIn(SingletonComponent::class)
    object DataStoreModule {

        private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

        @Provides
        @Singleton
        fun provideDataStore(
            @ApplicationContext context: Context
        ): DataStore<Preferences> {
            return context.dataStore
        }
    }

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
    fun providePublicCompanyRepository(api: CompanyApi): PublicCompanyRepository {
        return PublicCompanyRepositoryImpl(api)
    }
}
