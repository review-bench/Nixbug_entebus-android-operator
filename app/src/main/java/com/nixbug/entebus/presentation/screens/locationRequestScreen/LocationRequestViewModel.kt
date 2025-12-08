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

    private var isFetching = false

    fun fetchLocationIfNeeded(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (isFetching) return
        isFetching = true

        locationHelper.fetchCurrentLocation(
            onSuccess = { loc ->
                val job = viewModelScope.launch {
                    try {
                        saveLocationUseCase(loc.latitude, loc.longitude)
                        onSuccess()
                    } catch (e: Exception) {
                        onError(e.message ?: "Failed to save location")
                    }
                }
                job.invokeOnCompletion { isFetching = false }
            },
            onError = { err ->
                onError(err)
                isFetching = false
            }
        )
    }
}
