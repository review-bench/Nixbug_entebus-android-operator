package com.nixbug.entebus.data.local.dataStore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.nixbug.entebus.data.local.dataStore.model.LocationDataEntity
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("location_prefs")

class LocationPreferences(private val context: Context) {
    companion object {
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
        val HAS_LOCATION = booleanPreferencesKey("has_location")
    }

    suspend fun saveLocation(lat: Double, lon: Double) {
        context.dataStore.edit { prefs ->
            prefs[LATITUDE] = lat
            prefs[LONGITUDE] = lon
            prefs[HAS_LOCATION] = true
        }
    }

    fun getLocation() = context.dataStore.data.map { prefs ->
        val lat = prefs[LATITUDE]
        val lon = prefs[LONGITUDE]
        val hasLocation = prefs[HAS_LOCATION] ?: false
        LocationDataEntity(lat, lon, hasLocation)
    }
}
