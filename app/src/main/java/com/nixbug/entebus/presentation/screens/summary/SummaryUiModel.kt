package com.nixbug.entebus.presentation.screens.summary

data class SummaryUiModel(
    val id: Int,
    val serviceId: Int,
    val serviceName: String,
    val startedOn: String,
    val finishedOn: String,
    val regNO: String,
    val operatorId: Int,
    val operatorName: String,
    val collection: Int,
)
