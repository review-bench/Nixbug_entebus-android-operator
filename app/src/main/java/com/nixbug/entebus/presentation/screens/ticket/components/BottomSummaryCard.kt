package com.nixbug.entebus.presentation.screens.ticket.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nixbug.entebus.presentation.screens.ticket.Landmark
import com.nixbug.entebus.presentation.screens.ticket.Passenger
import com.nixbug.entebus.ui.theme.DisabledButtonColor
import com.nixbug.entebus.utils.toCurrency
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

@Composable
fun BottomSummaryCard(
    startingLandmark: Landmark?,
    endingLandmark: Landmark?,
    totalDistanceKm: Float,
    passengers: List<Passenger>,
    onPrintClick: () -> Unit,
    isPrinting: Boolean,
    totalFare: Double
) {
    val isTicketValid = startingLandmark != null &&
            endingLandmark != null &&
            passengers.any { it.passengerCount > 0 }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp, 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            if (startingLandmark != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        " ${startingLandmark.name} -> ${endingLandmark?.name ?: ""}",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdsClick,
                        contentDescription = "AdsClick",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        " Select a starting point and destination",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
            HorizontalDivider(thickness = 1.dp, color = Color.Black)
            Spacer(Modifier.height(5.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Total Distance",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    Text(
                        String.format(LocalLocale.current.platformLocale, "%.2f km", totalDistanceKm),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp
                    )
                }
                Column {
                    Text(
                        "Total Fare",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    Text(
                        text = "${totalFare.toCurrency()} \u20B9",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
            Button(
                onClick = { onPrintClick() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(13.dp),
                contentPadding = PaddingValues(5.dp),
                colors = ButtonDefaults.buttonColors(disabledContainerColor = DisabledButtonColor),
                enabled = isTicketValid && !isPrinting,
            ) {
                if (isPrinting) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "Print Ticket",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            " Print Ticket", style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}
