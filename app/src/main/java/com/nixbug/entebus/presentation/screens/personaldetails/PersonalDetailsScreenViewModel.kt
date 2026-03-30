package com.nixbug.entebus.presentation.screens.personaldetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.domain.repository.OperatorAccountRepository
import com.nixbug.entebus.presentation.screens.profile.ProfileUiModel
import com.nixbug.entebus.presentation.screens.profile.ProfileUiState
import com.nixbug.entebus.utils.network.Resource
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

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val response = operatorAccountRepository.fetchOperatorAccount()
            when (response) {
                is Resource.Success -> {
                    println("Profile Success${response.data}")
                }

                else -> {
                    println("Profile Error${response.message} ${response.statusCode}")
                }

            }
        }
    }

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

    enum class Gender(val id: Int, val displayName: String) {
        OTHER(1, "Other"),
        FEMALE(2, "Female"),
        MALE(3, "Male"),
        TRANSGENDER(4, "Transgender");

        companion object {
            fun fromId(id: Int): Gender =
                entries.firstOrNull { it.id == id } ?: OTHER

            fun fromText(text: String): Gender =
                entries.firstOrNull { it.displayName == text } ?: OTHER
        }
    }

    enum class Status(val id: Int, val displayName: String) {
        ACTIVE(1, "Active"),
        SUSPENDED(2, "Suspended");

        companion object {
            fun fromId(id: Int): Status =
                entries.firstOrNull { it.id == id } ?: SUSPENDED
        }
    }

    fun genderToText(genderId: Int): String =
        Gender.fromId(genderId).displayName

    fun textToGender(text: String): Int =
        Gender.fromText(text).id

    fun statusToText(statusId: Int): String =
        Status.fromId(statusId).displayName

    fun getGenderOptions(): List<String> =
        Gender.entries.map { it.displayName }

}
