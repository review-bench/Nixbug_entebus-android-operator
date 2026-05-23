package com.nixbug.entebus.presentation.screens.settings

import com.nixbug.entebus.domain.model.local.UiMode

data class SettingsUiState(
    val selectedUiMode: UiMode = UiMode.MODERN
)
