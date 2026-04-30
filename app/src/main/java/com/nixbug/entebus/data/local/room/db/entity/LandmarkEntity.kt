package com.nixbug.entebus.data.local.room.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "landmarks",
    primaryKeys = ["serviceId", "landmarkId"],
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
data class LandmarkEntity(
    val serviceId: Int,
    val landmarkId: Int,
    val landmarkName: String,
    val aliasName: String?,
    val distance: Int,
    val arrivalAt: String,
    val departureAt: String
)
