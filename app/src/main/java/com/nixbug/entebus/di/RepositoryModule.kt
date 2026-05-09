package com.nixbug.entebus.di

import com.google.gson.Gson
import com.nixbug.entebus.data.local.datastore.token.TokenLocalDataSource
import com.nixbug.entebus.data.local.datastore.user.UserLocalDataSource
import com.nixbug.entebus.data.local.room.db.dao.ServiceDao
import com.nixbug.entebus.data.local.room.db.dao.SyncQueueDao
import com.nixbug.entebus.data.manager.SessionManager
import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.data.remote.operator.api.OperatorAccountApi
import com.nixbug.entebus.data.remote.operator.api.OperatorAccountImageApi
import com.nixbug.entebus.data.remote.operator.api.OperatorDutyApi
import com.nixbug.entebus.data.remote.operator.api.OperatorLandmarkApi
import com.nixbug.entebus.data.remote.operator.api.OperatorServiceApi
import com.nixbug.entebus.data.remote.operator.api.OperatorServiceAssignmentApi
import com.nixbug.entebus.data.remote.operator.api.OperatorTokenApi
import com.nixbug.entebus.data.remote.publicapi.api.PublicCompanyApi
import com.nixbug.entebus.data.repository.LandmarkRepositoryImpl
import com.nixbug.entebus.data.repository.OperatorAccountImageRepositoryImpl
import com.nixbug.entebus.data.repository.OperatorAccountRepositoryImpl
import com.nixbug.entebus.data.repository.PublicCompanyRepositoryImpl
import com.nixbug.entebus.data.repository.AssignedServiceRepositoryImpl
import com.nixbug.entebus.data.repository.DutyHistoryRepositoryImpl
import com.nixbug.entebus.data.repository.ServiceCollectionRepositoryImpl
import com.nixbug.entebus.data.repository.ServiceRepositoryImpl
import com.nixbug.entebus.data.repository.SessionRepositoryImpl
import com.nixbug.entebus.data.repository.SyncQueueRepositoryImpl
import com.nixbug.entebus.data.repository.TokenRepositoryImpl
import com.nixbug.entebus.data.repository.UserRepositoryImpl
import com.nixbug.entebus.data.sync.SyncQueueProcessor
import com.nixbug.entebus.domain.repository.LandmarkRepository
import com.nixbug.entebus.domain.repository.OperatorAccountImageRepository
import com.nixbug.entebus.domain.repository.OperatorAccountRepository
import com.nixbug.entebus.domain.repository.PublicCompanyRepository
import com.nixbug.entebus.domain.repository.AssignedServiceRepository
import com.nixbug.entebus.domain.repository.DutyHistoryRepository
import com.nixbug.entebus.domain.repository.ServiceCollectionRepository
import com.nixbug.entebus.domain.repository.ServiceRepository
import com.nixbug.entebus.domain.repository.SessionRepository
import com.nixbug.entebus.domain.repository.SyncQueueRepository
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
        api: OperatorTokenApi,
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
    fun providePublicCompanyRepository(api: PublicCompanyApi): PublicCompanyRepository {
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
        api: OperatorAccountApi,
        tokenManager: TokenManager,
        @SerializeNullsGson gson: Gson
    ): OperatorAccountRepository {
        return OperatorAccountRepositoryImpl(api, tokenManager, gson)
    }

    @Provides
    @Singleton
    fun provideOperatorAccountImageRepository(
        api: OperatorAccountImageApi,
        tokenManager: TokenManager
    ): OperatorAccountImageRepository {
        return OperatorAccountImageRepositoryImpl(api, tokenManager)
    }

    @Provides
    @Singleton
    fun provideServiceRepository(
        api: OperatorServiceApi,
        landmarkRepository: LandmarkRepository,
        serviceDao: ServiceDao,
        gson: Gson
    ): ServiceRepository {
        return ServiceRepositoryImpl(
            api,
            landmarkRepository,
            serviceDao,
            gson
        )
    }

    @Provides
    @Singleton
    fun provideLandmarkRepository(api: OperatorLandmarkApi): LandmarkRepository {
        return LandmarkRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideAssignedServiceRepository(
        api: OperatorServiceAssignmentApi,
        tokenManager: TokenManager,
        serviceDao: ServiceDao
    ): AssignedServiceRepository {
        return AssignedServiceRepositoryImpl(api, tokenManager, serviceDao)
    }

    @Provides
    @Singleton
    fun provideSyncQueueRepository(
        syncQueueDao: SyncQueueDao,
        gson: Gson,
        processor: SyncQueueProcessor
    ): SyncQueueRepository {
        return SyncQueueRepositoryImpl(
            syncQueueDao = syncQueueDao,
            gson = gson,
            processor = processor
        )
    }

    @Provides
    @Singleton
    fun provideServiceCollectionRepository(
        impl: ServiceCollectionRepositoryImpl
    ): ServiceCollectionRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideDutyHistoryRepository(
        api: OperatorDutyApi,
        tokenManager: TokenManager
    ): DutyHistoryRepository {
        return DutyHistoryRepositoryImpl(api, tokenManager)
    }
}
