package com.nixbug.entebus.data.local.room.db.converter

import androidx.room.TypeConverter
import com.nixbug.entebus.domain.model.local.SyncOperationType
import com.nixbug.entebus.domain.model.local.SyncStatus

class SyncQueueConverters {

    @TypeConverter
    fun fromOperationType(value: SyncOperationType): String {
        return value.name
    }

    @TypeConverter
    fun toOperationType(value: String): SyncOperationType {
        return SyncOperationType.valueOf(value)
    }

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String {
        return value.name
    }

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus {
        return SyncStatus.valueOf(value)
    }
}
