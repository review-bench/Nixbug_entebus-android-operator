package com.nixbug.entebus.presentation.components.printer

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nixbug.entebus.ui.theme.DisabledButtonColor

@Composable
fun PrinterLoader(printerName: String?) {
    val primaryGreen = Color(0xFF8BC34A)
    val lightGreen = Color(0xFFE8F5E9)
    val darkGreen = Color(0xFF689F38)

    Column(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {

            IconBox(
                icon = Icons.Default.PhoneAndroid,
                iconColor = darkGreen
            )

            ConnectingDots(color = primaryGreen)

            IconBox(
                icon = Icons.Default.Print,
                iconColor = darkGreen
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Connecting to Printer",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (printerName != null) {
            Text(
                text = printerName,
                style = MaterialTheme.typography.bodyLarge,
                color = primaryGreen
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedProgressBar(
            color = primaryGreen,
            backgroundColor = lightGreen
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Please keep your device nearby...",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun ConnectingDots(color: Color) {
    val infiniteTransition = rememberInfiniteTransition()

    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        )
    )

    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        )
    )

    val alpha3 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Dot(alpha1, color)
        Dot(alpha2, color)
        Dot(alpha3, color)
    }
}

@Composable
private fun Dot(alpha: Float, color: Color) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

@Composable
fun IconBox(
    icon: ImageVector,
    iconColor: Color
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(DisabledButtonColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
fun AnimatedProgressBar(
    color: Color,
    backgroundColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ping_pong_loader")

    val progressAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "slow_slider"
    )

    LinearProgressIndicator(
        progress = { progressAnimation },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50.dp)),
        color = color,
        trackColor = backgroundColor,
        strokeCap = StrokeCap.Round,
    )
}
