package com.nixbug.entebus.data.local.room.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nixbug.entebus.data.local.room.db.entity.SyncQueueEntity
import com.nixbug.entebus.domain.model.local.SyncStatus

@Dao
interface SyncQueueDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue WHERE status IN ('PENDING', 'FAILED') ORDER BY queueOrder ASC LIMIT 1")
    suspend fun getNextPending(): SyncQueueEntity?

    @Query("UPDATE sync_queue SET status = :status WHERE queueOrder = :queueOrder")
    suspend fun updateStatus(queueOrder: Long, status: SyncStatus)

    @Query("""
    DELETE FROM sync_queue
    WHERE status = 'SYNCED'
    AND (
        operationType != 'PAPER_TICKET'
        OR queueOrder NOT IN (
            SELECT MAX(queueOrder)
            FROM sync_queue
            WHERE status = 'SYNCED'
            AND operationType = 'PAPER_TICKET'
            GROUP BY serviceId
        )
    )
""")
    suspend fun clearSynced()

    @Query("SELECT COUNT(*) > 0 FROM sync_queue WHERE status IN ('PENDING', 'FAILED', 'SYNCING')")
    suspend fun hasPendingItems(): Boolean

    @Query("SELECT MAX(businessId) FROM sync_queue WHERE serviceId = :serviceId AND operationType = 'PAPER_TICKET'")
    suspend fun getLastTicketSequence(serviceId: Int): Int?
}
