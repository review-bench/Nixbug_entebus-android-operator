import androidx.lifecycle.ViewModel
import com.nixbug.entebus.presentation.authScreen.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun onCompanyQueryChange(value: String) {
        _uiState.update { it.copy(companySearchQuery = value, expanded = true) }
        if (value == "") {
            _uiState.update { it.copy(selectedCompany = null) }
        }
    }

    fun onCompanySelected(company: String) {
        _uiState.update {
            it.copy(
                selectedCompany = company,
                companySearchQuery = company,
                expanded = false
            )
        }
    }

    fun onUserNameChange(value: String) {
        _uiState.update { it.copy(userName = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun expandedFunc(value: Boolean) {
        _uiState.update { it.copy(expanded = value) }
    }

    fun clearSearchQuery() {
        _uiState.update { it.copy(companySearchQuery = "") }
    }

    fun clearSelectedCompany() {
        _uiState.update { it.copy(selectedCompany = null) }
    }
}
