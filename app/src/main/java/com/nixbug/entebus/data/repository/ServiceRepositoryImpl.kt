package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.remote.operator.api.OperatorServiceApi
import com.nixbug.entebus.data.remote.utils.apiHelper
import com.nixbug.entebus.domain.model.MaskedServiceModel
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

        val landmarkResult = landmarkRepository.getLandmarksByIds(landmarkIds)

        val landmarkMap = when (landmarkResult) {
            is Resource.Success -> landmarkResult.data?.associateBy { it.id } ?: emptyMap()
            else -> emptyMap()
        }

        val finalList = services.map { service ->

            val start = landmarkMap[service.startingLandmarkId]
            val end = landmarkMap[service.endingLandmarkId]

            MaskedServiceModel(
                id = service.id,
                name = service.name,
                status = service.status,
                registrationNumber = service.registrationNumber,
                startingLandmarkId = service.startingLandmarkId,
                endingLandmarkId = service.endingLandmarkId,

                startingLandmarkName = start?.name,
                startingLandmarkAlias = start?.aliasNames,
                endingLandmarkName = end?.name,
                endingLandmarkAlias = end?.aliasNames,

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
}
