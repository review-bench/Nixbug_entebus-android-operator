package com.nixbug.entebus.presentation.authScreen

data class AuthUiState(
    val selectedCompany: String? = null,
    val userName: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val expanded: Boolean=false,
    val companyList: List<String> = listOf(
        "Company A", "Company B", "Company C", "Company D", "Company E", "Company F"
    )
)
