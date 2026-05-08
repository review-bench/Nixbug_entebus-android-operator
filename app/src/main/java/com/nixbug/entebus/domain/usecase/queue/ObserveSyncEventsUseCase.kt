package com.nixbug.entebus.domain.usecase.queue

import com.nixbug.entebus.domain.repository.SyncQueueRepository
import javax.inject.Inject

class ObserveSyncEventsUseCase @Inject constructor(
    private val repository: SyncQueueRepository
) {

    operator fun invoke() =
        repository.observeSyncEvents()
}
