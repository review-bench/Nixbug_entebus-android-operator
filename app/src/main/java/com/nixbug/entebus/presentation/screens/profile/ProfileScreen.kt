package com.nixbug.entebus.presentation.screens.profile

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nixbug.entebus.ui.theme.DisabledButtonColor


@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(
                color = MaterialTheme.colorScheme.primary,
            )
    ) {
        ProfileScreenTop(
            modifier = Modifier.weight(0.4f)
        )
        ProfileScreenBottom(
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
fun ProfileScreenTop(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Color(0xFFE1E8F4)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "A", style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold, color = DisabledButtonColor
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Amal",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProfileScreenBottom(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .verticalScroll(rememberScrollState())
            .padding(bottom = 60.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        ProfileOptionCard(
            leadingIcon = Icons.Default.Person2,
            leadingIconBg = DisabledButtonColor,
            title = "Personal details",
            subtitle = "View your personal info"
        )

        Spacer(modifier = Modifier.height(20.dp))

        ProfileOptionCard(
            leadingIcon = Icons.AutoMirrored.Filled.Logout,
            leadingIconBg = DisabledButtonColor,
            title = "Log Out",
            subtitle = "Sign out from this account"
        )

        Spacer(modifier = Modifier.height(20.dp))

        ProfileOptionCard(
            leadingIcon = Icons.AutoMirrored.Filled.Logout,
            leadingIconBg = DisabledButtonColor,
            title = "Log Out",
            subtitle = "Sign out from this account"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Version 0.1.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}


@Composable
fun ProfileOptionCard(
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector,
    leadingIconBg: Color,
    title: String,
    subtitle: String,
    trailingIcon: ImageVector = Icons.Default.DoubleArrow,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = { onClick?.invoke() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Leading icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(leadingIconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text section
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Trailing icon
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
