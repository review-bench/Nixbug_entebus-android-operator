package com.nixbug.entebus.data.local.room.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nixbug.entebus.data.local.room.db.entity.FareEntity
import com.nixbug.entebus.data.local.room.db.entity.LandmarkEntity
import com.nixbug.entebus.data.local.room.db.entity.ServiceEntity
import com.nixbug.entebus.data.local.room.db.entity.ServiceWithDetails
import com.nixbug.entebus.data.local.room.db.entity.VehicleEntity

@Dao
interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLandmarks(list: List<LandmarkEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFare(fare: FareEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)

    @Transaction
    suspend fun insertFullService(
        service: ServiceEntity,
        landmarks: List<LandmarkEntity>,
        fare: FareEntity,
        vehicle: VehicleEntity
    ) {
        insertService(service)
        deleteLandmarksByServiceId(service.id)
        insertLandmarks(landmarks)
        insertFare(fare)
        insertVehicle(vehicle)
    }

    @Transaction
    @Query("SELECT * FROM services WHERE id = :serviceId")
    suspend fun getServiceWithDetails(serviceId: Int): ServiceWithDetails?

    @Query("SELECT * FROM services WHERE localStatus = :status ORDER BY startingAt DESC")
    suspend fun getServicesByStatus(status: String): List<ServiceEntity>

    @Transaction
    @Query("SELECT * FROM services WHERE localStatus = :status")
    suspend fun getServicesWithDetailsByStatus(status: String): List<ServiceWithDetails>

    @Query("SELECT id FROM services")
    suspend fun getAllServiceIds(): List<Int>

    @Query("DELETE FROM landmarks WHERE serviceId = :serviceId")
    suspend fun deleteLandmarksByServiceId(serviceId: Int)

    @Query("UPDATE services SET localStatus = :status WHERE id = :serviceId")
    suspend fun updateLocalServiceStatus(serviceId: Int, status: String)

    @Query(" DELETE FROM services WHERE startingAt < :cutoffDate AND localStatus != :startedStatus")
    suspend fun deleteOldServices(cutoffDate: String, startedStatus: String)

    @Query(
        """
    DELETE FROM services 
    WHERE id NOT IN (:ids) 
    AND localStatus = :status
"""
    )
    suspend fun deleteServicesNotIn(ids: List<Int>, status: String)
}
