package com.nixbug.entebus.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.utils.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val locationHelper: LocationHelper) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    private var isFetching = false

    // TODO:
    // Currently, the fetched location (lat/lng) is not used.
    // This is intentionally kept for future enhancement.
    // Once the backend company endpoint supports location parameters,
    // these coordinates will be passed for location-based company filtering.
    fun fetchLocation(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (isFetching) {
            onError("Location fetch already in progress")
            return
        }
        isFetching = true
        try {
            locationHelper.fetchCurrentLocation(
                onSuccess = { loc ->
                    val job = viewModelScope.launch {
                        try {
                            onSuccess()
                        } catch (e: Exception) {
                            onError(e.message ?: "Failed to fetch location")
                        }
                    }
                    job.invokeOnCompletion { isFetching = false }
                },
                onError = { err ->
                    onError(err)
                    isFetching = false
                }
            )
        } catch (e: Exception) {
            isFetching = false
            onError(e.message ?: "Failed to fetch location")
        }
    }

    fun onCompanyQueryChange(value: String) {
        _uiState.update { it.copy(companySearchQuery = value, expanded = true) }
        if (value.isBlank()) {
            _uiState.update { it.copy(selectedCompany = null) }
        }
    }

    fun onCompanySelected(company: String) {
        _uiState.update {
            it.copy(
                selectedCompany = company,
                companySearchQuery = company,
                expanded = false
            )
        }
    }

    fun onUserNameChange(value: String) {
        _uiState.update { it.copy(userName = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
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

}
