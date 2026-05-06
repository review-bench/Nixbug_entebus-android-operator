package com.nixbug.entebus.data.local.room.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nixbug.entebus.data.local.room.db.entity.ServiceCollectionEntity
import com.nixbug.entebus.domain.model.local.CollectionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceCollectionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(collections: List<ServiceCollectionEntity>)

    @Query(
        "UPDATE service_collection SET totalCollection = totalCollection + :amount WHERE serviceId = :serviceId AND operatorId = :operatorId"
    )
    suspend fun incrementCollection(serviceId: Int, operatorId: Int, amount: Double)

    @Query("UPDATE service_collection SET status = :status WHERE serviceId = :serviceId")
    suspend fun updateStatus(serviceId: Int, status: CollectionStatus)

    @Query("SELECT * FROM service_collection ORDER BY startedAt DESC")
    fun observeCollections(): Flow<List<ServiceCollectionEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM service_collection WHERE serviceId = :serviceId AND operatorId = :operatorId AND totalCollection > 0)")
    suspend fun hasCollection(serviceId: Int, operatorId: Int): Boolean
}