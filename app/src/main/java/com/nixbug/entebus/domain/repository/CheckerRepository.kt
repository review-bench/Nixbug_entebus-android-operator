package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.checker.PassengerTypeCount

interface CheckerRepository {

    suspend fun getPassengerCountsBetweenLandmarks(
        serviceId: Int,
        fromLandmarkId: Int,
        toLandmarkId: Int
    ): List<PassengerTypeCount>

}