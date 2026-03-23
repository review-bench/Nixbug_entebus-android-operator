package com.nixbug.entebus.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.domain.usecase.token.GetTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppNavigationViewModel @Inject constructor(
    private val getTokenUseCase: GetTokenUseCase
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination

    init {
        checkLogin()
    }

    private fun checkLogin() {
        viewModelScope.launch {
            val token = getTokenUseCase().first()

            if (token != null && token.accessToken.isNotEmpty()) {
                _startDestination.value = AppNavItem.MainScreen.route
            } else {
                _startDestination.value = AppNavItem.Auth.route
            }
        }
    }
}
