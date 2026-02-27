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
            regNo = "KL02BT9268"
        ),
        sampleHomeUiModel.copy(
            id = 102,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            regNo = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 103,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            regNo = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 104,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            regNo = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 105,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            regNo = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 106,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            regNo = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 107,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            regNo = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 108,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            regNo = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 109,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            regNo = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 110,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            regNo = "KL02BT8234",
        ),
        sampleHomeUiModel.copy(
            id = 111,
            name = "2025-12-06 10:00 AM Kottarakkara -> Kollam (KL02BT8234)",
            startingAt = "2025-12-07T06:30:00Z",
            endingAt = "2025-12-06T05:10:00Z",
            regNo = "KL02BT8234",
        ),
    ),

    val downloadedServiceList: List<HomeUiModel> = listOf(),

    val recentServiceList: List<HomeUiModel> = listOf(),

    val otherServiceList: List<HomeUiModel> = listOf(
        sampleHomeUiModel.copy(
            id = 201,
            name = "2025-12-06 06:30 AM Kollam -> Paravur (KL02BT1111)",
            startingAt = "2025-12-20T16:30:00Z",
            endingAt = "2025-12-06T01:40:00Z",
            regNo = "KL02BT1111",
            route = sampleHomeUiModel.route.copy(
                name = "06:30 AM Kollam to Paravur"
            )
        ),
        sampleHomeUiModel.copy(
            id = 202,
            name = "2025-12-06 08:00 AM Kollam -> Chathannoor (KL02BT2222)",
            startingAt = "2025-12-08T16:30:00Z",
            endingAt = "2025-12-06T03:10:00Z",
            regNo = "KL02BT2222",
            route = sampleHomeUiModel.route.copy(
                name = "08:00 AM Kollam to Chathannoor"
            )
        ),
        sampleHomeUiModel.copy(
            id = 203,
            name = "2025-12-06 09:15 AM Kollam -> Punalur (KL02BT3333)",
            startingAt = "2025-12-09T16:30:00Z",
            endingAt = "2025-12-06T04:50:00Z",
            regNo = "KL02BT3333",
            route = sampleHomeUiModel.route.copy(
                name = "09:15 AM Kollam to Punalur"
            )
        ),
        sampleHomeUiModel.copy(
            id = 204,
            name = "2025-12-06 11:00 AM Kollam -> Kottarakkara (KL02BT4444)",
            startingAt = "2025-12-06T05:30:00Z",
            endingAt = "2025-12-06T06:20:00Z",
            regNo = "KL02BT4444",
            route = sampleHomeUiModel.route.copy(
                name = "11:00 AM Kollam to Kottarakkara"
            )
        ),
        sampleHomeUiModel.copy(
            id = 205,
            name = "2025-12-06 04:30 PM Kollam -> Karunagappally (KL02BT5555)",
            startingAt = "2025-12-06T11:00:00Z",
            endingAt = "2025-12-06T12:00:00Z",
            regNo = "KL02BT5555",
            route = sampleHomeUiModel.route.copy(
                name = "04:30 PM Kollam to Karunagappally"
            )
        ),
        sampleHomeUiModel.copy(
            id = 206,
            name = "2025-12-06 04:30 PM Kottayam-> Karunagappally (KL02BT5555)",
            startingAt = "2025-12-06T11:00:00Z",
            endingAt = "2025-12-06T12:00:00Z",
            regNo = "KL02BT5555",
            route = sampleHomeUiModel.route.copy(
                name = "04:30 PM Kollam to Karunagappally"
            )
        ),
    ),

    val searchQuery: String = "",

    val selectedTab: Int = 0,
    
    val pairedPrinters: List<BluetoothDevice> = emptyList(),

    val showPrinterPicker: Boolean = false,

    val selectedPrinterAddress: String? = null,

) {
    val filteredOtherServices: List<HomeUiModel>
        get() = otherServiceList.filter {
            it.regNo.contains(searchQuery, ignoreCase = true)
        }
}
