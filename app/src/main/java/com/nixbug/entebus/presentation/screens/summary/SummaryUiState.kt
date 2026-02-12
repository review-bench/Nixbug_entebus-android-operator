package com.nixbug.entebus.presentation.screens.summary

data class SummaryUiState(
    val summary: List<SummaryUiModel> = sampleSummaryData,
    val selectedServiceIds: Set<Int> = emptySet(),
    val todayServiceIds: Set<Int> = emptySet()
)
