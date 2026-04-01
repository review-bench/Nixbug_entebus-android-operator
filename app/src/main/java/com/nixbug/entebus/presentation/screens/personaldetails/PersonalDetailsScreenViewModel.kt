package com.nixbug.entebus.presentation.screens.personaldetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.data.remote.operator.model.GenderType
import com.nixbug.entebus.domain.usecase.account.GetOperatorAccountUseCase
import com.nixbug.entebus.domain.usecase.account.UpdateOperatorAccountUseCase
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
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class PersonalDetailsScreenViewModel @Inject constructor(
    private val getOperatorAccountUseCase: GetOperatorAccountUseCase,
    private val updateOperatorAccountUseCase: UpdateOperatorAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalDetailsUiState())
    val uiState = _uiState.asStateFlow()

//    To SHOW IN UI

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
                                statusText = mapStatusToText(user?.status),
                                phoneNumber = user?.phoneNumber,
                                emailId = user?.emailId,
                                createdOn = user?.createdOn,
                                createdOnText = formatDate(user?.createdOn),
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

    private fun mapStatusToText(status: Int?): String {
        return when (status) {
            1 -> "Active"
            2 -> "Suspended"
            else -> "N/A"
        }
    }

//    TO EDIT

    fun onStartEdit() {
        _uiState.update {
            it.copy(
                isEditMode = true,
                editedFullName = it.fullName,
                editedGender = it.gender,
                editedPhoneNumber = it.phoneNumber,
                editedEmailId = it.emailId
            )
        }
    }

    fun onFullNameChange(value: String) {
        _uiState.update {
            it.copy(
                editedFullName = value,
                fullNameError = null
            )
        }
    }

    fun onPhoneChange(value: String) {
        _uiState.update {
            it.copy(
                editedPhoneNumber = value,
                phoneError = null
            )
        }
    }

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(
                editedEmailId = value,
                emailError = null
            )
        }
    }

    fun onGenderChange(value: String) {
        val genderInt = when (value) {
            "Male" -> 3
            "Female" -> 2
            "Other" -> 1
            "Transgender" -> 4
            else -> null
        }
        _uiState.update {
            it.copy(
                editedGender = genderInt,
                genderText = value
            )
        }
    }

    fun onSaveClick() {

        val state = _uiState.value




    }

}
