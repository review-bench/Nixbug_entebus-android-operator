package com.nixbug.entebus.presentation.screens.qrscanner

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class QrScannerViewModel @Inject constructor() : ViewModel() {

    private val _scannedResult = MutableStateFlow<String?>(null)
    val scannedResult: StateFlow<String?> = _scannedResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onQrScanned(result: String) {
        _scannedResult.value = result
    }

    fun onScanError(message: String) {
        _errorMessage.value = message
    }

    fun clearResult() {
        _scannedResult.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

}
