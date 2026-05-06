package com.nixbug.entebus.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.domain.usecase.ClearSessionUseCase
import com.nixbug.entebus.domain.usecase.HasPendingSyncItemsUseCase
import com.nixbug.entebus.domain.usecase.accountimage.GetAccountImageUseCase
import com.nixbug.entebus.domain.usecase.service.ClearAllServicesUseCase
import com.nixbug.entebus.domain.usecase.service.HasServiceWithStartedStatusUseCase
import com.nixbug.entebus.domain.usecase.token.GetTokenUseCase
import com.nixbug.entebus.domain.usecase.token.RevokeTokenUseCase
import com.nixbug.entebus.domain.usecase.user.GetUserUseCase
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val revokeTokenUseCase: RevokeTokenUseCase,
    private val getTokenUseCase: GetTokenUseCase,
    private val getAccountImageUseCase: GetAccountImageUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val clearSessionUseCase: ClearSessionUseCase,
    private val clearAllServicesUseCase: ClearAllServicesUseCase,
    private val hasPendingSyncItemsUseCase: HasPendingSyncItemsUseCase,
    private val hasServiceWithStatusStartedUseCase: HasServiceWithStartedStatusUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getUserUseCase().collect { user ->
                _uiState.update {
                    it.copy(
                        username = user.username
                    )
                }
            }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isImageLoading = true) }
            try {
                val response = getAccountImageUseCase()
                when (response) {
                    is Resource.Success -> {
                        val userImage = response.data?.firstOrNull()
                        _uiState.update {
                            it.copy(
                                imageId = userImage?.id
                            )
                        }

                    }

                    is Resource.Error -> {
                        SnackbarManager.show(
                            response.message ?: "Something went wrong",
                            SnackbarType.ERROR
                        )
                    }

                    else -> {
                        SnackbarManager.show(
                            response.message ?: "Something went wrong",
                            SnackbarType.ERROR
                        )
                    }

                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                SnackbarManager.show(e.message ?: "Something went wrong", SnackbarType.ERROR)
            } finally {
                _uiState.update { it.copy(isImageLoading = false) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val hasPending = hasPendingSyncItemsUseCase()
            if (hasPending) {
                SnackbarManager.show(
                    "Sync in progress. Please wait.",
                    SnackbarType.ERROR
                )
                return@launch
            }
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
                            SnackbarManager.show(
                                result.message
                                    ?: "Logged out locally. Couldn't reach server.",
                                SnackbarType.ERROR
                            )
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
                clearSessionUseCase()
                clearAllServicesUseCase()
            }
        }
    }
}
