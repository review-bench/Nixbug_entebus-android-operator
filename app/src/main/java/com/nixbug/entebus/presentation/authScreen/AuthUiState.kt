package com.nixbug.entebus.presentation.authScreen

data class AuthUiState(
    var selectedCompany: String? = null,
    val companySearchQuery: String = "",
    val userName: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val expanded: Boolean = false,
    val companyList: List<String> = listOf(
        "Company A",
        "Company B",
        "Company C",
        "Company D",
        "Company E",
        "Company F",
        "Nixbug",
        "krishna"
    )
) {
    val filteredCompanies: List<String>
        get() = if (companySearchQuery.isBlank()) companyList
        else companyList.filter {
            it.contains(companySearchQuery, ignoreCase = true)
        }
}
