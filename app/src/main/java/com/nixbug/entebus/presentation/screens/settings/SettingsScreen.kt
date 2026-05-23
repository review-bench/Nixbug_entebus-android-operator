package com.nixbug.entebus.presentation.screens.settings

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
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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

    SetStatusBarColor(
        color = Color.White,
        darkIcons = true
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AppHeader(
            title = "Settings",
            onBackClick = { navController.popBackStack() }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
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
                    text = "Screen Preference",
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
            modifier = Modifier
                .fillMaxWidth(0.92f)
        ) {

            DropdownMenuItem(
                text = { Text("Modern (Default)") },
                onClick = {
                    onOptionSelected(UiMode.MODERN)
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Legacy") },
                onClick = {
                    onOptionSelected(UiMode.LEGACY)
                    expanded = false
                }
            )
        }
    }
}
