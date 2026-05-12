package com.nixbug.entebus.data.local.datastore.permission

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PermissionLocalDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun save(permissionJson: String) {
        dataStore.edit { preferences ->
            preferences[PermissionPreferences.PERMISSIONS] = permissionJson
        }
    }

    fun get(): Flow<String?> {
        return dataStore.data.map {
            it[PermissionPreferences.PERMISSIONS]
        }
    }

    suspend fun clear() {
        dataStore.edit {
            it.remove(PermissionPreferences.PERMISSIONS)
        }
    }
}
