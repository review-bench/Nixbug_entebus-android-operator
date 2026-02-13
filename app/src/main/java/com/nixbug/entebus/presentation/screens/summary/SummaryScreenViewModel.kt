package com.nixbug.entebus.presentation.screens.summary

import androidx.lifecycle.ViewModel
import com.nixbug.entebus.utils.utcToIstConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class SummaryScreenViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    init {
        autoSelectTodayServices()
    }

    private fun autoSelectTodayServices() {
        val today = Instant.now().toString()

        val todayServiceIds = sampleSummaryData
            .groupBy { it.serviceId }
            .filter { (_, group) ->
                val first = group.first()
                val (date, _) = utcToIstConverter(first.startedOn)
                    ?: return@filter false
                val (currentDate, _) = utcToIstConverter(today)
                    ?: return@filter false

                currentDate == date
            }
            .keys

        _uiState.value = _uiState.value.copy(
            selectedServiceIds = todayServiceIds,
            todayServiceIds = todayServiceIds
        )
    }


    fun toggleSelection(serviceId: Int) {

        val current = _uiState.value.selectedServiceIds.toMutableSet()

        if (current.contains(serviceId)) {
            current.remove(serviceId)
        } else {
            current.add(serviceId)
        }

        _uiState.value = _uiState.value.copy(
            selectedServiceIds = current
        )
    }

}
