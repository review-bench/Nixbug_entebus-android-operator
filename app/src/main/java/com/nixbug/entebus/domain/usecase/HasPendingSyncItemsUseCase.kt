package com.nixbug.entebus.domain.usecase

import com.nixbug.entebus.domain.repository.SyncQueueRepository
import javax.inject.Inject

class HasPendingSyncItemsUseCase @Inject constructor(
    private val repository: SyncQueueRepository
) {

    suspend operator fun invoke(): Boolean {
        return repository.hasPendingItems()
    }
}
