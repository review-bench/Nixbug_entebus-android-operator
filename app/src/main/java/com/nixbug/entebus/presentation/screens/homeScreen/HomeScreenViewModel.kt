package com.nixbug.entebus.presentation.screens.homeScreen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class HomeScreenViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: MutableStateFlow<HomeUIState> = _uiState

    fun addCurrentService(service: HomeUiModel) {
        _uiState.update { current ->
            current.copy(
                currentServiceList = current.currentServiceList + service
            )
        }
    }
}