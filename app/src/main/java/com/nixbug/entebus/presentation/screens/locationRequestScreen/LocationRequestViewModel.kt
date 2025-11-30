package com.nixbug.entebus.presentation.screens.locationRequestScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.data.local.dataStore.LocationData
import com.nixbug.entebus.domain.usecase.GetLocationUseCase
import com.nixbug.entebus.domain.usecase.SaveLocationUseCase
import com.nixbug.entebus.utils.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationRequestViewModel @Inject constructor(
    private val saveLocationUseCase: SaveLocationUseCase,
    getLocationUseCase: GetLocationUseCase,
    private val locationHelper: LocationHelper
) : ViewModel() {

    data class UIState(
        val data: LocationData? = null,
        val isLoading: Boolean = true
    )

    val uiState = getLocationUseCase()
        .map { UIState(it, false) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            UIState()
        )

    fun fetchLocationIfNeeded(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val state = uiState.value

        if (state.isLoading) return
        if (state.data?.hasLocation == true) return

        locationHelper.fetchCurrentLocation(
            onSuccess = { loc ->
                viewModelScope.launch {
                    saveLocationUseCase(loc.latitude, loc.longitude)
                }
                onSuccess()
            },
            onError = { err ->
                onError(err)
            }
        )
    }
}
