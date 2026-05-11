package com.nixbug.entebus.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nixbug.entebus.BuildConfig
import com.nixbug.entebus.data.manager.TokenManager
import com.nixbug.entebus.domain.model.PublicCompanyModel
import com.nixbug.entebus.domain.repository.SessionRepository
import com.nixbug.entebus.domain.usecase.token.CreateTokenUseCase
import com.nixbug.entebus.domain.usecase.FetchPublicCompanyUseCase
import com.nixbug.entebus.domain.usecase.roleandpermission.GetRoleAndPermissionUseCase
import com.nixbug.entebus.domain.usecase.token.SaveTokenUseCase
import com.nixbug.entebus.domain.usecase.user.SaveUserUseCase
import com.nixbug.entebus.utils.LocationHelper
import com.nixbug.entebus.utils.network.Resource
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val locationHelper: LocationHelper,
    private val createTokenUseCase: CreateTokenUseCase,
    private val fetchPublicCompanyUseCase: FetchPublicCompanyUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val tokenManager: TokenManager,
    private val sessionRepository: SessionRepository,
    private val gson: Gson,
    private val getRoleAndPermissionUseCase: GetRoleAndPermissionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    private var isFetching = false
    private val searchQuery = MutableStateFlow("")
    private val location = MutableStateFlow("")
    private var searchJob: Job? = null

    @OptIn(FlowPreview::class)
    private fun observeCompanySearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { _ ->
                    fetchPublicCompaniesInternal(isPagination = false, isSearching = true)
                }
        }
    }

    fun fetchLocation(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (isFetching) {
            onError("Location fetch already in progress")
            return
        }
        isFetching = true
        _uiState.update {
            it.copy(
                locationFetchLoading = true
            )
        }

        try {
            locationHelper.fetchCurrentLocation(
                onSuccess = { loc ->
                    val job = viewModelScope.launch {
                        try {
                            location.value = "POINT(${loc.longitude} ${loc.latitude})"
                            _uiState.update { it.copy(locationFetchLoading = false) }
                            fetchPublicCompanies()
                            onSuccess()
                        } catch (e: Exception) {
                            _uiState.update { it.copy(locationFetchLoading = false) }
                            fetchPublicCompanies()
                            SnackbarManager.show(
                                message = "Failed to fetch location",
                                type = SnackbarType.ERROR
                            )
                            onError("Failed to process location")
                        } finally {
                            isFetching = false
                        }
                    }
                    job.invokeOnCompletion { isFetching = false }
                },
                onError = { err ->
                    _uiState.update { it.copy(locationFetchLoading = false) }
                    isFetching = false
                    fetchPublicCompanies()
                    SnackbarManager.show(
                        message = "Failed to fetch location",
                        type = SnackbarType.ERROR
                    )
                    onError("Failed to process location")
                }
            )
        } catch (e: Exception) {
            _uiState.update { it.copy(locationFetchLoading = false) }
            isFetching = false
            fetchPublicCompanies()
            SnackbarManager.show(message = "Failed to fetch location", type = SnackbarType.ERROR)
            onError("Failed to process location")
        }
    }

    fun fetchPublicCompanies(isPagination: Boolean = false) {
        viewModelScope.launch {
            fetchPublicCompaniesInternal(isPagination)
        }
    }

    suspend fun fetchPublicCompaniesInternal(
        isPagination: Boolean = false,
        isSearching: Boolean = false
    ) {
        val currentState = _uiState.value
        if (isPagination && (currentState.isPaginationLoading || !currentState.hasMore)) return
        val locationParam = location.value.ifEmpty { null }

        if (isPagination) {
            _uiState.update { it.copy(isPaginationLoading = true) }
        } else if (isSearching) {
            _uiState.update {
                it.copy(
                    offset = 0,
                    hasMore = true,
                    companyListError = null,
                    companySearchLoading = true
                )
            }

        } else {
            _uiState.update {
                it.copy(
                    offset = 0,
                    companyListLoading = true,
                    hasMore = true,
                    companyListError = null
                )
            }
        }

        try {
            val result = fetchPublicCompanyUseCase(
                location = locationParam,
                name = searchQuery.value,
                offset = if (isPagination) currentState.offset else 0,
                limit = currentState.limit
            )

            when (result) {
                is Resource.Success -> {
                    val newList = result.data ?: emptyList()

                    _uiState.update {
                        it.copy(
                            companyList = if (isPagination) it.companyList + newList else newList,
                            offset = if (isPagination) it.offset + it.limit else it.limit,
                            hasMore = newList.size == it.limit,
                            companyListError = null
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(companyListError = result.message)
                    }
                }

                else -> {}
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            SnackbarManager.show(e.message ?: "Something went wrong", SnackbarType.ERROR)
        } finally {
            if (isPagination) {
                _uiState.update { it.copy(isPaginationLoading = false) }
            } else if (isSearching) {
                _uiState.update { it.copy(companySearchLoading = false) }
            } else {
                _uiState.update { it.copy(companyListLoading = false) }
            }
        }

    }

    fun expandedFunc(value: Boolean) {
        _uiState.update { it.copy(expanded = value) }
    }

    fun onCompanyQueryChange(value: String) {

        _uiState.update {
            it.copy(
                companySearchQuery = value,
                expanded = true,
                offset = 0,
                hasMore = true
            )
        }

        searchQuery.value = value

        if (searchJob == null) {
            observeCompanySearch()
        }
    }

    fun clearSearchQuery() {
        _uiState.update { it.copy(companySearchQuery = "") }
        searchQuery.value = ""
    }

    fun clearSelectedCompany() {
        _uiState.update { it.copy(selectedCompany = null) }
    }

    fun onCompanySelected(company: PublicCompanyModel) {
        _uiState.update {
            it.copy(
                selectedCompany = company,
                companySearchQuery = company.name,
                companyError = null,
                expanded = false
            )
        }
    }

    fun onUserNameChange(value: String) {
        _uiState.update { it.copy(userName = value, userNameError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun login() {

        val companyId = _uiState.value.selectedCompany?.id
        val username = _uiState.value.userName.trim()
        val password = _uiState.value.password
        val clientDetails = ClientDetailsModel(
            androidVersion = android.os.Build.VERSION.RELEASE,
            apiLevel = android.os.Build.VERSION.SDK_INT,
            apiVersion = BuildConfig.VERSION_NAME,
            manufacturer = android.os.Build.MANUFACTURER,
            brand = android.os.Build.BRAND,
            model = android.os.Build.MODEL
        )

        val clientDetailsJson = gson.toJson(clientDetails)

        var hasError = false

        if (companyId == null) {
            _uiState.update { it.copy(companyError = "Company is required") }
            hasError = true
        } else {
            _uiState.update { it.copy(companyError = null) }
        }

        if (username.isEmpty()) {
            _uiState.update { it.copy(userNameError = "Username is required") }
            hasError = true
        } else {
            _uiState.update { it.copy(userNameError = null) }
        }

        if (password.isEmpty()) {
            _uiState.update { it.copy(passwordError = "Password is required") }
            hasError = true
        } else {
            _uiState.update { it.copy(passwordError = null) }
        }

        if (hasError) return

        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true, loginError = null) }

            val result = createTokenUseCase(
                companyId = companyId!!,
                username = username,
                password = password,
                clientDetails = clientDetailsJson
            )

            when (result) {

                is Resource.Success -> {
                    result.data?.let { token ->
                        saveTokenUseCase(token)
                        tokenManager.accessToken = token.accessToken
                        val company = _uiState.value.selectedCompany
                        val persistedUsername = username

                        if (company != null) {
                            saveUserUseCase(
                                companyId = company.id,
                                companyName = company.name,
                                username = persistedUsername
                            )
                        }
                    }
                    val operatorId = result.data?.operatorId
                    if (operatorId == null || operatorId <= 0) {
                        tokenManager.accessToken = null
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loginError = "Invalid login response. Please try again."
                            )
                        }
                        return@launch
                    }
                    val permissionResult = getRoleAndPermissionUseCase(operatorId)
                    when (permissionResult) {

                        is Resource.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    loginSuccess = true,
                                    isNavigating = true
                                )
                            }
                            sessionRepository.resetSession()
                        }

                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    loginError = permissionResult.message
                                        ?: "Unable to fetch role and permissions."
                                )
                            }
                        }

                        else -> Unit
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginError = result.message
                        )
                    }
                }

                else -> {}
            }
        }
    }

}
