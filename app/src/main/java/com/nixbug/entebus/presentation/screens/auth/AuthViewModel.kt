package com.nixbug.entebus.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nixbug.entebus.BuildConfig
import com.nixbug.entebus.domain.model.PublicCompanyModel
import com.nixbug.entebus.domain.usecase.CreateTokenUseCase
import com.nixbug.entebus.domain.usecase.FetchPublicCompanyUseCase
import com.nixbug.entebus.utils.LocationHelper
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val locationHelper: LocationHelper,
    private val createTokenUseCase: CreateTokenUseCase,
    private val fetchPublicCompanyUseCase: FetchPublicCompanyUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    private var isFetching = false

    fun fetchLocation(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (isFetching) {
            onError("Location fetch already in progress")
            return
        }
        isFetching = true
        _uiState.update {
            it.copy(
                locationFetchLoading = true
            )
        }

        try {
            locationHelper.fetchCurrentLocation(
                onSuccess = { loc ->
                    val job = viewModelScope.launch {
                        try {
                            _uiState.update { it.copy(locationFetchLoading = false) }
                            fetchPublicCompanies(location = "POINT(${loc.longitude} ${loc.latitude})")
                            onSuccess()
                        } catch (e: Exception) {
                            _uiState.update { it.copy(locationFetchLoading = false) }
                            SnackbarManager.show(
                                message = "Failed to fetch location",
                                type = SnackbarType.ERROR
                            )
                        }
                    }
                    job.invokeOnCompletion { isFetching = false }
                },
                onError = { err ->
                    onError(err)
                    _uiState.update { it.copy(locationFetchLoading = false) }
                    isFetching = false
                }
            )
        } catch (e: Exception) {
            _uiState.update { it.copy(locationFetchLoading = false) }
            isFetching = false
            SnackbarManager.show(message = "Failed to fetch location", type = SnackbarType.ERROR)
        }
    }

    fun fetchPublicCompanies(location: String? = null) {
        viewModelScope.launch {

            _uiState.update { it.copy(companyListLoading = true) }
            val result = fetchPublicCompanyUseCase(location)
            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            companyListLoading = false,
                            companyList = result.data ?: emptyList()
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            companyListLoading = false,
                            companyList = emptyList()
                        )
                    }
                }

                is Resource.Loading -> {
                    _uiState.update {
                        it.copy(
                            companyListLoading = true,
                            companyList = emptyList()
                        )
                    }
                }
            }
        }
    }

    fun onCompanyQueryChange(value: String) {
        _uiState.update { it.copy(companySearchQuery = value, expanded = true) }
        if (value.isBlank()) {
            _uiState.update { it.copy(selectedCompany = null) }
        }
    }

    fun onCompanySelected(company: PublicCompanyModel) {
        _uiState.update {
            it.copy(
                selectedCompany = company,
                companySearchQuery = company.name,
                companyError = null,
                expanded = false
            )
        }
    }

    fun onUserNameChange(value: String) {
        _uiState.update { it.copy(userName = value, userNameError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun expandedFunc(value: Boolean) {
        _uiState.update { it.copy(expanded = value) }
    }

    fun clearSearchQuery() {
        _uiState.update { it.copy(companySearchQuery = "") }
    }

    fun clearSelectedCompany() {
        _uiState.update { it.copy(selectedCompany = null) }
    }

    fun login() {

        val companyId = _uiState.value.selectedCompany?.id
        val username = _uiState.value.userName.trim()
        val password = _uiState.value.password
        val clientDetails = ClientDetailsModel(
            androidVersion = android.os.Build.VERSION.RELEASE,
            apiLevel = android.os.Build.VERSION.SDK_INT,
            apiVersion = BuildConfig.VERSION_NAME,
            manufacturer = android.os.Build.MANUFACTURER,
            brand = android.os.Build.BRAND,
            model = android.os.Build.MODEL
        )

        val clientDetailsJson = Gson().toJson(clientDetails)

        var hasError = false

        if (companyId == null) {
            _uiState.update { it.copy(companyError = "Company is required") }
            hasError = true
        } else {
            _uiState.update { it.copy(companyError = null) }
        }

        if (username.isEmpty()) {
            _uiState.update { it.copy(userNameError = "Username is required") }
            hasError = true
        } else {
            _uiState.update { it.copy(userNameError = null) }
        }

        if (password.isEmpty()) {
            _uiState.update { it.copy(passwordError = "Password is required") }
            hasError = true
        } else {
            _uiState.update { it.copy(passwordError = null) }
        }

        if (hasError) return

        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true, loginError = null) }
            val result = createTokenUseCase(
                companyId = companyId!!,
                username = username,
                password = password,
                clientDetails = clientDetailsJson.toString()
            )

            when (result) {

                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            loginSuccess = true,
                            isLoading = false
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginError = result.message
                        )
                    }
                }

                is Resource.Loading -> {}
            }
        }
    }
}
