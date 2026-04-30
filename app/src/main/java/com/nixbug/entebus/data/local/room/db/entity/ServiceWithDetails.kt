package com.nixbug.entebus.data.local.room.db.entity


import androidx.room.Embedded
import androidx.room.Relation

data class ServiceWithDetails(

    @Embedded val service: ServiceEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "serviceId"
    )
    val landmarks: List<LandmarkEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "serviceId"
    )
    val fare: FareEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "serviceId"
    )
    val vehicle: VehicleEntity
)
