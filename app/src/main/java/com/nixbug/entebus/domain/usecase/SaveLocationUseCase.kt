package com.nixbug.entebus.domain.usecase

import com.nixbug.entebus.domain.repository.LocationRepository
import javax.inject.Inject

class SaveLocationUseCase @Inject constructor(private val repo: LocationRepository) {
    suspend operator fun invoke(lat: Double, lon: Double) {
        repo.saveLocation(lat, lon)
    }
}
