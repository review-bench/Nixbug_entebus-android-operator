package com.nixbug.entebus.data.local.room.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: Int,
    val companyId: Int,
    val name: String,
    val status: Int,
    val registrationNumber: String,
    val startingLandmarkId: Int?,
    val endingLandmarkId: Int?,
    val ticketMode: Int,
    val remark: String?,
    val startingAt: String,
    val endingAt: String,
    val createdOn: String,
    val updatedOn: String?,
    val publicKey: String,
    val startingLandmarkName: String,
    val endingLandmarkName: String,
    val startingLandmarkAlias: String?,
    val endingLandmarkAlias: String?
)
