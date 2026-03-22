package com.nixbug.entebus.data.local.datastore.token

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun save(
        operatorId: Int,
        refreshBefore: String,
        createdOn: String,
        expiryTime: Long,
        accessToken: String,
        refreshToken: String,
    ) {
        dataStore.edit { prefs ->
            prefs[AuthPreferences.OPERATOR_ID] = operatorId
            prefs[AuthPreferences.REFRESH_BEFORE] = refreshBefore
            prefs[AuthPreferences.CREATED_ON] = createdOn
            prefs[AuthPreferences.EXPIRY_TIME] = expiryTime
            prefs[AuthPreferences.ACCESS_TOKEN] = accessToken
            prefs[AuthPreferences.REFRESH_TOKEN] = refreshToken
        }
    }

    fun get(): Flow<TokenLocalModel> {
        return dataStore.data.map { prefs ->
            TokenLocalModel(
                operatorId = prefs[AuthPreferences.OPERATOR_ID],
                refreshBefore = prefs[AuthPreferences.REFRESH_BEFORE],
                createdOn = prefs[AuthPreferences.CREATED_ON],
                expiryTime = prefs[AuthPreferences.EXPIRY_TIME],
                accessToken = prefs[AuthPreferences.ACCESS_TOKEN],
                refreshToken = prefs[AuthPreferences.REFRESH_TOKEN]
            )
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}