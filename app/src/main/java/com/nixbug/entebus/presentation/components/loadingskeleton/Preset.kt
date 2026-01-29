package com.nixbug.entebus.presentation.components.loadingskeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

/**
 * Common skeleton card wrapper.
 * ONLY extracted shared Card configuration.
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            content = content
        )
    }
}

// TODO: Currently unused – will be shown when API integration adds a loading state.
@Composable
fun HistoryCardSkeleton(shimmerBrush: Brush) {
    SkeletonCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SkeletonBlock(shimmerBrush = shimmerBrush, width = 120.dp, height = 16.dp)
                    SkeletonBlock(shimmerBrush = shimmerBrush, width = 80.dp, height = 16.dp)
                }
                SkeletonBlock(
                    shimmerBrush = shimmerBrush,
                    width = 100.dp,
                    height = 30.dp,
                    cornerRadius = 20.dp
                )
            }

            HorizontalDivider()

            repeat(3) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SkeletonBlock(
                        shimmerBrush = shimmerBrush,
                        width = 20.dp,
                        height = 20.dp,
                        cornerRadius = 4.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    SkeletonBlock(shimmerBrush = shimmerBrush, width = 150.dp, height = 14.dp)
                }
            }
        }
    }
}

// TODO: Currently unused – will be shown when API integration adds a loading state.
@Composable
fun ServiceCardSkeleton(shimmerBrush: Brush) {
    SkeletonCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp)
    ) {
        SkeletonBlock(
            shimmerBrush = shimmerBrush,
            width = 180.dp,
            height = 20.dp,
            cornerRadius = 6.dp
        )

        Spacer(modifier = Modifier.height(15.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SkeletonBlock(
                        shimmerBrush = shimmerBrush,
                        width = 24.dp,
                        height = 24.dp,
                        cornerRadius = 12.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    SkeletonBlock(
                        shimmerBrush = shimmerBrush,
                        width = 80.dp,
                        height = 14.dp,
                        cornerRadius = 4.dp
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    SkeletonBlock(
                        shimmerBrush = shimmerBrush,
                        width = 24.dp,
                        height = 24.dp,
                        cornerRadius = 12.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    SkeletonBlock(
                        shimmerBrush = shimmerBrush,
                        width = 100.dp,
                        height = 14.dp,
                        cornerRadius = 4.dp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SkeletonBlock(
                        shimmerBrush = shimmerBrush,
                        width = 20.dp,
                        height = 20.dp,
                        cornerRadius = 10.dp
                    )
                    Spacer(Modifier.width(5.dp))
                    SkeletonBlock(
                        shimmerBrush = shimmerBrush,
                        width = 50.dp,
                        height = 14.dp,
                        cornerRadius = 4.dp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                SkeletonBlock(
                    shimmerBrush = shimmerBrush,
                    width = 95.dp,
                    height = 35.dp,
                    cornerRadius = 10.dp
                )
            }
        }
    }
}

// TODO: Currently unused – will be shown when API integration adds a loading state.
@Composable
fun PersonalDetailsSkeleton(shimmerBrush: Brush) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        SkeletonBlock(
            shimmerBrush = shimmerBrush,
            width = 120.dp,
            height = 16.dp,
            cornerRadius = 4.dp
        )

        Spacer(modifier = Modifier.height(15.dp))

        SkeletonCard(modifier = Modifier.fillMaxWidth()) {
            repeat(6) { index ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SkeletonBlock(
                        shimmerBrush = shimmerBrush,
                        width = 48.dp,
                        height = 48.dp,
                        cornerRadius = 14.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        SkeletonBlock(
                            shimmerBrush = shimmerBrush,
                            width = 70.dp,
                            height = 12.dp,
                            cornerRadius = 4.dp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        SkeletonBlock(
                            shimmerBrush = shimmerBrush,
                            width = 150.dp,
                            height = 18.dp,
                            cornerRadius = 4.dp
                        )
                    }
                }

                if (index < 5) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SkeletonBlock(
            shimmerBrush = shimmerBrush,
            width = 110.dp,
            height = 16.dp,
            cornerRadius = 4.dp
        )

        Spacer(modifier = Modifier.height(15.dp))

        SkeletonCard(modifier = Modifier.fillMaxWidth()) {
            repeat(2) { index ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SkeletonBlock(
                        shimmerBrush = shimmerBrush,
                        width = 48.dp,
                        height = 48.dp,
                        cornerRadius = 14.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        SkeletonBlock(
                            shimmerBrush = shimmerBrush,
                            width = 65.dp,
                            height = 12.dp,
                            cornerRadius = 4.dp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        SkeletonBlock(
                            shimmerBrush = shimmerBrush,
                            width = 180.dp,
                            height = 18.dp,
                            cornerRadius = 4.dp
                        )
                    }
                }

                if (index < 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
