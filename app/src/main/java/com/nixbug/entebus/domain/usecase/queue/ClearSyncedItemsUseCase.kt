package com.nixbug.entebus.domain.usecase.queue

import com.nixbug.entebus.domain.repository.SyncQueueRepository
import javax.inject.Inject

class ClearSyncedItemsUseCase @Inject constructor(private val repository: SyncQueueRepository) {
    suspend operator fun invoke() {
        repository.clearSyncedItems()
    }
}
