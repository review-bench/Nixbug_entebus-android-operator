package com.nixbug.entebus.di

import android.content.Context
import com.nixbug.entebus.data.local.datastore.LocationPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideLocationPreferences(@ApplicationContext context: Context): LocationPreferences =
        LocationPreferences(context)
}
