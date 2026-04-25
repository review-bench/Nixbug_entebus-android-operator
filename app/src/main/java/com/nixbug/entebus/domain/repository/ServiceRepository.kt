package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.MaskedServiceModel
import com.nixbug.entebus.utils.network.Resource

interface ServiceRepository {
    suspend fun fetchMaskedService(): Resource<List<MaskedServiceModel>>
}