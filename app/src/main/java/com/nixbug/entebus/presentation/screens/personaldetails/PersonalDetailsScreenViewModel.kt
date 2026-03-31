package com.nixbug.entebus.presentation.screens.personaldetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.domain.usecase.account.GetOperatorAccountUseCase
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import com.nixbug.entebus.utils.utcToIstConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalDetailsScreenViewModel @Inject constructor(
    private val getOperatorAccountUseCase: GetOperatorAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val response = getOperatorAccountUseCase()
                when (response) {
                    is Resource.Success -> {
                        val user = response.data?.firstOrNull()
                        _uiState.update {
                            it.copy(
                                id = user?.id,
                                username = user?.username,
                                gender = user?.gender,
                                genderText = mapGenderToText(user?.gender),
                                fullName = user?.fullName,
                                status = user?.status,
                                phoneNumber = user?.phoneNumber,
                                emailId = user?.emailId,
                                createdOn = user?.createdOn,
                                createdOnText = formatDate(user?.createdOn)
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

    private fun mapGenderToText(gender: Int?): String {
        return when (gender) {
            1 -> "Other"
            2 -> "Female"
            3 -> "Male"
            4 -> "Transgender"
            else -> "N/A"
        }
    }

    private fun formatDate(date: String?): String {
        return utcToIstConverter(date)?.first ?: "N/A"
    }
}
