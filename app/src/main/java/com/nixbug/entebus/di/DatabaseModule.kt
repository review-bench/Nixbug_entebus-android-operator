package com.nixbug.entebus.di

import android.content.Context
import androidx.room.Room
import com.nixbug.entebus.data.local.room.db.AppDatabase
import com.nixbug.entebus.data.local.room.db.dao.ServiceDao
import com.nixbug.entebus.data.local.room.db.dao.SyncQueueDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "entebus_db"
        ).build()
    }

    @Provides
    fun provideServiceDao(db: AppDatabase): ServiceDao {
        return db.serviceDao()
    }

    @Provides
    fun provideSyncQueueDao(db: AppDatabase): SyncQueueDao {
        return db.syncQueueDao()
    }
}
