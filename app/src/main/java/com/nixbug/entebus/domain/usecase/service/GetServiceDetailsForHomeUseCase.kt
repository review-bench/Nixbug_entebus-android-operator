package com.nixbug.entebus.domain.usecase.service

import com.nixbug.entebus.domain.model.MaskedServiceModel
import com.nixbug.entebus.domain.repository.ServiceRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject

class GetServiceDetailsForHomeUseCase @Inject constructor(private val repository: ServiceRepository) {
    suspend operator fun invoke(
        id: Int,
        localStatus: String
    ): Resource<MaskedServiceModel> {
        return repository.fetchServiceDetailsForHome(id, localStatus)
    }
}
