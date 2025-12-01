package com.nixbug.entebus.presentation.screens.authScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.data.local.dataStore.LocationData
import com.nixbug.entebus.domain.usecase.GetLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor( getLocationUseCase: GetLocationUseCase): ViewModel(){
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    val location = getLocationUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, LocationData(null, null,false))

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

    init {
        // Collect the location flow and log changes
        viewModelScope.launch {
            location.collect { loc ->
                Log.d("AuthDebug_VM", "Latitude=${loc.latitude}, Longitude=${loc.longitude}")
                Log.d("AuthDebug_VM","hasLocation${loc.hasLocation}")
            }
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
