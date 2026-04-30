package com.nixbug.entebus.data.repository

import com.google.gson.Gson
import com.nixbug.entebus.data.local.room.db.dao.ServiceDao
import com.nixbug.entebus.data.local.room.mapper.toDomain
import com.nixbug.entebus.data.mapper.toDomainWithLandmarks
import com.nixbug.entebus.data.mapper.toFareEntity
import com.nixbug.entebus.data.mapper.toLandmarkEntities
import com.nixbug.entebus.data.mapper.toServiceEntity
import com.nixbug.entebus.data.mapper.toVehicleEntity
import com.nixbug.entebus.data.remote.operator.api.OperatorServiceApi
import com.nixbug.entebus.data.remote.operator.model.service.AppApiServiceOrderBy
import com.nixbug.entebus.data.remote.operator.model.service.OrderIn
import com.nixbug.entebus.data.remote.operator.model.service.ServiceStatus
import com.nixbug.entebus.data.remote.utils.apiHelper
import com.nixbug.entebus.domain.model.MaskedServiceModel
import com.nixbug.entebus.domain.model.ServiceDetailModel
import com.nixbug.entebus.domain.repository.LandmarkRepository
import com.nixbug.entebus.domain.repository.ServiceRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val serviceApi: OperatorServiceApi,
    private val landmarkRepository: LandmarkRepository,
    private val serviceDao: ServiceDao,
    private val gson: Gson
) : ServiceRepository {

    override suspend fun fetchMaskedService(
        offset: Int,
        limit: Int,
        search: String?
    ): Resource<List<MaskedServiceModel>> {
        val excludedIds = serviceDao.getAllServiceIds()
        val safeExcludedIds = excludedIds.ifEmpty { null }
        val serviceResult = apiHelper(
            apiCall = {
                serviceApi.getMaskedService(
                    offset = offset,
                    limit = limit,
                    search = search,
                    orderBy = AppApiServiceOrderBy.starting_at,
                    orderIn = OrderIn.desc,
                    statusList = listOf(ServiceStatus._1, ServiceStatus._2, ServiceStatus._3),
                    idExcluding = safeExcludedIds
                )
            },
            mapper = { it }
        )

        if (serviceResult !is Resource.Success) {
            return when (serviceResult) {

                is Resource.Error -> Resource.Error(
                    message = serviceResult.message,
                    statusCode = serviceResult.statusCode
                )

                is Resource.Empty -> Resource.Empty(
                    statusCode = serviceResult.statusCode ?: 204
                )

                else -> Resource.Error("Unknown error")
            }
        }

        val services = serviceResult.data ?: emptyList()

        val landmarkIds = services.flatMap {
            listOfNotNull(it.startingLandmarkId, it.endingLandmarkId)
        }.distinct()

        val landmarkMap = if (landmarkIds.isNotEmpty()) {

            val landmarkResult = landmarkRepository.getLandmarksByIds(landmarkIds)

            when (landmarkResult) {
                is Resource.Success -> landmarkResult.data?.associateBy { it.id } ?: emptyMap()
                else -> emptyMap()
            }

        } else {
            emptyMap()
        }

        val finalList = services.map { service ->

            val start = service.startingLandmarkId?.let { landmarkMap[it] }
            val end = service.endingLandmarkId?.let { landmarkMap[it] }

            MaskedServiceModel(
                id = service.id,
                name = service.name,
                status = service.status,
                registrationNumber = service.registrationNumber,
                startingLandmarkId = service.startingLandmarkId,
                endingLandmarkId = service.endingLandmarkId,

                startingLandmarkName = start?.name ?: "N/A",
                startingLandmarkAlias = start?.aliasNames ?: emptyList(),
                endingLandmarkName = end?.name ?: "N/A",
                endingLandmarkAlias = end?.aliasNames ?: emptyList(),

                ticketMode = service.ticketMode,
                remark = service.remark,
                startingAt = service.startingAt,
                endingAt = service.endingAt,
                updatedOn = service.updatedOn,
                createdOn = service.createdOn
            )
        }

        return Resource.Success(finalList)
    }

    override suspend fun fetchServiceDetails(
        id: Int,
        localStatus: String
    ): Resource<ServiceDetailModel> {

        val result = apiHelper(
            apiCall = {
                serviceApi.getServiceDetails(
                    id = id,
                    markedAsCached = true
                )
            },
            mapper = { it }
        )

        if (result !is Resource.Success) {
            return when (result) {

                is Resource.Error -> Resource.Error(
                    message = result.message,
                    statusCode = result.statusCode
                )

                is Resource.Empty -> Resource.Empty(
                    statusCode = result.statusCode ?: 204
                )

                else -> Resource.Error("Unknown error")
            }
        }

        val service = result.data ?: return Resource.Error("No data")

        val landmarkIds = buildList {
            service.startingLandmarkId?.let { add(it) }
            service.endingLandmarkId?.let { add(it) }
            service.route.mapTo(this) { it.landmarkId }
        }.distinct()

        val landmarkMap = if (landmarkIds.isNotEmpty()) {
            when (val landmarkResult = landmarkRepository.getLandmarksByIds(landmarkIds)) {
                is Resource.Success -> landmarkResult.data?.associateBy { it.id } ?: emptyMap()
                else -> emptyMap()
            }
        } else {
            emptyMap()
        }

        val domain = service.toDomainWithLandmarks(landmarkMap)

        saveServiceToDb(domain, localStatus)

        return Resource.Success(domain)
    }

    private suspend fun saveServiceToDb(domain: ServiceDetailModel, status: String) {
        try {
            serviceDao.insertFullService(
                service = domain.toServiceEntity(status, gson),
                landmarks = domain.toLandmarkEntities(),
                fare = domain.toFareEntity(gson),
                vehicle = domain.toVehicleEntity()
            )
        } catch (e: Exception) {
            throw (e)
        }
    }

    override suspend fun getLocalServicesByStatus(status: String): List<MaskedServiceModel> {
        val localData = serviceDao.getServicesByStatus(status)
        return localData.map { it.toDomain(gson) }
    }

}
