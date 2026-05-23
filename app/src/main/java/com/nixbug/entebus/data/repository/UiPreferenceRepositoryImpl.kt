package com.nixbug.entebus.data.repository

import com.nixbug.entebus.data.local.datastore.uipreference.UiPreferenceLocalDataSource
import com.nixbug.entebus.domain.model.local.UiMode
import com.nixbug.entebus.domain.repository.UiPreferenceRepository
import javax.inject.Inject

class UiPreferenceRepositoryImpl @Inject constructor(
    private val uiPreferenceLocalDataSource: UiPreferenceLocalDataSource
) : UiPreferenceRepository {

    override suspend fun saveUiMode(uiMode: UiMode) {
        uiPreferenceLocalDataSource.saveUiMode(uiMode)
    }

    override suspend fun getUiMode(): UiMode {
        return uiPreferenceLocalDataSource.getUiMode()
    }

    override suspend fun clearUiMode() {
        uiPreferenceLocalDataSource.clearUiMode()
    }
}
