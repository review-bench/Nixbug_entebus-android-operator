package com.nixbug.entebus.presentation.screens.home

import com.google.gson.annotations.SerializedName

data class HomeUiModel(
    val id: Int,
    val name: String,
    val status: Int,
    val registrationNumber: String,
    val startingLandmarkId: Int?,
    val endingLandmarkId: Int?,

    val startingLandmarkName: String?,
    val startingLandmarkAlias: List<String>?,
    val endingLandmarkName: String?,
    val endingLandmarkAlias: List<String>?,

    val ticketMode: Int,
    val remark: String?,
    val startingAt: String,
    val endingAt: String,
    val updatedOn: String?,
    val createdOn: String
)

enum class CardType {
    ONGOING, ASSIGNED, DOWNLOADED, RECENT, OTHER
}

sealed class HomeUiEvent {
    data object NavigateToTicketScreen : HomeUiEvent()
    data object RequestBluetoothPermission : HomeUiEvent()
    data object RequestEnableBluetooth : HomeUiEvent()
    data object OpenPrinterPicker : HomeUiEvent()
}
