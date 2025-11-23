package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.data.local.dataStore.LocationData
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun saveLocation(lat: Double, lon: Double)
    fun getLocation(): Flow<LocationData>
}
