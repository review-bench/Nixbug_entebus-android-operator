package com.nixbug.entebus.domain.usecase.service

import com.nixbug.entebus.domain.model.MaskedServiceModel
import com.nixbug.entebus.domain.repository.ServiceRepository
import javax.inject.Inject

class GetLocalServicesByStatusUseCase @Inject constructor(
    private val repository: ServiceRepository
) {
    suspend operator fun invoke(status: String): List<MaskedServiceModel> {
        return repository.getLocalServicesByStatus(status)
    }
}
