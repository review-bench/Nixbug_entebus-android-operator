package com.nixbug.entebus.domain.repository

import com.nixbug.entebus.domain.model.local.UiMode

interface UiPreferenceRepository {

    suspend fun saveUiMode(uiMode: UiMode)

    suspend fun getUiMode(): UiMode

    suspend fun clearUiMode()
}
