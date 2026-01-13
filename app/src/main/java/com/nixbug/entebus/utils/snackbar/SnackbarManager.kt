package com.nixbug.entebus.utils.snackbar

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * A centralized manager for dispatching Snackbar events across the app.
 *
 * `SnackbarManager` exposes a shared [events] Flow that UI layers can collect
 * to display Snackbar messages in a single, consistent place (typically from
 * a root `Scaffold`).
 *
 * ## Usage
 * - Call [show] to request displaying a Snackbar with a message, type, optional
 *   action, and duration.
 * - Call [dismiss] to programmatically dismiss the currently visible Snackbar,
 *   if any.
 *
 * The UI layer is responsible for collecting [events] and rendering the Snackbar
 * using a `SnackbarHost`.
 *
 * ## Thread safety
 * - [show] and [dismiss] are safe to call from any thread.
 * - Internally, events are sent through a buffered [Channel], which avoids
 *   blocking callers.
 * - If the channel buffer is full, the event may be dropped; such cases are
 *   logged to avoid silent failures.
 *
 * ## Notes
 * - This manager is intended for short-lived, non-blocking user feedback
 *   (success, error, warning, info).
 * - Long-running or blocking user interactions should use dialogs instead.
 */

object SnackbarManager {
    private const val TAG = "SnackbarManager"
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
