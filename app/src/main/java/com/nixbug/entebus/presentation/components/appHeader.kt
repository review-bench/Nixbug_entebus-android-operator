package com.nixbug.entebus.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppHeader(
    title: String,
    onBackClick: () -> Unit,
    showTopBarActions: Boolean = false,
    primaryActionText: String = "",
    primaryActionIcon: ImageVector? = null,
    onPrimaryActionClick: () -> Unit = {},
    secondaryActionText: String = "",
    secondaryActionIcon: ImageVector? = null,
    onSecondaryActionClick: () -> Unit = {},
) {
    Surface(
//        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding( vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🔙 Back Button
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            // 📌 Title
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            // 🔘 Conditional Action Buttons
            if (showTopBarActions) {
                if (primaryActionIcon != null) {
                    Button(
                        onClick = onPrimaryActionClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = primaryActionIcon,
                            contentDescription = primaryActionText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = primaryActionText)
                    }
                }

                if (secondaryActionIcon != null) {
                    TextButton(onClick = onSecondaryActionClick) {
                        Icon(
                            imageVector = secondaryActionIcon,
                            contentDescription = secondaryActionText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = secondaryActionText)
                    }
                }
            }
        }
    }
}
