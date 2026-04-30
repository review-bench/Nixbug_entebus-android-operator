package com.nixbug.entebus.data.local.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nixbug.entebus.data.local.room.db.dao.ServiceDao
import com.nixbug.entebus.data.local.room.db.entity.FareEntity
import com.nixbug.entebus.data.local.room.db.entity.LandmarkEntity
import com.nixbug.entebus.data.local.room.db.entity.ServiceEntity
import com.nixbug.entebus.data.local.room.db.entity.VehicleEntity

@Database(
    entities = [
        ServiceEntity::class,
        LandmarkEntity::class,
        FareEntity::class,
        VehicleEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao
}
