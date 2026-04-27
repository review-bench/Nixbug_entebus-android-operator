package com.nixbug.entebus.domain.usecase.service

import com.nixbug.entebus.domain.model.MaskedServiceModel
import com.nixbug.entebus.domain.repository.ServiceRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject

class GetServiceUseCase @Inject constructor(private val repository: ServiceRepository) {
    suspend operator fun invoke(
        offset: Int = 0,
        limit: Int = 20,
        search: String? = null
    ): Resource<List<MaskedServiceModel>> =
        repository.fetchMaskedService(offset, limit, search)
}
