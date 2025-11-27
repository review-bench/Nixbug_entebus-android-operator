package com.nixbug.entebus.presentation.screens.homeScreen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class HomeScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: MutableStateFlow<HomeUIState> = _uiState

    // Only check if allowed to select, do NOT update currentService
    fun canSelectService(): Boolean {
        return _uiState.value.currentService == null
    }

    // Start service → set currentService
    fun startService(selectedService: HomeUiModel?): Boolean {
        selectedService ?: return false

        // Only allow starting if no current service
        if (_uiState.value.currentService != null) return false

        _uiState.update { state ->
            val newAssigned = state.assignedServiceList.filter { it.id != selectedService.id }
            val newOther = state.serviceList.filter { it.id != selectedService.id }

            state.copy(
                currentService = selectedService,
                assignedServiceList = newAssigned,
                serviceList = newOther
            )
        }
        return true
    }
}

