package com.nixbug.entebus.data.local.datastore.uipreference

import com.nixbug.entebus.domain.model.local.UiMode
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UiPreferenceLocalDataSource @Inject constructor(
    private val uiPreferenceDataStore: UiPreferenceLocalDataStore
) {

    suspend fun saveUiMode(uiMode: UiMode) {
        uiPreferenceDataStore.save(uiMode.name)
    }

    suspend fun getUiMode(): UiMode {
        val storedValue =
            uiPreferenceDataStore.get().firstOrNull()

        return runCatching {
            UiMode.valueOf(storedValue ?: UiMode.MODERN.name)
        }.getOrDefault(UiMode.MODERN)
    }

    suspend fun clearUiMode() {
        uiPreferenceDataStore.clear()
    }
}
