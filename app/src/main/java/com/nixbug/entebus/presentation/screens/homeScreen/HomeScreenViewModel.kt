package com.nixbug.entebus.presentation.screens.homeScreen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class HomeScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: MutableStateFlow<HomeUIState> = _uiState

    var selectedService: HomeUiModel? = null
        private set

    fun toggleServiceSelection(service: HomeUiModel) {
        selectedService = if (selectedService == service) null else service
    }

    fun canSelectService(): Boolean {
        return _uiState.value.currentService == null
    }

    fun startService(selectedService: HomeUiModel?): Boolean {
        selectedService ?: return false

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

        // After starting a service, clear selection
        this.selectedService = null
        return true
    }
}


