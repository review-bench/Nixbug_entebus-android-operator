package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.mapper.toMaskedServiceDomain
import com.nixbug.entebus.data.remote.operator.api.ServiceApi
import com.nixbug.entebus.data.remote.utils.apiHelper
import com.nixbug.entebus.domain.model.MaskedServiceModel
import com.nixbug.entebus.domain.repository.ServiceRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject


class ServiceRepositoryImpl @Inject constructor(
    private val serviceApi: ServiceApi,
) : ServiceRepository {
    override suspend fun fetchMaskedService(): Resource<List<MaskedServiceModel>> {
        return apiHelper(
            apiCall = {
                serviceApi.getMaskedService()
            },
            mapper = { response ->
                response.map { it.toMaskedServiceDomain() }
            }
        )
    }
}