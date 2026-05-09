package com.nixbug.entebus.presentation.screens.history

import com.nixbug.entebus.domain.model.DutyHistoryModel

data class HistoryUiState(
    val history: List<DutyHistoryModel> = emptyList(),
    val isLoading: Boolean = true,
    val isPaginationLoading: Boolean = false,

    val offset: Int = 0,
    val limit: Int = 10,

    val hasMore: Boolean = true,

    val searchQuery: String = "",
    val selectedStatus: Int? = null,
    val selectedDate: String? = null,
    val isFilterApplied: Boolean = false,
    val isFilterSelected: Boolean = false
)
