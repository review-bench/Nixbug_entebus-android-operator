package com.nixbug.entebus.data.sync

import com.google.gson.Gson
import com.nixbug.entebus.data.local.room.db.dao.SyncQueueDao
import com.nixbug.entebus.data.remote.operator.api.OperatorPaperTicketApi
import com.nixbug.entebus.data.remote.operator.api.OperatorServiceApi
import com.nixbug.entebus.data.remote.operator.model.service.AppApiServiceUpdateForm
import com.nixbug.entebus.data.remote.operator.model.paperticket.AppApiPaperTicketCreateForm
import com.nixbug.entebus.domain.model.local.SyncEvent
import com.nixbug.entebus.domain.model.local.SyncOperationType
import com.nixbug.entebus.domain.model.local.SyncStatus
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class SyncQueueProcessor @Inject constructor(
    private val syncQueueDao: SyncQueueDao,
    private val paperTicketApi: OperatorPaperTicketApi,
    private val serviceApi: OperatorServiceApi,
    private val gson: Gson
) {
    private val _events = MutableSharedFlow<SyncEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<SyncEvent> = _events.asSharedFlow()
    private val mutex = Mutex()

    suspend fun processQueue() {

        mutex.withLock {
            _events.emit(SyncEvent.Started)
            while (true) {
                val item = syncQueueDao.getNextPending() ?: break

                try {
                    syncQueueDao.updateStatus(
                        queueOrder = item.queueOrder,
                        status = SyncStatus.SYNCING
                    )

                    when (item.operationType) {

                        SyncOperationType.PAPER_TICKET -> {
                            val request = gson.fromJson(
                                item.payload,
                                AppApiPaperTicketCreateForm::class.java
                            )
                            val response = paperTicketApi.createPaperTicket(request)
                            if (!response.isSuccessful) {
                                throw Exception("Unable to sync ticket. Verify your ticket printing permission or contact your owner.")
                            }
                        }

                        SyncOperationType.SERVICE_PATCH -> {
                            val request = gson.fromJson(
                                item.payload,
                                AppApiServiceUpdateForm::class.java
                            )
                            val response = serviceApi.updateService(
                                id = item.serviceId,
                                appApiServiceUpdateForm = request
                            )
                            if (!response.isSuccessful) {
                                throw Exception("Unable to sync service. Please verify your service update permission. If the issue persists, contact your owner.")
                            }
                        }
                    }

                    syncQueueDao.updateStatus(
                        queueOrder = item.queueOrder,
                        status = SyncStatus.SYNCED
                    )

                } catch (e: Exception) {

                    if (e is CancellationException) {
                        throw e
                    }

                    syncQueueDao.updateStatus(
                        queueOrder = item.queueOrder,
                        status = SyncStatus.FAILED
                    )
                    val message = when {
                        e.message?.contains("timeout", ignoreCase = true) == true ->
                            "Request timed out. Please try again."

                        e.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                            "No internet connection. Please check your network."

                        else ->
                            e.message ?: "Sync failed"
                    }
                    _events.emit(
                        SyncEvent.Failed(message)
                    )
                    return
                }
            }

            _events.emit(SyncEvent.Success)
        }
    }
}
