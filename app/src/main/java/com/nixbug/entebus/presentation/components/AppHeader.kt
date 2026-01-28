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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppHeader(
    title: String,
    onBackClick: () -> Unit,
    showTopBarActions: Boolean = false,
    primaryActionText: String = "",
    primaryActionIcon: ImageVector? = null,
    onPrimaryActionClick: () -> Unit = {},
    paddingEnd: Dp = 9.dp
) {
    Surface(
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp, end = paddingEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            if (showTopBarActions) {
                if (primaryActionIcon != null) {
                    Button(
                        onClick = onPrimaryActionClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                    ) {
                        Icon(
                            imageVector = primaryActionIcon,
                            contentDescription = primaryActionText,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = primaryActionText,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }

    }
}
