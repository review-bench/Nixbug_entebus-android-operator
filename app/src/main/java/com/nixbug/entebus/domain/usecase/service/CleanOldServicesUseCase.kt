package com.nixbug.entebus.domain.usecase.service

import com.nixbug.entebus.domain.repository.ServiceRepository
import javax.inject.Inject


class CleanOldServicesUseCase @Inject constructor(
    private val repository: ServiceRepository
) {
    suspend operator fun invoke() {
        val cutoffDate = getYesterdayIso()
        repository.deleteOldServices(cutoffDate)
    }

    private fun getYesterdayIso(): String {
        val instant = java.time.Instant.now()
            .minus(1, java.time.temporal.ChronoUnit.DAYS)

        return instant.toString()
    }
}
