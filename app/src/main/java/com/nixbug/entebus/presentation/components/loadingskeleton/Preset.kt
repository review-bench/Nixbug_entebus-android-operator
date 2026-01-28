package com.nixbug.entebus.presentation.components.loadingskeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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

// TODO: Currently unused – will be shown when API integration adds a loading state.
@Composable
fun HistoryCardSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SkeletonBlock(width = 120.dp, height = 16.dp)
                    SkeletonBlock(width = 80.dp, height = 16.dp)
                }
                SkeletonBlock(width = 100.dp, height = 30.dp, cornerRadius = 20.dp)
            }

            HorizontalDivider()

            repeat(3) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SkeletonBlock(width = 20.dp, height = 20.dp, cornerRadius = 4.dp)
                    Spacer(Modifier.width(8.dp))
                    SkeletonBlock(width = 150.dp, height = 14.dp)
                }
            }
        }
    }
}

// TODO: Currently unused – will be shown when API integration adds a loading state.
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

            SkeletonBlock(width = 180.dp, height = 20.dp, cornerRadius = 6.dp)

            Spacer(modifier = Modifier.height(15.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        SkeletonBlock(width = 24.dp, height = 24.dp, cornerRadius = 12.dp)
                        Spacer(Modifier.width(8.dp))
                        SkeletonBlock(width = 80.dp, height = 14.dp, cornerRadius = 4.dp)
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SkeletonBlock(width = 24.dp, height = 24.dp, cornerRadius = 12.dp)
                        Spacer(Modifier.width(8.dp))
                        SkeletonBlock(width = 100.dp, height = 14.dp, cornerRadius = 4.dp)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SkeletonBlock(width = 20.dp, height = 20.dp, cornerRadius = 10.dp)
                        Spacer(Modifier.width(5.dp))
                        SkeletonBlock(width = 50.dp, height = 14.dp, cornerRadius = 4.dp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SkeletonBlock(width = 95.dp, height = 35.dp, cornerRadius = 10.dp)
                }
            }
        }
    }
}

// TODO: Currently unused – will be shown when API integration adds a loading state.
@Composable
fun PersonalDetailsSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        SkeletonBlock(width = 120.dp, height = 16.dp, cornerRadius = 4.dp)
        Spacer(modifier = Modifier.height(15.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                repeat(6) { index ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SkeletonBlock(
                            width = 48.dp,
                            height = 48.dp,
                            cornerRadius = 14.dp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            SkeletonBlock(
                                width = 70.dp,
                                height = 12.dp,
                                cornerRadius = 4.dp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            SkeletonBlock(
                                width = 150.dp,
                                height = 18.dp,
                                cornerRadius = 4.dp
                            )
                        }
                    }
                    if (index < 5) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SkeletonBlock(width = 110.dp, height = 16.dp, cornerRadius = 4.dp)
        Spacer(modifier = Modifier.height(15.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                repeat(2) { index ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SkeletonBlock(width = 48.dp, height = 48.dp, cornerRadius = 14.dp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            SkeletonBlock(width = 65.dp, height = 12.dp, cornerRadius = 4.dp)
                            Spacer(modifier = Modifier.height(6.dp))
                            SkeletonBlock(width = 180.dp, height = 18.dp, cornerRadius = 4.dp)
                        }
                    }
                    if (index < 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
