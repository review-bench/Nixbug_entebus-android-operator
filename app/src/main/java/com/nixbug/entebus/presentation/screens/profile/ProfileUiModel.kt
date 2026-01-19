package com.nixbug.entebus.presentation.screens.profile


import com.google.gson.annotations.SerializedName

data class ProfileUiModel(
    val id: Int,

    @SerializedName("company_id")
    val companyId: Int,

    val username: String,

    val gender: Int,

    @SerializedName("full_name")
    val fullName: String,

    val status: Int,

    @SerializedName("phone_number")
    val phoneNumber: String,

    @SerializedName("email_id")
    val emailId: String?,

    @SerializedName("updated_on")
    val updatedOn: String,

    @SerializedName("created_on")
    val createdOn: String
)
