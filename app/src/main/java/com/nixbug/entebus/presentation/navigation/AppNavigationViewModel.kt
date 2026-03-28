package com.nixbug.entebus.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.data.manager.SessionManager
import com.nixbug.entebus.domain.usecase.token.GetTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppNavigationViewModel @Inject constructor(
    private val getTokenUseCase: GetTokenUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination
    val sessionExpired = sessionManager.sessionExpired

    init {
        checkLogin()
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
}
