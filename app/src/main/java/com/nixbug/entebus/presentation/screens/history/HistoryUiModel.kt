package com.nixbug.entebus.presentation.screens.history

import com.google.gson.annotations.SerializedName

data class HistoryUiModel(

    val id: Int,

    @SerializedName("company_id")
    val companyId: Int,

    @SerializedName("operator_id")
    val operatorId: Int,

    @SerializedName("service_id")
    val serviceId: Int,

    val status: Int,

    @SerializedName("started_on")
    val startedOn: String,

    @SerializedName("finished_on")
    val finishedOn: String?,

    val collection: Int?,

    @SerializedName("updated_on")
    val updatedOn: String?,

    @SerializedName("created_on")
    val createdOn: String
)
