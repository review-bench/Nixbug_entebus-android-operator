package com.nixbug.entebus.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nixbug.entebus.domain.model.local.UiMode
import com.nixbug.entebus.presentation.components.AppHeader
import com.nixbug.entebus.utils.SetStatusBarColor

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SetStatusBarColor(color = Color.White, darkIcons = true)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AppHeader(title = "Settings", onBackClick = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                ScreenPreferenceItem(
                    selectedOption = uiState.selectedUiMode,
                    onOptionSelected = viewModel::onUiModeSelected
                )
            }
        }
    }
}

@Composable
fun ScreenPreferenceItem(
    selectedOption: UiMode,
    onOptionSelected: (UiMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Ticket Screen Preference",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = when (selectedOption) {
                        UiMode.MODERN -> "Modern (Default)"
                        UiMode.LEGACY -> "Legacy"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowRight,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = Color(0xFFF8F9FA),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            DropdownMenuItem(
                text = { Text("Modern (Default)") },
                trailingIcon = {
                    if (selectedOption == UiMode.MODERN) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                    }
                },
                modifier = Modifier.background(
                    if (selectedOption == UiMode.MODERN)
                        Color(0xFFE8F5E9)
                    else
                        Color.Transparent
                ),
                onClick = {
                    onOptionSelected(UiMode.MODERN)
                    expanded = false
                }
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFFE5E7EB)
            )

            DropdownMenuItem(
                text = { Text("Legacy") },
                trailingIcon = {
                    if (selectedOption == UiMode.LEGACY) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                    }
                },
                modifier = Modifier.background(
                    if (selectedOption == UiMode.LEGACY)
                        Color(0xFFE8F5E9)
                    else
                        Color.Transparent
                ),
                onClick = {
                    onOptionSelected(UiMode.LEGACY)
                    expanded = false
                }
            )
        }
    }
}
