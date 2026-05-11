package com.nixbug.entebus.domain.usecase.collection

import com.nixbug.entebus.domain.repository.ServiceCollectionRepository
import javax.inject.Inject

class GetServiceCollectionsUseCase @Inject constructor(
    private val repository: ServiceCollectionRepository
) {
    suspend operator fun invoke() = repository.getCollections()
}
