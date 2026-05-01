package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.data.remote.operator.model.serviceassignment.ServiceAssignmentSchema
import com.nixbug.entebus.utils.network.Resource


interface AssignedServiceRepository {
    suspend fun fetchAssignedServices( offset: Int,
                                       limit: Int): Resource<List<ServiceAssignmentSchema>>
}
