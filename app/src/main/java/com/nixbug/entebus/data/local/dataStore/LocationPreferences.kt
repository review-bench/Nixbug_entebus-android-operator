package com.nixbug.entebus.data.local.dataStore

import android.content.Context
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
    }

    suspend fun saveLocation(lat: Double, lon: Double) {
        context.dataStore.edit { prefs ->
            prefs[LATITUDE] = lat
            prefs[LONGITUDE] = lon
        }
    }

    fun getLocation() = context.dataStore.data.map { prefs ->
        val lat = prefs[LATITUDE]
        val lon = prefs[LONGITUDE]
        LocationDataEntity(lat, lon)
    }
}
