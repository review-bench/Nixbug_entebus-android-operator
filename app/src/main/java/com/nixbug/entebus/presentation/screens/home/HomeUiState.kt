package com.nixbug.entebus.presentation.screens.home

import android.bluetooth.BluetoothDevice

data class HomeUiState(

    val ongoingService: HomeUiModel? = null,

    val assignedServiceList: List<HomeUiModel> = emptyList(),
    val downloadedServiceList: List<HomeUiModel> = emptyList(),
    val recentServiceList: List<HomeUiModel> = emptyList(),
    val otherServiceList: List<HomeUiModel> = emptyList(),

    val searchQuery: String = "",

    val selectedTab: Int = 0,

    val pairedPrinters: List<BluetoothDevice> = emptyList(),

    val showPrinterPicker: Boolean = false,

    val isConnectingPrinter: Boolean = false,

    val serviceLoading: Boolean = false,
    val downloadingServiceId: Int? = null,
    val startingServiceId: Int? = null,

    val offset: Int = 0,
    val limit: Int = 20,
    val hasMore: Boolean = true,
    val isPaginationLoading: Boolean = false,

    val assignedOffset: Int = 0,
    val assignedLimit: Int = 10,
    val assignedHasMore: Boolean = true,
    val isAssignedPaginationLoading: Boolean = false,
    val isRefreshing: Boolean = false
)
