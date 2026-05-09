package com.nixbug.entebus.presentation.components.loadingskeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

@Composable
fun TicketScreenSkeleton(shimmerBrush: Brush) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Row(
            modifier = Modifier
                .weight(0.73f)
                .fillMaxWidth()
        ) {

            // LANDMARKS
            SkeletonCard(
                modifier = Modifier
                    .weight(0.58f)
                    .fillMaxHeight()
                    .padding(horizontal = 5.dp)
            ) {

                repeat(15) { index ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Timeline icon
                        SkeletonBlock(
                            shimmerBrush = shimmerBrush,
                            width = 20.dp,
                            height = 20.dp,
                            cornerRadius = 10.dp
                        )

                        Spacer(Modifier.width(10.dp))

                        Column {

                            // Optional From / Till tag
                            if (index == 1 || index == 6) {
                                SkeletonBlock(
                                    shimmerBrush = shimmerBrush,
                                    width = 40.dp,
                                    height = 10.dp,
                                    cornerRadius = 4.dp
                                )

                                Spacer(Modifier.height(4.dp))
                            }

                            // Landmark name
                            SkeletonBlock(
                                shimmerBrush = shimmerBrush,
                                width = 130.dp,
                                height = 14.dp,
                                cornerRadius = 4.dp
                            )
                        }
                    }

                    if (index < 14) {
                        Spacer(Modifier.height(14.dp))
                    }
                }
            }

            // PASSENGERS
            Column(
                modifier = Modifier
                    .weight(0.42f)
                    .fillMaxHeight()
                    .padding(end = 5.dp)
            ) {

                repeat(4) {

                    SkeletonCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {

                        // Passenger type
                        SkeletonBlock(
                            shimmerBrush = shimmerBrush,
                            width = 90.dp,
                            height = 16.dp,
                            cornerRadius = 4.dp
                        )

                        Spacer(Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // Minus
                            SkeletonBlock(
                                shimmerBrush = shimmerBrush,
                                width = 34.dp,
                                height = 34.dp,
                                cornerRadius = 8.dp
                            )

                            // Count
                            SkeletonBlock(
                                shimmerBrush = shimmerBrush,
                                width = 46.dp,
                                height = 38.dp,
                                cornerRadius = 6.dp
                            )

                            // Plus
                            SkeletonBlock(
                                shimmerBrush = shimmerBrush,
                                width = 34.dp,
                                height = 34.dp,
                                cornerRadius = 8.dp
                            )
                        }
                    }
                }
            }
        }

        // BOTTOM SUMMARY
        SkeletonCard(
            modifier = Modifier
                .weight(0.27f)
                .padding(10.dp, 8.dp)
                .fillMaxWidth()
        ) {

            // Route
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                SkeletonBlock(
                    shimmerBrush = shimmerBrush,
                    width = 20.dp,
                    height = 20.dp,
                    cornerRadius = 10.dp
                )

                Spacer(Modifier.width(8.dp))

                SkeletonBlock(
                    shimmerBrush = shimmerBrush,
                    width = 180.dp,
                    height = 14.dp,
                    cornerRadius = 4.dp
                )
            }

            Spacer(Modifier.height(12.dp))

            HorizontalDivider()

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {

                    SkeletonBlock(
                        shimmerBrush = shimmerBrush,
                        width = 70.dp,
                        height = 10.dp
                    )

                    Spacer(Modifier.height(6.dp))

                    SkeletonBlock(
                        shimmerBrush = shimmerBrush,
                        width = 80.dp,
                        height = 18.dp
                    )
                }

                Column {

                    SkeletonBlock(
                        shimmerBrush = shimmerBrush,
                        width = 60.dp,
                        height = 10.dp
                    )

                    Spacer(Modifier.height(6.dp))

                    SkeletonBlock(
                        shimmerBrush = shimmerBrush,
                        width = 70.dp,
                        height = 18.dp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Print button
            SkeletonBlock(
                shimmerBrush = shimmerBrush,
                width = 220.dp,
                height = 45.dp,
                cornerRadius = 13.dp
            )
        }
    }
}

@Composable
fun SummaryScreenSkeleton(
    shimmerBrush: Brush
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(bottom = 75.dp)
    ) {

        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(bottom = 12.dp)
        ) {

            SkeletonBlock(
                shimmerBrush = shimmerBrush,
                width = 180.dp,
                height = 22.dp,
                cornerRadius = 4.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            SkeletonBlock(
                shimmerBrush = shimmerBrush,
                width = 240.dp,
                height = 14.dp,
                cornerRadius = 4.dp
            )
        }

        // Cards
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {

            items(4) {
                SummaryCardSkeleton(
                    shimmerBrush = shimmerBrush
                )
            }
        }
    }
}


@Composable
fun SummaryCardSkeleton(
    shimmerBrush: Brush
) {

    SkeletonCard(
        modifier = Modifier.fillMaxWidth()
    ) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                SkeletonBlock(
                    shimmerBrush = shimmerBrush,
                    width = 180.dp,
                    height = 18.dp
                )

                Spacer(modifier = Modifier.height(6.dp))

                SkeletonBlock(
                    shimmerBrush = shimmerBrush,
                    width = 90.dp,
                    height = 14.dp
                )

                Spacer(modifier = Modifier.height(6.dp))

                SkeletonBlock(
                    shimmerBrush = shimmerBrush,
                    width = 220.dp,
                    height = 12.dp
                )
            }

            SkeletonBlock(
                shimmerBrush = shimmerBrush,
                width = 22.dp,
                height = 22.dp,
                cornerRadius = 4.dp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Operators
        repeat(3) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                SkeletonBlock(
                    shimmerBrush = shimmerBrush,
                    width = 100.dp,
                    height = 14.dp
                )

                SkeletonBlock(
                    shimmerBrush = shimmerBrush,
                    width = 60.dp,
                    height = 14.dp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(6.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(8.dp))

        // Total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            SkeletonBlock(
                shimmerBrush = shimmerBrush,
                width = 120.dp,
                height = 16.dp
            )

            SkeletonBlock(
                shimmerBrush = shimmerBrush,
                width = 70.dp,
                height = 18.dp
            )
        }
    }
}
