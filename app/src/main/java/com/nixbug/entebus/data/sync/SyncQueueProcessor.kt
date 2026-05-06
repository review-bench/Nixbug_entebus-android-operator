package com.nixbug.entebus.data.sync

import com.google.gson.Gson
import com.nixbug.entebus.data.local.room.db.dao.SyncQueueDao
import com.nixbug.entebus.data.remote.operator.api.OperatorPaperTicketApi
import com.nixbug.entebus.data.remote.operator.api.OperatorServiceApi
import com.nixbug.entebus.data.remote.operator.model.service.AppApiServiceUpdateForm
import com.nixbug.entebus.data.remote.operator.model.paperticket.AppApiPaperTicketCreateForm
import com.nixbug.entebus.domain.model.local.SyncOperationType
import com.nixbug.entebus.domain.model.local.SyncStatus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncQueueProcessor @Inject constructor(
    private val syncQueueDao: SyncQueueDao,
    private val paperTicketApi: OperatorPaperTicketApi,
    private val serviceApi: OperatorServiceApi,
    private val gson: Gson
) {

    private val mutex = Mutex()

    suspend fun processQueue() {

        mutex.withLock {

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
                                throw Exception("Ticket sync failed")
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
                                throw Exception("Patch sync failed")
                            }
                        }
                    }

                    syncQueueDao.updateStatus(
                        queueOrder = item.queueOrder,
                        status = SyncStatus.SYNCED
                    )

                } catch (e: Exception) {

                    if (e is kotlinx.coroutines.CancellationException) {
                        throw e
                    }

                    syncQueueDao.updateStatus(
                        queueOrder = item.queueOrder,
                        status = SyncStatus.FAILED
                    )

                    break
                }
            }
        }
    }
}
