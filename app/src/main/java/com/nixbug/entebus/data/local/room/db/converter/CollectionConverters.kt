package com.nixbug.entebus.data.local.room.db.converter

import androidx.room.TypeConverter
import com.nixbug.entebus.domain.model.local.CollectionStatus

class CollectionConverters {

    @TypeConverter
    fun fromStatus(status: CollectionStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): CollectionStatus {
        return CollectionStatus.valueOf(value)
    }
}
