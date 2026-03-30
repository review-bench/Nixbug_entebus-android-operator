package com.nixbug.entebus.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.domain.repository.SessionRepository
import com.nixbug.entebus.domain.usecase.ClearSessionUseCase
import com.nixbug.entebus.domain.usecase.token.GetTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppNavigationViewModel @Inject constructor(
    private val getTokenUseCase: GetTokenUseCase,
    private val sessionRepository: SessionRepository,
    private val clearSessionUseCase: ClearSessionUseCase
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination

    private val _navigateToAuth = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToAuth = _navigateToAuth.asSharedFlow()
    val sessionExpired = sessionRepository.sessionExpired

    init {
        checkLogin()
        observeSession()
    }

    private fun checkLogin() {
        viewModelScope.launch {
            val token = getTokenUseCase().first()

            if (!token.accessToken.isNullOrEmpty()) {
                _startDestination.value = AppNavItem.MainScreen.route
            } else {
                _startDestination.value = AppNavItem.Auth.route
            }
        }
    }

    private var handled = false

    private fun observeSession() {
        viewModelScope.launch {
            sessionRepository.sessionExpired.collect {
                if (!handled) {
                    handled = true
                    clearSessionUseCase()
                    _navigateToAuth.tryEmit(Unit)
                }
            }
        }
    }
}
