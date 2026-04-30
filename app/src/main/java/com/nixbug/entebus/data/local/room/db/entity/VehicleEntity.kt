package com.nixbug.entebus.data.local.room.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vehicles",
    foreignKeys = [
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("serviceId")]
)
data class VehicleEntity(
    @PrimaryKey
    val serviceId: Int,
    val vehicleId: Int,
    val name: String,
    val registrationNumber: String,
    val capacity: Int,
    val version: Int
)
