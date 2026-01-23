package com.nixbug.entebus.presentation.screens.personaldetails

import androidx.lifecycle.ViewModel
import com.nixbug.entebus.presentation.screens.profile.ProfileUiModel
import com.nixbug.entebus.presentation.screens.profile.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PersonalDetailsScreenViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _editState = MutableStateFlow<ProfileUiModel?>(null)
    val editState = _editState.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

    fun startEdit() {
        _editState.value = uiState.value.userDetails.copy()
        _isEditMode.value = true
    }

    fun cancelEdit() {
        _editState.value = null
        _isEditMode.value = false
    }

    fun saveEdit() {
        val updated = _editState.value ?: return

        // TODO call API here

        _uiState.update {
            it.copy(userDetails = updated)
        }

        _isEditMode.value = false
        _editState.value = null
    }

    fun updateFullName(value: String) {
        _editState.update { it?.copy(fullName = value) }
    }

    fun updatePhone(value: String) {
        _editState.update { it?.copy(phoneNumber = value) }
    }

    fun updateEmail(value: String) {
        _editState.update { it?.copy(emailId = value) }
    }

    fun updateGender(value: Int) {
        _editState.update { it?.copy(gender = value) }
    }

    fun statusToText(status: Int): String =
        if (status == 1) "Active" else "Suspended"

    fun genderToText(gender: Int): String =
        when (gender) {
            1 -> "Other"
            2 -> "Female"
            3 -> "Male"
            4 -> "Transgender"
            else -> "N/A"
        }

    fun textToGender(text: String): Int =
        when (text) {
            "Other" -> 1
            "Female" -> 2
            "Male" -> 3
            "Transgender" -> 4
            else -> 1
        }
}
