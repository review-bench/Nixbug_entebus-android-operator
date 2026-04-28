package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.MaskedServiceModel
import com.nixbug.entebus.domain.model.ServiceDetailModel
import com.nixbug.entebus.utils.network.Resource

interface ServiceRepository {
    suspend fun fetchMaskedService(
        offset: Int,
        limit: Int,
        search: String?
    ): Resource<List<MaskedServiceModel>>

    suspend fun fetchServiceDetails(id: Int): Resource<ServiceDetailModel>
}
