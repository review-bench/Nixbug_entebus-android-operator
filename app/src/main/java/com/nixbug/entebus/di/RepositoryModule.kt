package com.nixbug.entebus.di

import com.nixbug.entebus.data.local.datastore.LocationPreferences
import com.nixbug.entebus.data.repository.LocationRepositoryImpl
import com.nixbug.entebus.domain.repository.LocationRepository
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
    fun provideLocationRepository(prefs: LocationPreferences): LocationRepository =
        LocationRepositoryImpl(prefs)
}
