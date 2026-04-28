package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.mapper.toDomain
import com.nixbug.entebus.data.remote.operator.api.OperatorServiceApi
import com.nixbug.entebus.data.remote.utils.apiHelper
import com.nixbug.entebus.domain.model.MaskedServiceModel
import com.nixbug.entebus.domain.model.ServiceDetailModel
import com.nixbug.entebus.domain.repository.LandmarkRepository
import com.nixbug.entebus.domain.repository.ServiceRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject


class ServiceRepositoryImpl @Inject constructor(
    private val serviceApi: OperatorServiceApi,
    private val landmarkRepository: LandmarkRepository
) : ServiceRepository {

    override suspend fun fetchMaskedService(
        offset: Int,
        limit: Int,
        search: String?
    ): Resource<List<MaskedServiceModel>> {

        val serviceResult = apiHelper(
            apiCall = {
                serviceApi.getMaskedService(
                    offset = offset,
                    limit = limit,
                    search = search
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

    override suspend fun fetchServiceDetails(id: Int): Resource<ServiceDetailModel> {
        return apiHelper(
            apiCall = {
                serviceApi.getServiceDetails(
                    id = id,
                    markedAsCached = true
                )
            },
            mapper = { it.toDomain() }
        )
    }

}
