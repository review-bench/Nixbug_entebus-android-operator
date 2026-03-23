package com.nixbug.entebus.data.local.datastore.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserLocalDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun save(
        companyId: Int,
        companyName: String,
        username: String
    ) {
        dataStore.edit { prefs ->
            prefs[UserPreferences.COMPANY_ID] = companyId
            prefs[UserPreferences.COMPANY_NAME] = companyName
            prefs[UserPreferences.USERNAME] = username
        }
    }

    fun get(): Flow<UserLocalModel> {
        return dataStore.data.map { prefs ->
            UserLocalModel(
                companyId = prefs[UserPreferences.COMPANY_ID],
                companyName = prefs[UserPreferences.COMPANY_NAME],
                username = prefs[UserPreferences.USERNAME]
            )
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}