package com.nixbug.entebus.presentation.screens.summary

import android.bluetooth.BluetoothDevice

data class SummaryUiState(
    val summary: List<SummaryUiModel> = sampleSummaryData,
    val selectedServiceIds: Set<Int> = emptySet(),
    val showPrinterPicker: Boolean = false,
    val pairedPrinters: List<BluetoothDevice> = emptyList(),
)
