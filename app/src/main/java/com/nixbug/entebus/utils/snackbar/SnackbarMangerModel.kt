package com.nixbug.entebus.utils.snackbar

import androidx.compose.material3.SnackbarDuration


enum class SnackbarType {
    SUCCESS, ERROR, WARNING, INFO
}

data class SnackbarEvent(
    val message: String,
    val type: SnackbarType = SnackbarType.INFO,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val showDismissButton: Boolean? = true
)
