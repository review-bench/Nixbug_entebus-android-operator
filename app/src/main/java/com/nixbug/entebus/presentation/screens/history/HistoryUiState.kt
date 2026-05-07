package com.nixbug.entebus.presentation.screens.history

import com.nixbug.entebus.domain.model.DutyHistoryModel

data class HistoryUiState(
    val history: List<DutyHistoryModel> = emptyList(),
    val isLoading: Boolean = false,
)
