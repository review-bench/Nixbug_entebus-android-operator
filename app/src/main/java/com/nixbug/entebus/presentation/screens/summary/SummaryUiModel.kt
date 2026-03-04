package com.nixbug.entebus.presentation.screens.summary

import com.nixbug.entebus.presentation.screens.home.HomeUiEvent

data class SummaryUiModel(
    val id: Int,
    val serviceId: Int,
    val serviceName: String,
    val startedOn: String,
    val finishedOn: String,
    val regNo: String,
    val operatorId: Int,
    val operatorName: String,
    val collection: Int,
)

sealed class SummaryUiEvent {
    data object OpenPrinterPicker : SummaryUiEvent()
    data object RequestBluetoothPermission : SummaryUiEvent()
    data object RequestEnableBluetooth : SummaryUiEvent()
}

data class PrintSummaryData(
    val services: List<PrintServiceData>,
    val grandTotal: String,
    val printedAt: String
)

data class PrintServiceData(
    val serviceName: String,
    val regNo: String,
    val dateTimeRange: String,
    val operators: List<Pair<String, Int>>,
    val serviceTotal: String
)
