package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.local.room.db.dao.ServiceDao
import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.data.mapper.toDomain
import com.nixbug.entebus.data.remote.operator.api.OperatorServiceAssignmentApi
import com.nixbug.entebus.data.remote.operator.model.serviceassignment.OrderIn
import com.nixbug.entebus.data.remote.operator.model.serviceassignment.AppApiServiceAssignmentOrderBy
import com.nixbug.entebus.data.remote.utils.apiHelper
import com.nixbug.entebus.domain.repository.AssignedServiceRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject
import com.nixbug.entebus.domain.model.AssignedServiceModel
import java.time.Duration
import java.time.Instant

class AssignedServiceRepositoryImpl @Inject constructor(
    private val serviceAssignmentApi: OperatorServiceAssignmentApi,
    private val tokenManager: TokenManager,
    private val serviceDao: ServiceDao,
) : AssignedServiceRepository {

    override suspend fun fetchAssignedServices(
        offset: Int,
        limit: Int,
    ): Resource<List<AssignedServiceModel>> {
        val startingAtGe = getUtc24HoursAgo()
        val operatorId = tokenManager.operatorId
        if (operatorId == null) {
            return Resource.Error(
                message = "Session expired. Please login again."
            )
        }
        val excludedServiceIds = serviceDao.getAllServiceIdsExcludingAssigned()
        val safeExcludedServiceIds = excludedServiceIds.ifEmpty { null }
        return apiHelper(
            apiCall = {
                serviceAssignmentApi.getAssignedServices(
                    offset = offset,
                    limit = limit,
                    operatorId = operatorId,
                    serviceIdExcluding = safeExcludedServiceIds,
                    orderBy = AppApiServiceAssignmentOrderBy.starting_at,
                    orderIn = OrderIn.asc,
                    startingAtGe = startingAtGe
                )
            },
            mapper = { responseList -> responseList.map { it.toDomain() } }
        )
    }

    private fun getUtc24HoursAgo(): String {
        return Instant.now()
            .minus(Duration.ofHours(24))
            .toString()
    }
}
