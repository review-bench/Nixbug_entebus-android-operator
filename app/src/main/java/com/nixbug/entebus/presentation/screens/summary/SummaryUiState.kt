package com.nixbug.entebus.presentation.screens.summary

import android.bluetooth.BluetoothDevice

data class SummaryUiState(
    val summary: List<SummaryUiModel> = emptyList(),
    val selectedServiceIds: Set<Int> = emptySet(),
    val showPrinterPicker: Boolean = false,
    val pairedPrinters: List<BluetoothDevice> = emptyList(),
    val isConnectingPrinter: Boolean = false,
    val showSummaryPreviewDialog: Boolean = false,
    val companyName: String? = null,
    val isLoading: Boolean = false
)
