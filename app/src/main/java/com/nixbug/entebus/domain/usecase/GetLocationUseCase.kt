package com.nixbug.entebus.domain.usecase

import com.nixbug.entebus.domain.repository.LocationRepository
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(private val repo: LocationRepository) {
    operator fun invoke() = repo.getLocation()
}
