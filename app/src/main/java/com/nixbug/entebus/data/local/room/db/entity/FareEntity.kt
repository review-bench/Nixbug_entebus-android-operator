package com.nixbug.entebus.data.local.room.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fares",
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
data class FareEntity(
    @PrimaryKey
    val serviceId: Int,
    val fareId: Int,
    val name: String,
    val function: String,
    val currencyType: String,
    val distanceUnit: String,
    val version: Int,
    val ticketTypesJson: String,
    val dfVersion: Int
)
