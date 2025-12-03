package com.nixbug.entebus.presentation.screens.locationRequestScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.domain.usecase.SaveLocationUseCase
import com.nixbug.entebus.utils.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationRequestViewModel @Inject constructor(
    private val saveLocationUseCase: SaveLocationUseCase,
    private val locationHelper: LocationHelper
) : ViewModel() {

    fun fetchLocationIfNeeded(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        locationHelper.fetchCurrentLocation(
            onSuccess = { loc ->
                viewModelScope.launch {
                    try {
                        saveLocationUseCase(loc.latitude, loc.longitude)
                        onSuccess()
                    } catch (e: Exception) {
                        onError(e.message ?: "Failed to save location")
                    }
                }
            },
            onError = { err ->
                onError(err)
            }
        )
    }
}
