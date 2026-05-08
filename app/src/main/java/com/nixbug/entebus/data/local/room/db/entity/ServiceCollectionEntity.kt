package com.nixbug.entebus.data.local.room.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nixbug.entebus.domain.model.local.CollectionStatus

@Entity(
    tableName = "service_collection",
    indices = [
        Index(
            value = ["serviceId", "operatorId"],
            unique = true
        ),
        Index(value = ["status"])
    ]
)
data class ServiceCollectionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serviceId: Int,
    val serviceName: String,
    val regNo: String,
    val operatorId: Int,
    val operatorName: String,
    val totalCollection: Double = 0.0,
    val status: CollectionStatus = CollectionStatus.WORKING,
    val startedAt: String,
    val finishOn: String
)
