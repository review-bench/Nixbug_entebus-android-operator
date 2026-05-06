package com.nixbug.entebus.data.repository

import com.google.gson.Gson
import com.nixbug.entebus.data.local.room.db.dao.ServiceDao
import com.nixbug.entebus.data.local.room.mapper.toDomain
import com.nixbug.entebus.data.mapper.toDomain
import com.nixbug.entebus.data.mapper.toDomainWithLandmarks
import com.nixbug.entebus.data.mapper.toFareEntity
import com.nixbug.entebus.data.mapper.toLandmarkEntities
import com.nixbug.entebus.data.mapper.toMaskedModel
import com.nixbug.entebus.data.mapper.toServiceEntity
import com.nixbug.entebus.data.mapper.toVehicleEntity
import com.nixbug.entebus.data.remote.operator.api.OperatorServiceApi
import com.nixbug.entebus.data.remote.operator.model.service.AppApiServiceOrderBy
import com.nixbug.entebus.data.remote.operator.model.service.OrderIn
import com.nixbug.entebus.data.remote.operator.model.service.ServiceStatus
import com.nixbug.entebus.data.remote.utils.apiHelper
import com.nixbug.entebus.domain.model.local.LocalServiceStatus
import com.nixbug.entebus.domain.model.MaskedServiceModel
import com.nixbug.entebus.domain.model.ServiceDetailModel
import com.nixbug.entebus.domain.model.ServiceDetails
import com.nixbug.entebus.domain.repository.LandmarkRepository
import com.nixbug.entebus.domain.repository.ServiceRepository
import com.nixbug.entebus.utils.network.Resource
import java.time.Duration
import java.time.Instant
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
        val startingAtGe = getUtc24HoursAgo()
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
                    startingAtGe = startingAtGe,
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

        val landmarkIds =
            services.flatMap { listOfNotNull(it.startingLandmarkId, it.endingLandmarkId) }
                .distinct()

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
        id: Int, localStatus: String
    ): Resource<ServiceDetailModel> {

        val result = apiHelper(
            apiCall = { serviceApi.getServiceDetails(id = id, markedAsCached = true) },
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

    override suspend fun fetchServiceDetailsForHome(
        id: Int,
        localStatus: String
    ): Resource<MaskedServiceModel> {

        val result = fetchServiceDetails(id, localStatus)

        return when (result) {
            is Resource.Success -> {
                val masked = result.data?.toMaskedModel()
                    ?: return Resource.Error("Mapping failed")

                Resource.Success(masked)
            }

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

    private suspend fun saveServiceToDb(domain: ServiceDetailModel, status: String) {
        try {
            serviceDao.insertFullService(
                service = domain.toServiceEntity(status, gson),
                landmarks = domain.toLandmarkEntities(),
                fare = domain.toFareEntity(gson),
                vehicle = domain.toVehicleEntity()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getUtc24HoursAgo(): String {
        return Instant.now()
            .minus(Duration.ofHours(24))
            .toString()
    }

    override suspend fun getLocalServicesByStatus(status: String): List<MaskedServiceModel> {
        val localData = serviceDao.getServicesByStatus(status)
        return localData.map { it.toDomain(gson) }
    }

    override suspend fun getServicesWithDetailsByStatus(status: String): List<ServiceDetails> {
        return serviceDao.getServicesWithDetailsByStatus(status).map { it.toDomain(gson) }
    }

    override suspend fun updateLocalServiceStatus(serviceId: Int, status: String) {
        serviceDao.updateLocalServiceStatus(serviceId, status)
    }

    override suspend fun deleteOldServices(cutoffDate: String) {
        serviceDao.deleteOldServices(cutoffDate, LocalServiceStatus.STARTED)
    }

    override suspend fun deleteAssignedServicesNotIn(ids: List<Int>) {
        serviceDao.deleteServicesNotIn(
            ids = ids,
            status = LocalServiceStatus.ASSIGNED
        )
    }

    override suspend fun deleteAssignedServices() {
        serviceDao.deleteServicesByStatus(status = LocalServiceStatus.ASSIGNED)
    }

    override suspend fun deleteAllServices() {
        serviceDao.deleteAllServices()
    }

    override suspend fun hasServiceWithStartedStatus(): Boolean {
        return serviceDao.hasStartedService()
    }
}
