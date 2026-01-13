package com.nixbug.entebus.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import com.nixbug.entebus.utils.snackbar.SnackbarEvent
import com.nixbug.entebus.utils.snackbar.SnackbarType

@Composable
fun StyledSnackbar(
    event: SnackbarEvent,
    snackbarData: SnackbarData
) {
    val backgroundColor = when (event.type) {
        SnackbarType.SUCCESS -> Color(0xFF4CAF50)
        SnackbarType.ERROR -> Color(0xFFF44336)
        SnackbarType.WARNING -> Color(0xFFFF9800)
        SnackbarType.INFO -> Color(0xFF2196F3)
    }

    Snackbar(
        containerColor = backgroundColor,
        contentColor = Color.White,
        action = {
            event.actionLabel?.let {
                TextButton(onClick = { snackbarData.performAction() }) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        },
        dismissAction = {
            if (event.showDismissButton) {
                IconButton(onClick = { snackbarData.dismiss() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Color.White
                    )
                }
            }
        }
    ) {
        Text(text = event.message, color = Color.White, style = MaterialTheme.typography.labelLarge)
    }
}
