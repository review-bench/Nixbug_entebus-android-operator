package com.nixbug.entebus.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun DialogBox(
    title: String,
    message: String,
    confirmText: String = "Open Settings",
    onConfirmClick: () -> Unit,
    dismissible: Boolean = false,
    showExitButton: Boolean = false,
    exitText: String = "Exit App",
    onExitClick: (() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = dismissible,
            dismissOnClickOutside = dismissible
        )
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            tonalElevation = 8.dp,
            shadowElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .widthIn(min = 280.dp, max = 360.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF144698)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Message
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Confirm Button
                Button(
                    onClick = onConfirmClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D6EFD)),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = confirmText,
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                if (showExitButton && onExitClick != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onExitClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = MaterialTheme.shapes.small,
                        border = BorderStroke(1.dp, Color(0xFF0D6EFD))
                    ) {
                        Text(
                            text = exitText,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF0D6EFD)
                        )
                    }
                }
            }
        }
    }
}
