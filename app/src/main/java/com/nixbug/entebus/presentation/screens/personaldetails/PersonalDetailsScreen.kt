package com.nixbug.entebus.presentation.screens.personaldetails

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nixbug.entebus.presentation.components.AppHeader
import com.nixbug.entebus.presentation.components.DialogBox
import com.nixbug.entebus.presentation.components.loadingskeleton.PersonalDetailsSkeleton
import com.nixbug.entebus.presentation.components.loadingskeleton.rememberShimmerBrush
import com.nixbug.entebus.ui.theme.DisabledButtonColor
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType

@Composable
fun PersonalDetailsScreen(
    navController: NavHostController,
    viewModel: PersonalDetailsScreenViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var showDiscardDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = uiState.isEditMode) {
        showDiscardDialog = true
    }

    if (showDiscardDialog) {
        DialogBox(
            title = "Discard changes?",
            message = "Changes have not been saved. Leaving this page will discard them.",
            confirmText = "Discard",
            onConfirmClick = {
                showDiscardDialog = false
                navController.popBackStack()
            },
            dismissible = true,
            onDismiss = { showDiscardDialog = false },
            showExitButton = true,
            exitText = "Keep Editing",
            onExitClick = { showDiscardDialog = false },
            titleColor = MaterialTheme.colorScheme.primary,
            confirmBtnColor = MaterialTheme.colorScheme.primary,
            exitBtnBorderColor = MaterialTheme.colorScheme.primary
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        AppHeader(
            title = "Personal Details",
            onBackClick = {

                navController.popBackStack()

            },
            showTopBarActions = true,
            primaryActionText = "Edit",
            primaryActionIcon = Icons.Default.Edit,
            onPrimaryActionClick = {
                if (uiState.isEditMode) {
                    SnackbarManager.show(
                        message = "You are already in edit mode",
                        type = SnackbarType.INFO
                    )
                } else {
                    viewModel.onStartEdit()
                }
            },
            paddingEnd = 16.dp
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        )
        {
            if (uiState.isLoading && !uiState.isEditMode) {
                val shimmerBrush = rememberShimmerBrush()
                PersonalDetailsSkeleton(shimmerBrush = shimmerBrush)
            } else {
                /* ---------- Personal Information ---------- */
                SectionTitle(title = "Personal Info")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        if (!uiState.isEditMode) {
                            // ID
                            InfoRow(
                                label = "User Id",
                                value = uiState.id?.toString() ?: "N/A",
                                icon = Icons.Default.Tag,
                                iconBg = DisabledButtonColor,
                                iconTint = MaterialTheme.colorScheme.primary
                            )

                            DividerWithSpacing()
                            // User Name
                            InfoRow(
                                label = "Username",
                                value = uiState.username ?: "N/A",
                                icon = Icons.Default.AccountBox,
                                iconBg = DisabledButtonColor,
                                iconTint = MaterialTheme.colorScheme.primary
                            )

                            DividerWithSpacing()
                        }

                        // Full Name
                        EditableInfoRow(
                            placeHolder = "Enter Your Full Name",
                            label = "Full Name",
                            icon = Icons.Default.Badge,
                            iconBg = DisabledButtonColor,
                            iconTint = MaterialTheme.colorScheme.primary,
                            isEditMode = uiState.isEditMode,
                            value = if (uiState.isEditMode) uiState.editedFullName else uiState.fullName,
                            onValueChange = viewModel::onFullNameChange,
                            error = uiState.fullNameError
                        )

                        DividerWithSpacing()

                        // Gender
                        GenderDropdownRow(
                            label = "Gender",
                            value = uiState.genderText,
                            icon = Icons.Default.Wc,
                            iconBg = DisabledButtonColor,
                            iconTint = MaterialTheme.colorScheme.primary,
                            isEditMode = uiState.isEditMode,
                            options = listOf("Male", "Female", "Other", "Transgender"),
                            onValueChange = viewModel::onGenderChange
                        )

                        if (!uiState.isEditMode) {
                            DividerWithSpacing()

                            // Status
                            InfoRow(
                                label = "Status",
                                value = uiState.statusText,
                                icon = Icons.Default.Info,
                                iconBg = DisabledButtonColor,
                                iconTint = MaterialTheme.colorScheme.primary
                            )

                            DividerWithSpacing()

                            // Created On
                            InfoRow(
                                label = "Created On",
                                value = uiState.createdOnText,
                                icon = Icons.Default.Today,
                                iconBg = DisabledButtonColor,
                                iconTint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                /* ---------- Contact Information ---------- */
                SectionTitle("Contact Info")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        // Phone Number
                        EditableInfoRow(
                            placeHolder = "Enter Your Phone Number",
                            label = "Phone",
                            value = if (uiState.isEditMode) uiState.editedPhoneNumber else uiState.formattedPhoneNumber,
                            icon = Icons.Default.Phone,
                            iconBg = DisabledButtonColor,
                            iconTint = MaterialTheme.colorScheme.primary,
                            isEditMode = uiState.isEditMode,
                            onValueChange = viewModel::onPhoneChange,
                            error = uiState.phoneError,
                            isPhoneField = true
                        )

                        DividerWithSpacing()

                        // Email
                        EditableInfoRow(
                            placeHolder = "Enter Your Email",
                            label = "Email",
                            value = if (uiState.isEditMode) uiState.editedEmailId else uiState.emailId,
                            icon = Icons.Default.Email,
                            iconBg = DisabledButtonColor,
                            iconTint = MaterialTheme.colorScheme.primary,
                            isEditMode = uiState.isEditMode,
                            onValueChange = viewModel::onEmailChange,
                            error = uiState.emailError
                        )
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))

                /* ---------- Save & Cancel Buttons ---------- */
                if (uiState.isEditMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    )
                    {

                        // Cancel Button
                        OutlinedButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            onClick = {},
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                text = "Cancel",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Save Changes Button
                        Button(
                            onClick = { viewModel.onSaveClick() },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "Save Changes",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DividerWithSpacing() {
    Spacer(modifier = Modifier.height(12.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconBg, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableInfoRow(
    placeHolder: String,
    label: String,
    value: String?,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    isEditMode: Boolean,
    onValueChange: (String) -> Unit,
    error: String? = null,
    isPhoneField: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconBg, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = iconTint)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isEditMode) {
                BasicTextField(
                    value = value ?: "",
                    onValueChange = {
                        if (isPhoneField) {
                            if (it.length <= 10 && it.all { ch -> ch.isDigit() }) {
                                onValueChange(it)
                            }
                        } else {
                            onValueChange(it)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .onFocusChanged {
                            isFocused = it.isFocused
                        },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true,
                    interactionSource = interactionSource,
                ) { innerTextField ->
                    OutlinedTextFieldDefaults.DecorationBox(
                        value = value ?: "",
                        placeholder = {
                            if (!(isPhoneField && (isFocused || !value.isNullOrEmpty()))) {
                                Text(
                                    placeHolder,
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        },
                        innerTextField = {
                            Row(verticalAlignment = Alignment.CenterVertically) {

                                if (isPhoneField && (isFocused || !value.isNullOrEmpty())) {
                                    Text(
                                        text = "+91 ",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                innerTextField()
                            }
                        },
                        enabled = true,
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = interactionSource,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        container = {
                            OutlinedTextFieldDefaults.Container(
                                enabled = true,
                                isError = false,
                                interactionSource = interactionSource,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(8.dp),
                                focusedBorderThickness = 1.dp,
                                unfocusedBorderThickness = 1.dp,
                            )
                        }
                    )
                }
                if (error != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            } else {
                if (value != null) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.DoNotDisturb,
                        contentDescription = "Not Available",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdownRow(
    label: String,
    value: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    isEditMode: Boolean,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconBg, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = iconTint)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {

            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isEditMode) {

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {

                    val borderColor = when {
                        expanded -> MaterialTheme.colorScheme.primary
                        else -> Color.LightGray
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = {},
                        readOnly = true,
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .height(40.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),

                        ) { innerTextField ->

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    1.dp,
                                    borderColor,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            innerTextField()
                            Icon(
                                imageVector = if (expanded) {
                                    Icons.Default.ArrowDropUp
                                } else {
                                    Icons.Default.ArrowDropDown
                                },
                                contentDescription = if (expanded) "Collapse dropdown" else "Expand dropdown",
                                modifier = Modifier.align(Alignment.CenterEnd),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        containerColor = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        options.forEachIndexed { index, option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onValueChange(option)
                                    expanded = false
                                }
                            )
                            if (index != options.lastIndex) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

            } else {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
