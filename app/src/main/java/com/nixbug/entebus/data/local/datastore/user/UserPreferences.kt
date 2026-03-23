package com.nixbug.entebus.data.local.datastore.user

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferences {
    val COMPANY_ID = intPreferencesKey("company_id")
    val COMPANY_NAME = stringPreferencesKey("company_name")
    val USERNAME = stringPreferencesKey("username")
}