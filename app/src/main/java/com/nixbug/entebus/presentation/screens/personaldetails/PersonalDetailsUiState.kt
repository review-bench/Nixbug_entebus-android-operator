package com.nixbug.entebus.presentation.screens.personaldetails

data class PersonalDetailsUiState(
    val id: Int? = null,
    val username: String? = null,
    val gender: Int? = null,
    val fullName: String? = null,
    val status: Int? = null,
    val phoneNumber: String? = null,
    val emailId: String? = null,
    val createdOn: String? = null,

    val genderText: String = "N/A",
    val createdOnText: String = "N/A",
    val statusText: String = "N/A",

    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
)
