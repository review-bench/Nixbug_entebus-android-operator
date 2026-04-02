package com.nixbug.entebus.presentation.screens.personaldetails

data class PersonalDetailsUiState(
    val id: Int? = null,
    val username: String? = null,
    val gender: Int? = null,
    val status: Int? = null,
    val createdOn: String? = null,

    val fullName: String? = null,
    val phoneNumber: String? = null,
    val emailId: String? = null,

    val genderText: String = "N/A",
    val createdOnText: String = "N/A",
    val statusText: String = "N/A",
    val formattedPhoneNumber: String? = null,

    val editedFullName: String? = null,
    val editedGender: Int? = null,
    val editedPhoneNumber: String? = null,
    val editedEmailId: String? = null,

    val fullNameError: String? = null,
    val phoneError: String? = null,
    val emailError: String? = null,

    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
)
