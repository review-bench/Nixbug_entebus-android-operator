package com.nixbug.entebus.data.local.room.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nixbug.entebus.data.local.room.db.entity.ServiceCollectionEntity
import com.nixbug.entebus.domain.model.local.CollectionStatus

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
    suspend fun getCollections(): List<ServiceCollectionEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM service_collection WHERE serviceId = :serviceId AND operatorId = :operatorId AND totalCollection > 0)")
    suspend fun hasCollection(serviceId: Int, operatorId: Int): Boolean

    @Query("DELETE FROM service_collection WHERE datetime(startedAt) < datetime('now', '-7 days')")
    suspend fun deleteCollectionsOlderThan7Days()

    @Query("SELECT lastTicketSequence FROM service_collection WHERE serviceId = :serviceId AND operatorId = :operatorId")
    suspend fun getLastTicketSequence(serviceId: Int, operatorId: Int): Int?

    @Query("UPDATE service_collection SET lastTicketSequence = lastTicketSequence + 1 WHERE serviceId = :serviceId AND operatorId = :operatorId")
    suspend fun incrementTicketSequence(serviceId: Int, operatorId: Int)

    @Transaction
    suspend fun getAndIncrementTicketSequence(serviceId: Int, operatorId: Int): Int {
        incrementTicketSequence(serviceId, operatorId)
        return getLastTicketSequence(serviceId, operatorId) ?: 0
    }

    @Query("SELECT lastTicketSequence FROM service_collection WHERE serviceId = :serviceId AND operatorId = :operatorId LIMIT 1")
    suspend fun getLastSequenceByServiceId(serviceId: Int, operatorId: Int): Int?
}
