package com.nixbug.entebus.data.local.datastore.token

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object TokenPreferences {
    val OPERATOR_ID = intPreferencesKey("operator_id")
    val REFRESH_BEFORE = stringPreferencesKey("refresh_before")
    val CREATED_ON = stringPreferencesKey("created_on")
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val EXPIRY_TIME = longPreferencesKey("expiry_time")
}