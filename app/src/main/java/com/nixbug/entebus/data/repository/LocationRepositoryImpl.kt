package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.local.dataStore.LocationData
import com.nixbug.entebus.data.local.dataStore.LocationPreferences
import com.nixbug.entebus.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(private val prefs: LocationPreferences) :
    LocationRepository {
    override suspend fun saveLocation(lat: Double, lon: Double) {
        prefs.saveLocation(lat, lon)
    }

    override fun getLocation(): Flow<LocationData> {
        return prefs.getLocation()
    }
}
