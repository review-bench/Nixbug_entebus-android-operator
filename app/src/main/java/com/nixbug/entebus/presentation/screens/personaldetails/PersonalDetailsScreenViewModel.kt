package com.nixbug.entebus.presentation.screens.personaldetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.domain.repository.OperatorAccountRepository
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalDetailsScreenViewModel @Inject constructor(
    private val operatorAccountRepository: OperatorAccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val response = operatorAccountRepository.fetchOperatorAccount()
                when (response) {
                    is Resource.Success -> {
                        val user = response.data?.firstOrNull()
                        _uiState.update {
                            it.copy(
                                id = user?.id,
                                username = user?.username,
                                gender = user?.gender,
                                fullName = user?.fullName,
                                status = user?.status,
                                phoneNumber = user?.phoneNumber,
                                emailId = user?.emailId,
                                createdOn = user?.createdOn
                            )
                        }
                    }

                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(error = response.message)
                        }
                        SnackbarManager.show("${response.message}", SnackbarType.ERROR)
                    }

                    else -> {
                        SnackbarManager.show("${response.message}", SnackbarType.ERROR)
                    }

                }
            } catch (e: Exception) {
                SnackbarManager.show("${e.message}", SnackbarType.ERROR)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }

        }
    }
}
