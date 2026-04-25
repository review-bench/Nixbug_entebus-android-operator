package com.nixbug.entebus.domain.model

data class MaskedServiceModel(
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
