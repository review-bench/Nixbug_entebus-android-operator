package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.MaskedServiceModel
import com.nixbug.entebus.domain.model.ServiceDetailModel
import com.nixbug.entebus.utils.network.Resource

interface ServiceRepository {
    suspend fun fetchMaskedService(offset: Int, limit: Int, search: String?): Resource<List<MaskedServiceModel>>

    suspend fun fetchServiceDetails(id: Int, localStatus: String): Resource<ServiceDetailModel>

    suspend fun fetchServiceDetailsForHome(id: Int, localStatus: String): Resource<MaskedServiceModel>

    suspend fun getLocalServicesByStatus(status: String): List<MaskedServiceModel>

    suspend fun updateLocalServiceStatus(serviceId: Int, status: String)

    suspend fun deleteOldServices(cutoffDate: String)

    suspend fun deleteAssignedServicesNotIn(ids: List<Int>)

}
