package com.nixbug.entebus.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.domain.usecase.token.ClearTokenUseCase
import com.nixbug.entebus.domain.usecase.token.GetTokenUseCase
import com.nixbug.entebus.domain.usecase.token.RevokeTokenUseCase
import com.nixbug.entebus.domain.usecase.user.ClearUserUseCase
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

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

    private val _logoutEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvent = _logoutEvent.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            try {
                val token = getTokenUseCase().first()
                val accessToken = token.accessToken
                if (!accessToken.isNullOrEmpty()) {
                    val result = revokeTokenUseCase(token = accessToken)
                    when (result) {
                        is Resource.Empty -> {
                            SnackbarManager.show(
                                "You have been logged out successfully",
                                SnackbarType.SUCCESS
                            )
                        }

                        is Resource.Error -> {
                            val statusCode = result.statusCode
                            when (statusCode) {
                                401 -> {
                                    SnackbarManager.show(
                                        "You have been signed out. Please sign in again.",
                                        SnackbarType.ERROR
                                    )
                                }
                                403 -> {
                                    SnackbarManager.show(
                                        "Session expired. You’ve been logged out",
                                        SnackbarType.SUCCESS
                                    )
                                }
                                else -> {
                                    SnackbarManager.show(
                                        "Logged out locally. Couldn't reach server.",
                                        SnackbarType.WARNING
                                    )
                                }
                            }
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(
                    "Logged out locally. Couldn't reach server.",
                    SnackbarType.WARNING
                )
            } finally {
                clearTokenUseCase()
                clearUserUseCase()
                _logoutEvent.tryEmit(Unit)
            }
        }
    }
}
