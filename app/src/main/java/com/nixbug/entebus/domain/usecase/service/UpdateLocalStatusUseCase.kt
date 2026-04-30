package com.nixbug.entebus.domain.usecase.service

import com.nixbug.entebus.domain.repository.ServiceRepository
import javax.inject.Inject

class UpdateLocalStatusUseCase @Inject constructor(private val repository: ServiceRepository) {
    suspend operator fun invoke(
        id: Int, localStatus: String
    ) = repository.updateLocalServiceStatus(id, localStatus)
}
