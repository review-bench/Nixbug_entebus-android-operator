package com.nixbug.entebus.domain.usecase

import com.nixbug.entebus.domain.model.checker.PassengerTypeCount
import com.nixbug.entebus.domain.repository.CheckerRepository
import javax.inject.Inject

class GetPassengerStatsUseCase @Inject constructor(
    private val repository: CheckerRepository
) {

    suspend operator fun invoke(
        serviceId: Int,
        fromLandmarkId: Int,
        toLandmarkId: Int
    ): List<PassengerTypeCount> {

        return repository.getPassengerCountsBetweenLandmarks(
            serviceId = serviceId,
            fromLandmarkId = fromLandmarkId,
            toLandmarkId = toLandmarkId
        )
    }
}
