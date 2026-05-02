package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.AssignedServiceModel
import com.nixbug.entebus.utils.network.Resource


interface AssignedServiceRepository {
    suspend fun fetchAssignedServices(
        offset: Int,
        limit: Int
    ): Resource<List<AssignedServiceModel>>
}
