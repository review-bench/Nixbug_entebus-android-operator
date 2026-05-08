package com.nixbug.entebus.data.local.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nixbug.entebus.data.local.room.db.converter.CollectionConverters
import com.nixbug.entebus.data.local.room.db.converter.SyncQueueConverters
import com.nixbug.entebus.data.local.room.db.dao.ServiceCollectionDao
import com.nixbug.entebus.data.local.room.db.dao.ServiceDao
import com.nixbug.entebus.data.local.room.db.dao.SyncQueueDao
import com.nixbug.entebus.data.local.room.db.entity.FareEntity
import com.nixbug.entebus.data.local.room.db.entity.LandmarkEntity
import com.nixbug.entebus.data.local.room.db.entity.ServiceCollectionEntity
import com.nixbug.entebus.data.local.room.db.entity.ServiceEntity
import com.nixbug.entebus.data.local.room.db.entity.SyncQueueEntity
import com.nixbug.entebus.data.local.room.db.entity.VehicleEntity

@Database(
    entities = [
        ServiceEntity::class,
        LandmarkEntity::class,
        FareEntity::class,
        VehicleEntity::class,
        SyncQueueEntity::class,
        ServiceCollectionEntity::class
    ],
    version = 1
)
@TypeConverters(SyncQueueConverters::class, CollectionConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao

    abstract fun syncQueueDao(): SyncQueueDao

    abstract fun serviceCollectionDao(): ServiceCollectionDao
}
