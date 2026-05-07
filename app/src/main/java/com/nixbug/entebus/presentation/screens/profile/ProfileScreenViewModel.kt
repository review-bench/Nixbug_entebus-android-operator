package com.nixbug.entebus.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.domain.model.local.SyncEvent
import com.nixbug.entebus.domain.usecase.ClearSessionUseCase
import com.nixbug.entebus.domain.usecase.queue.HasPendingSyncItemsUseCase
import com.nixbug.entebus.domain.usecase.queue.ObserveSyncEventsUseCase
import com.nixbug.entebus.domain.usecase.queue.TriggerSyncUseCase
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
import kotlinx.coroutines.async
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
    private val hasServiceWithStatusStartedUseCase: HasServiceWithStartedStatusUseCase,
    private val triggerSyncUseCase: TriggerSyncUseCase,
    private val observeSyncEventsUseCase: ObserveSyncEventsUseCase
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

    fun syncNow() {
        viewModelScope.launch {

            val eventDeferred = async {
                observeSyncEventsUseCase()
                    .first {
                        it is SyncEvent.Success || it is SyncEvent.Failed
                    }
            }

            triggerSyncUseCase()

            when (val event = eventDeferred.await()) {

                is SyncEvent.Failed -> {
                    SnackbarManager.show(
                        event.message,
                        SnackbarType.ERROR
                    )
                }

                is SyncEvent.Success -> {
                    SnackbarManager.show(
                        "Sync completed",
                        SnackbarType.SUCCESS
                    )
                }

                else -> Unit
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val hasActiveService = hasServiceWithStatusStartedUseCase()
            if (hasActiveService) {
                SnackbarManager.show(
                    "You have an active service. Please end it before logging out.",
                    SnackbarType.ERROR
                )
                return@launch
            }
            val hasPending = hasPendingSyncItemsUseCase()
            if (hasPending) {
                SnackbarManager.show(
                    message = "Sync in progress. Please wait.",
                    type = SnackbarType.ERROR,
                    actionLabel = "Sync",
                    onAction = { viewModelScope.launch { syncNow() } }
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
