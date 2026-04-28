package com.nixbug.entebus.domain.usecase.service

import com.nixbug.entebus.domain.model.ServiceDetailModel
import com.nixbug.entebus.domain.repository.ServiceRepository
import com.nixbug.entebus.utils.network.Resource
import javax.inject.Inject


class GetServiceDetailUseCase @Inject constructor(private val repository: ServiceRepository) {
    suspend operator fun invoke(
        id: Int
    ): Resource<ServiceDetailModel> = repository.fetchServiceDetails(id)
}
