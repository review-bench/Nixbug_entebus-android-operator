package com.nixbug.entebus.presentation.components.loadingskeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HistoryCardSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SkeletonBlock(width = 120.dp, height = 16.dp) // Service ID
                    SkeletonBlock(width = 80.dp, height = 16.dp)  // Duty ID
                }
                SkeletonBlock(width = 100.dp, height = 30.dp, cornerRadius = 20.dp) // Status Chip
            }

            HorizontalDivider()

            repeat(3) { // Date, Finish, Duration rows
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SkeletonBlock(width = 20.dp, height = 20.dp, cornerRadius = 4.dp) // Icon
                    Spacer(Modifier.width(8.dp))
                    SkeletonBlock(width = 150.dp, height = 14.dp)
                }
            }
        }
    }
}

@Composable
fun ServiceCardSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            // --- Route Name Line ---
            SkeletonBlock(width = 180.dp, height = 20.dp, cornerRadius = 6.dp)

            Spacer(modifier = Modifier.height(15.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    // --- Date Row ---
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 12.dp corner on a 24.dp box makes a perfect circle
                        SkeletonBlock(width = 24.dp, height = 24.dp, cornerRadius = 12.dp)
                        Spacer(Modifier.width(8.dp))
                        SkeletonBlock(width = 80.dp, height = 14.dp, cornerRadius = 4.dp)
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    // --- Reg No Row ---
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SkeletonBlock(width = 24.dp, height = 24.dp, cornerRadius = 12.dp)
                        Spacer(Modifier.width(8.dp))
                        SkeletonBlock(width = 100.dp, height = 14.dp, cornerRadius = 4.dp)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    // --- Time Row ---
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SkeletonBlock(width = 20.dp, height = 20.dp, cornerRadius = 10.dp)
                        Spacer(Modifier.width(5.dp))
                        SkeletonBlock(width = 50.dp, height = 14.dp, cornerRadius = 4.dp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // --- Start/Download Button Skeleton ---
                    SkeletonBlock(width = 95.dp, height = 35.dp, cornerRadius = 10.dp)
                }
            }
        }
    }
}