package com.nixbug.entebus.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nixbug.entebus.domain.usecase.dutyhistory.GetDutyHistoryUseCase
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@HiltViewModel
class HistoryScreenViewModel @Inject constructor(
    private val getDutyHistoryUseCase: GetDutyHistoryUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private var searchJob: Job? = null

    fun onScreenVisible() {
        viewModelScope.launch {
            fetchHistory()
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                offset = 0,
                hasMore = true
            )
        }
        searchQuery.value = query
        if (searchJob == null) {
            observeSearch()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest {
                    fetchHistory(isPagination = false)
                }
        }
    }

    fun loadMoreHistory() {
        viewModelScope.launch {
            fetchHistory(isPagination = true)
        }
    }

    fun onStatusSelected(status: Int?) {
        _uiState.update {
            it.copy(selectedStatus = status, isFilterSelected = true)
        }
    }

    fun onDateSelected(date: String?) {
        _uiState.update {
            it.copy(selectedDate = date, isFilterSelected = true)
        }
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                selectedStatus = null,
                selectedDate = null,
                isFilterApplied = false,
                isFilterSelected = false
            )
        }
        viewModelScope.launch {
            fetchHistory()
        }
    }

    fun applyFilters() {
        _uiState.update { it.copy(isFilterApplied = true) }
        viewModelScope.launch {
            fetchHistory()
        }
    }

    private suspend fun fetchHistory(
        isPagination: Boolean = false
    ) {
        val currentState = _uiState.value
        if (isPagination && (currentState.isPaginationLoading || !currentState.hasMore)) return

        if (isPagination) {
            _uiState.update { it.copy(isPaginationLoading = true) }
        } else {
            _uiState.update {
                it.copy(
                    offset = 0,
                    hasMore = false,
                    history = emptyList(),
                    isLoading = true
                )
            }
        }

        try {
            val result = getDutyHistoryUseCase(
                offset = if (isPagination) currentState.offset else 0,
                limit = currentState.limit,
                dutyId = searchQuery.value,
                status = currentState.selectedStatus,
                date = currentState.selectedDate
            )

            when (result) {
                is Resource.Success -> {
                    val newList = result.data ?: emptyList()
                    _uiState.update {
                        it.copy(
                            history = if (isPagination)
                                it.history + newList
                            else
                                newList,
                            offset = if (isPagination)
                                it.history.size + newList.size
                            else
                                newList.size,
                            hasMore = newList.size == it.limit
                        )
                    }
                }

                is Resource.Empty -> {
                    _uiState.update {
                        if (isPagination) {
                            it.copy(hasMore = false)
                        } else {
                            it.copy(history = emptyList(), hasMore = false)
                        }
                    }
                }

                is Resource.Error -> {
                    SnackbarManager.show(
                        result.message ?: "Failed to fetch history",
                        SnackbarType.ERROR
                    )
                }

                else -> {}
            }

        } finally {
            if (isPagination) {
                _uiState.update { it.copy(isPaginationLoading = false) }
            } else {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun calculateDuration(startedOn: String?, finishedOn: String?): String {
        if (startedOn == null || finishedOn == null) return "Not finished yet"

        return try {
            val startInstant = Instant.parse(startedOn)
            val endInstant = Instant.parse(finishedOn)

            val duration = Duration.between(startInstant, endInstant)

            val hours = duration.toHours()
            val minutes = duration.minusHours(hours).toMinutes()

            when {
                hours > 0 -> "${hours}h ${minutes}m"
                minutes > 0 -> "${minutes}m"
                else -> "Less than a minute"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }

    /**
     * Converts epoch time in milliseconds to a formatted date string.
     *
     * @param millis Epoch time in milliseconds, representing an instant in UTC.
     * @return A date string formatted as `yyyy-MM-dd`, where the date is calculated
     *         using the UTC time zone and formatted using the device's default locale.
     *
     * Note:
     * This function intentionally uses UTC to ensure consistent date values
     * across all devices, regardless of their local time zone.
     */
    fun convertMillisToDate(millis: Long): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withLocale(Locale.getDefault())

        return Instant.ofEpochMilli(millis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
            .format(formatter)
    }
}
