package com.nixbug.entebus.data.local.datastore.uipreference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UiPreferenceLocalDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun save(uiMode: String) {
        dataStore.edit { preferences -> preferences[UiPreferenceKeys.UI_MODE] = uiMode }
    }

    fun get(): Flow<String?> {
        return dataStore.data.map { it[UiPreferenceKeys.UI_MODE] }
    }

    suspend fun clear() {
        dataStore.edit { it.remove(UiPreferenceKeys.UI_MODE) }
    }
}
