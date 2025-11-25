package com.nixbug.entebus.presentation.screens.homeScreen

import com.nixbug.entebus.presentation.screens.homeScreen.HomeUiModel

data class HomeUIState(
    val companyList: List<HomeUiModel> = listOf(
        HomeUiModel("KOLLAM -> BHARANIKAVU", "28/10/2025, 07:48 AM", "KL02BT9268"),
        HomeUiModel("KOLLAM -> BHARANIKAVU", "28/10/2025, 01:31 PM", "KL02BT9268"),
        HomeUiModel("BHARANIKAVU -> KOLLAM", "28/10/2025, 03:37 PM", "KL02BT9268"),
        HomeUiModel("KOLLAM -> MAYYANAD", "28/10/2025, 06:05 PM", "KL02BT9268"),
        HomeUiModel("KOLLAM -> MAYYANAD", "28/10/2025, 06:05 PM", "KL02BT9268"),
        HomeUiModel("KOLLAM -> MAYYANAD", "28/10/2025, 06:05 PM", "KL02BT9268"),
        HomeUiModel("KOLLAM -> MAYYANAD", "28/10/2025, 06:05 PM", "KL02BT9268"),
        HomeUiModel("KOLLAM -> MAYYANAD", "28/10/2025, 06:05 PM", "KL02BT9268")
    )
)
