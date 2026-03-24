package com.nixbug.entebus.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.data.interceptor.TokenManager
import com.nixbug.entebus.domain.usecase.token.ClearTokenUseCase
import com.nixbug.entebus.domain.usecase.token.GetTokenUseCase
import com.nixbug.entebus.domain.usecase.token.RevokeTokenUseCase
import com.nixbug.entebus.domain.usecase.user.ClearUserUseCase
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val revokeTokenUseCase: RevokeTokenUseCase,
    private val getTokenUseCase: GetTokenUseCase,
    private val clearTokenUseCase: ClearTokenUseCase,
    private val tokenManager: TokenManager,
    private val clearUserUseCase: ClearUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _logoutEvent = MutableStateFlow(false)
    val logoutEvent: StateFlow<Boolean> = _logoutEvent

    fun logout() {
        viewModelScope.launch {
            try {
                val token = getTokenUseCase().first()
                val accessToken = token?.accessToken
                if (!accessToken.isNullOrEmpty()) {
                    revokeTokenUseCase(token = accessToken)
                }
            } catch (e: Exception) {
                SnackbarManager.show("Token not found.", SnackbarType.WARNING)
            } finally {
                clearTokenUseCase()
                clearUserUseCase()
                tokenManager.accessToken = null
                _logoutEvent.value = true
            }
        }
    }
}
