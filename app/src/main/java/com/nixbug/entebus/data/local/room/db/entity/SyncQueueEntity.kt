package com.nixbug.entebus.data.local.room.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nixbug.entebus.domain.model.local.SyncOperationType
import com.nixbug.entebus.domain.model.local.SyncStatus

@Entity(
    tableName = "sync_queue",
    indices = [
        Index(value = ["queueOrder"]),
        Index(value = ["status"]),
        Index(value = ["operationType", "serviceId", "businessId"], unique = true)
    ]
)
data class SyncQueueEntity(

    @PrimaryKey(autoGenerate = true)
    val queueOrder: Long = 0,

    val operationType: SyncOperationType,

    val serviceId: Int,

    val businessId: Int?,

    val payload: String,

    val status: SyncStatus = SyncStatus.PENDING,

    val createdAt: Long = System.currentTimeMillis()
)
