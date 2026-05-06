package com.nixbug.entebus.domain.usecase.collection

import com.nixbug.entebus.domain.repository.ServiceCollectionRepository
import javax.inject.Inject

class ObserveServiceCollectionsUseCase @Inject constructor(
    private val repository: ServiceCollectionRepository
) {
    operator fun invoke() = repository.observeCollections()
}
