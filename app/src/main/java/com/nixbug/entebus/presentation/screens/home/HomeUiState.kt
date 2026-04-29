package com.nixbug.entebus.presentation.screens.home

import android.bluetooth.BluetoothDevice

data class HomeUiState(

    val ongoingService: HomeUiModel? = null,

    val assignedServiceList: List<HomeUiModel> = emptyList<HomeUiModel>(),

    val downloadedServiceList: List<HomeUiModel> = emptyList<HomeUiModel>(),

    val recentServiceList: List<HomeUiModel> = emptyList<HomeUiModel>(),

    val otherServiceList: List<HomeUiModel> = emptyList<HomeUiModel>(),

    val searchQuery: String = "",

    val selectedTab: Int = 0,

    val pairedPrinters: List<BluetoothDevice> = emptyList(),

    val showPrinterPicker: Boolean = false,

    val isConnectingPrinter: Boolean = false,

    val otherServiceLoading: Boolean = false,

    val offset: Int = 0,
    val limit: Int = 20,
    val hasMore: Boolean = true,
    val isPaginationLoading: Boolean = false,
)
