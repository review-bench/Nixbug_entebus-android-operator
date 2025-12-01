package com.nixbug.entebus.presentation.screens.homeScreen

data class HomeUIState(
    val currentService: HomeUiModel? = null,

    val assignedServiceList: List<HomeUiModel> = listOf(
        HomeUiModel(2,"KOLLAM -> SASHTHAMKOTTA", "28/10/2025", "08:10 AM", "KL02BT9268"),
        HomeUiModel(3,"KOLLAM -> PUNALUR", "28/10/2025", "12:00 PM", "KL02BT9268"),
    ),

    val serviceList: List<HomeUiModel> = listOf(
        HomeUiModel(4,"KOLLAM -> MAYYANAD", "28/10/2025", "7:48 AM", "KL02BT9268"),
        HomeUiModel(5,"KOLLAM -> PARAVUR", "28/10/2025", "9:15 AM", "KL02BT9268"),
        HomeUiModel(6,"KOLLAM -> KUNDARA", "28/10/2025", "11:00 AM", "KL02BT9268"),
        HomeUiModel(7,"KOLLAM -> CHATHANNOOR", "28/10/2025", "01:45 PM", "KL02BT9268"),
        HomeUiModel(8,"KOLLAM -> KARUNAGAPPALLY", "28/10/2025", "04:30 PM", "KL02BT9268")
    )
)
