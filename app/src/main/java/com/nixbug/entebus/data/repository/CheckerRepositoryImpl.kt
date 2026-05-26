package com.nixbug.entebus.data.repository

import com.google.gson.Gson
import com.nixbug.entebus.data.local.room.db.dao.ServiceDao
import com.nixbug.entebus.data.local.room.db.dao.SyncQueueDao
import com.nixbug.entebus.domain.model.checker.FareTicketType
import com.nixbug.entebus.domain.repository.CheckerRepository
import com.nixbug.entebus.domain.model.checker.PassengerTypeCount
import com.nixbug.entebus.domain.model.checker.TicketPayload


class CheckerRepositoryImpl(
    private val serviceDao: ServiceDao,
    private val syncQueueDao: SyncQueueDao,
    private val gson: Gson
) : CheckerRepository {
    override suspend fun getPassengerCountsBetweenLandmarks(
        serviceId: Int,
        fromLandmarkId: Int,
        toLandmarkId: Int
    ): List<PassengerTypeCount> {

        val service = serviceDao.getServiceWithDetails(serviceId) ?: return emptyList()
        val landmarkDistanceMap = service.landmarks.associate { it.landmarkId to it.distance }
        val fromDistance = landmarkDistanceMap[fromLandmarkId] ?: return emptyList()
        val toDistance = landmarkDistanceMap[toLandmarkId] ?: return emptyList()
        if (fromDistance == toDistance) {
            return emptyList()
        }

        val checkerStart = minOf(fromDistance, toDistance)
        val checkerEnd = maxOf(fromDistance, toDistance)

        val fareTypes = runCatching {
            gson.fromJson(service.fare.ticketTypesJson, Array<FareTicketType>::class.java).toList()
        }.getOrElse { emptyList() }

        val typeNameMap = fareTypes.associate { it.id to it.name }

        val typeCountMap = mutableMapOf<Int, Int>()
        val queuedTickets = syncQueueDao.getPaperTicketsByService(serviceId)

        queuedTickets.forEach { entity ->
                val payload = runCatching {
                    gson.fromJson(
                        entity.payload,
                        TicketPayload::class.java
                    )
                }.getOrNull() ?: return@forEach

                val pickupDistance = landmarkDistanceMap[payload.ticket.pickupPoint] ?: return@forEach
                val droppingDistance = landmarkDistanceMap[payload.ticket.droppingPoint] ?: return@forEach

                val ticketStart = minOf(pickupDistance, droppingDistance)
                val ticketEnd = maxOf(pickupDistance, droppingDistance)

                val overlaps = ticketStart < checkerEnd && ticketEnd > checkerStart

                if (!overlaps) {
                    return@forEach
                }

                payload.ticket.ticketTypes.forEach {

                    if (it.count <= 0) {
                        return@forEach
                    }

                    typeCountMap[it.id] = typeCountMap.getOrDefault(it.id, 0) + it.count
                }
            }

        return typeCountMap.map {

            PassengerTypeCount(
                typeId = it.key,
                typeName = typeNameMap[it.key] ?: "Type ${it.key}",
                count = it.value
            )

        }.sortedBy {
            it.typeName
        }
    }
}
