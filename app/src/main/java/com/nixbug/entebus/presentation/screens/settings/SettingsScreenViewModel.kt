package com.nixbug.entebus.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.domain.model.local.UiMode
import com.nixbug.entebus.domain.usecase.uimode.GetUiModeUseCase
import com.nixbug.entebus.domain.usecase.uimode.SaveUiModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val getUiModeUseCase: GetUiModeUseCase,
    private val saveUiModeUseCase: SaveUiModeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUiMode()
    }

    private fun loadUiMode() {
        viewModelScope.launch {
            val uiMode = getUiModeUseCase()

            _uiState.update {
                it.copy(selectedUiMode = uiMode)
            }
        }
    }

    fun onUiModeSelected(uiMode: UiMode) {
        viewModelScope.launch {

            _uiState.update {
                it.copy(selectedUiMode = uiMode)
            }

            saveUiModeUseCase(uiMode)
        }
    }
}
