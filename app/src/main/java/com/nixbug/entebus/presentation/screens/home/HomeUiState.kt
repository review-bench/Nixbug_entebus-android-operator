package com.nixbug.entebus.presentation.screens.home

import android.bluetooth.BluetoothDevice

data class HomeUiState(

    val ongoingService: HomeUiModel? = null,

    val assignedServiceList: List<HomeUiModel> = listOf(
        sampleHomeUiModel.copy(
            id = 101,
            name = "2025-12-06 7:00 AM Mayyanad -> Kollam (KL02BT9268)",
            startingAt = "2025-12-20T18:30:00Z",
            endingAt = "2025-12-06T02:00:00Z",
            registrationNumber = "KL02BT9268"
        ),
        sampleHomeUiModel.copy(
            id = 102,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            registrationNumber = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 103,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            registrationNumber = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 104,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            registrationNumber = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 105,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            registrationNumber = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 106,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            registrationNumber = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 107,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            registrationNumber = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 108,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            registrationNumber = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 109,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            registrationNumber = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 110,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            registrationNumber = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 111,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            registrationNumber = "KL02BT8234",
        ),
    ),

    val downloadedServiceList: List<HomeUiModel> = emptyList<HomeUiModel>(),

    val recentServiceList: List<HomeUiModel> = emptyList<HomeUiModel>(),

    val otherServiceList: List<HomeUiModel> = emptyList<HomeUiModel>(),

    val searchQuery: String = "",

    val selectedTab: Int = 0,

    val pairedPrinters: List<BluetoothDevice> = emptyList(),

    val showPrinterPicker: Boolean = false,

    val isConnectingPrinter: Boolean = false,

    val otherServiceLoading: Boolean=false

) {
    val filteredOtherServices: List<HomeUiModel>
        get() = otherServiceList.filter {
            it.registrationNumber.contains(searchQuery, ignoreCase = true)
        }
}
