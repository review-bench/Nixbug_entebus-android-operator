package com.nixbug.entebus.utils.snackbar

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object SnackbarManager {
    private val _events = Channel<SnackbarEvent?>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun show(
        message: String,
        type: SnackbarType = SnackbarType.INFO,
        duration: SnackbarDuration = SnackbarDuration.Short,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        showDismissButton: Boolean = true
    ) {
        val result = _events.trySend(
            SnackbarEvent(
                message,
                type,
                duration,
                actionLabel,
                onAction,
                showDismissButton
            )
        )

        if (result.isFailure) {
            Log.e(
                TAG,
                "Snackbar event dropped. Message=$message, Type=$type",
                result.exceptionOrNull()
            )
        }
    }

    fun dismiss() {
        _events.trySend(null)
    }
}
