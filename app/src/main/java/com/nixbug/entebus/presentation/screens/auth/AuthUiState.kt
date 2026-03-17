package com.nixbug.entebus.presentation.screens.auth

import com.nixbug.entebus.domain.model.PublicCompanyModel

data class AuthUiState(
    val selectedCompany: PublicCompanyModel? = null,
    val companySearchQuery: String = "",
    val companyListLoading: Boolean = false,
    val locationFetchLoading: Boolean = false,
    val userName: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val expanded: Boolean = false,
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val loginSuccess: Boolean = false,
    val userNameError: String? = null,
    val passwordError: String? = null,
    val companyError: String? = null,
    val companyList: List<PublicCompanyModel> = emptyList(),
    val offset: Int = 0,
    val limit: Int = 20,
    val hasMore: Boolean = true,
    val isPaginationLoading: Boolean = false,
)
