package com.nixbug.entebus.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.data.local.dataStore.LocationData
import com.nixbug.entebus.domain.usecase.GetLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class LocationState(
    val data: LocationData? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class AppNavigationViewModel @Inject constructor(
    getLocationUseCase: GetLocationUseCase
) : ViewModel() {

    val locationState = getLocationUseCase()
        .map { LocationState(it, false) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            LocationState() // initial: loading = true
        )
}
