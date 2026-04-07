package com.nixbug.entebus.presentation.screens.personaldetails

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class PersonalDetailsScreenViewModel @Inject constructor(
    private val getOperatorAccountUseCase: GetOperatorAccountUseCase,
    private val updateOperatorAccountUseCase: UpdateOperatorAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalDetailsUiState())
    val uiState = _uiState.asStateFlow()
    private val nameRegex = Regex("^[A-Za-z]+( [A-Za-z]+)*$")
    private val phoneRegex = Regex("^[0-9]{10}$")

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
                                formattedPhoneNumber = formatPhoneForUI(user?.phoneNumber),
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

    private fun formatPhoneForUI(phone: String?): String? {
        if (phone.isNullOrBlank()) return null

        return phone
            .replace("tel:", "")
            .replace("+91", "")
            .replace("-", "")
            .filter { it.isDigit() }
            .takeLast(10)
    }

    fun onStartEdit() {
        _uiState.update {
            it.copy(
                isEditMode = true,
                editedFullName = it.fullName,
                editedGender = it.gender,
                editedPhoneNumber = it.formattedPhoneNumber,
                editedEmailId = it.emailId
            )
        }
    }

    fun onCancelEdit() {
        if (_uiState.value.isEditMode) {
            _uiState.update {
                it.copy(
                    isEditMode = false,
                    editedFullName = null,
                    editedGender = null,
                    editedPhoneNumber = null,
                    editedEmailId = null,
                    fullNameError = null,
                    phoneError = null,
                    emailError = null
                )
            }
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
        val fullNameError = validateFullName(state.editedFullName)
        val emailError = validateEmail(state.editedEmailId)
        val phoneError = validatePhone(state.editedPhoneNumber)

        if (fullNameError != null || emailError != null || phoneError != null) {
            _uiState.update {
                it.copy(
                    fullNameError = fullNameError,
                    emailError = emailError,
                    phoneError = phoneError
                )
            }
            return
        }

        val updateMap = mutableMapOf<String, Any?>()

        // 1. Full Name
        if (state.editedFullName?.trim() != state.fullName?.trim()) {
            updateMap["full_name"] = state.editedFullName.takeIf { it?.isNotBlank() == true }
        }

        // 2. Email
        if (state.editedEmailId?.trim() != state.emailId?.trim()) {
            updateMap["email_id"] = state.editedEmailId.takeIf { it?.isNotBlank() == true }
        }

        // 3. Phone (Compare the cleaned versions)
        if (state.editedPhoneNumber != state.formattedPhoneNumber) {
            val phoneValue =
                state.editedPhoneNumber.takeIf { it?.isNotBlank() == true }?.let { "+91$it" }
            updateMap["phone_number"] = phoneValue
        }

        // 4. Gender
        if (state.editedGender != state.gender) {
            updateMap["gender"] = state.editedGender
        }

        if (updateMap.isEmpty()) {
            _uiState.update { it.copy(isEditMode = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val gson = GsonBuilder().serializeNulls().create()
            val jsonString = gson.toJson(updateMap)
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = jsonString.toRequestBody(mediaType)

            val result = updateOperatorAccountUseCase(body)

            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isEditMode = false,
                            fullName = result.data?.fullName,
                            gender = result.data?.gender,
                            phoneNumber = result.data?.phoneNumber,
                            emailId = result.data?.emailId,
                            genderText = mapGenderToText(result.data?.gender),
                            formattedPhoneNumber = formatPhoneForUI(result.data?.phoneNumber),
                        )
                    }
                    SnackbarManager.show("Profile updated successfully", SnackbarType.SUCCESS)
                }

                is Resource.Error -> {
                    SnackbarManager.show(result.message ?: "Update failed", SnackbarType.ERROR)
                }

                else -> {}
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun validateFullName(name: String?): String? {
        if (name.isNullOrBlank()) return null

        if (name != name.trim()) {
            return "Name should not start or end with spaces"
        }

        if (!nameRegex.matches(name)) {
            return "Invalid name format"
        }

        return null
    }

    private fun validateEmail(email: String?): String? {
        if (email.isNullOrBlank()) return null

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid email address"
        }

        return null
    }

    private fun validatePhone(phone: String?): String? {
        if (phone.isNullOrBlank()) return null

        if (!phoneRegex.matches(phone)) {
            return "Phone number must be 10 digits"
        }

        return null
    }

}
