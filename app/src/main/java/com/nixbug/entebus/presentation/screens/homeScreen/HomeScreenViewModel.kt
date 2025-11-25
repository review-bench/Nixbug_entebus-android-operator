package com.nixbug.entebus.presentation.screens.homeScreen

import androidx.lifecycle.ViewModel
import com.nixbug.entebus.presentation.screens.authScreen.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeScreenViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: MutableStateFlow<HomeUIState> = _uiState
}