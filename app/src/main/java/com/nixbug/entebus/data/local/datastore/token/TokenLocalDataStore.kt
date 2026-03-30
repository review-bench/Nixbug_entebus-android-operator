package com.nixbug.entebus.data.local.datastore.token

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.nixbug.entebus.data.local.model.AuthTokenLocal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenLocalDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun save(
        operatorId: Int,
        refreshBefore: Long,
        createdOn: Long,
        expiryTime: Long,
        accessToken: String,
        refreshToken: String,
    ) {
        dataStore.edit { prefs ->
            prefs[TokenPreferences.OPERATOR_ID] = operatorId
            prefs[TokenPreferences.REFRESH_BEFORE] = refreshBefore
            prefs[TokenPreferences.CREATED_ON] = createdOn
            prefs[TokenPreferences.EXPIRY_TIME] = expiryTime
            prefs[TokenPreferences.ACCESS_TOKEN] = accessToken
            prefs[TokenPreferences.REFRESH_TOKEN] = refreshToken
        }
    }

    fun get(): Flow<AuthTokenLocal> {
        return dataStore.data.map { prefs ->
            AuthTokenLocal(
                operatorId = prefs[TokenPreferences.OPERATOR_ID],
                refreshBefore = prefs[TokenPreferences.REFRESH_BEFORE],
                createdOn = prefs[TokenPreferences.CREATED_ON],
                expiryTime = prefs[TokenPreferences.EXPIRY_TIME],
                accessToken = prefs[TokenPreferences.ACCESS_TOKEN],
                refreshToken = prefs[TokenPreferences.REFRESH_TOKEN]
            )
        }
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(TokenPreferences.OPERATOR_ID)
            prefs.remove(TokenPreferences.REFRESH_BEFORE)
            prefs.remove(TokenPreferences.CREATED_ON)
            prefs.remove(TokenPreferences.EXPIRY_TIME)
            prefs.remove(TokenPreferences.ACCESS_TOKEN)
            prefs.remove(TokenPreferences.REFRESH_TOKEN)
        }
    }
}
