package com.nixbug.entebus.presentation.screens.personaldetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nixbug.entebus.presentation.components.AppHeader
import com.nixbug.entebus.ui.theme.DisabledButtonColor
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType

@Composable
fun PersonalDetailsScreen(navController: NavHostController) {

    var isEditMode by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("Amal") }
    var email by remember { mutableStateOf("N/A") }
    var phone by remember { mutableStateOf("N/A") }
    var gender by remember { mutableStateOf("Male") }
    val focusManager = LocalFocusManager.current



    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        AppHeader(
            title = "Personal Details",
            onBackClick = { navController.popBackStack() },
            showTopBarActions = true,
            primaryActionText = "Edit",
            primaryActionIcon = Icons.Default.Edit,
            onPrimaryActionClick = {
                if (isEditMode) {
                    SnackbarManager.show(
                        message = "You are already in edit mode",
                        type = SnackbarType.INFO
                    )
                } else {
                    isEditMode = true
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
        ) {

            SectionTitle(
                title = "Personal Info",
//                showEditButton = true,
//                onEditClick = { isEditMode = true }
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {

                Column(modifier = Modifier.padding(16.dp)) {

                    InfoRow(
                        label = "User Id",
                        value = "1",
                        icon = Icons.Default.Tag,
                        iconBg = DisabledButtonColor,
                        iconTint = MaterialTheme.colorScheme.primary
                    )

                    DividerWithSpacing()

                    InfoRow(
                        label = "Username",
                        value = "amal",
                        icon = Icons.Default.AccountBox,
                        iconBg = DisabledButtonColor,
                        iconTint = MaterialTheme.colorScheme.primary
                    )

                    DividerWithSpacing()

                    EditableInfoRow(
                        label = "Full Name",
                        value = fullName,
                        icon = Icons.Default.Badge,
                        iconBg = DisabledButtonColor,
                        iconTint = MaterialTheme.colorScheme.primary,
                        isEditMode = isEditMode,
                        onValueChange = { fullName = it }
                    )

                    DividerWithSpacing()

                    GenderDropdownRow(
                        label = "Gender",
                        value = gender,
                        icon = Icons.Default.Wc,
                        iconBg = DisabledButtonColor,
                        iconTint = MaterialTheme.colorScheme.primary,
                        isEditMode = isEditMode,
                        onValueChange = { gender = it }
                    )

                    DividerWithSpacing()

                    InfoRow(
                        label = "Status",
                        value = statusToText(1),
                        icon = Icons.Default.Info,
                        iconBg = DisabledButtonColor,
                        iconTint = MaterialTheme.colorScheme.primary
                    )

                    DividerWithSpacing()

                    InfoRow(
                        label = "Created On",
                        value = "25 Dec 2025",
                        icon = Icons.Default.Today,
                        iconBg = DisabledButtonColor,
                        iconTint = MaterialTheme.colorScheme.primary
                    )
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

                    EditableInfoRow(
                        label = "Phone",
                        value = phone,
                        icon = Icons.Default.Phone,
                        iconBg = DisabledButtonColor,
                        iconTint = MaterialTheme.colorScheme.primary,
                        isEditMode = isEditMode,
                        onValueChange = { phone = it }
                    )

                    DividerWithSpacing()

                    EditableInfoRow(
                        label = "Email",
                        value = email,
                        icon = Icons.Default.Email,
                        iconBg = DisabledButtonColor,
                        iconTint = MaterialTheme.colorScheme.primary,
                        isEditMode = isEditMode,
                        onValueChange = { email = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (isEditMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                )
                {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        onClick = { isEditMode = false },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            isEditMode = false
                        },
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

/* ---------------------------------------------------
   Components
--------------------------------------------------- */

@Composable
private fun SectionTitle(
    title: String,
    showEditButton: Boolean = false,
    onEditClick: (() -> Unit)? = null
) {
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

        if (showEditButton) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(DisabledButtonColor, RoundedCornerShape(12.dp))
                    .clickable { onEditClick?.invoke() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
private fun DividerWithSpacing() {
    Spacer(modifier = Modifier.height(12.dp))
    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    )
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
    label: String,
    value: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    isEditMode: Boolean,
    onValueChange: (String) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box (Keep your existing code)
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
                // HIGHLIGHTED CHANGE START
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp), // Set your desired height here
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true,
                    interactionSource = interactionSource,
                ) { innerTextField ->
                    OutlinedTextFieldDefaults.DecorationBox(
                        value = value,
                        innerTextField = innerTextField,
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
                // HIGHLIGHTED CHANGE END
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

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun GenderDropdownRow(
//    label: String,
//    value: String,
//    icon: ImageVector,
//    iconBg: Color,
//    iconTint: Color,
//    isEditMode: Boolean,
//    onValueChange: (String) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//
//        Box(
//            modifier = Modifier
//                .size(48.dp)
//                .background(iconBg, RoundedCornerShape(14.dp)),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(icon, contentDescription = label, tint = iconTint)
//        }
//
//        Spacer(modifier = Modifier.width(16.dp))
//
//        Column(modifier = Modifier.fillMaxWidth()) {
//
//            Text(
//                text = label.uppercase(),
//                style = MaterialTheme.typography.labelMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//
//            Spacer(modifier = Modifier.height(4.dp))
//
//            if (isEditMode) {
//
//                ExposedDropdownMenuBox(
//                    expanded = expanded,
//                    onExpandedChange = { expanded = !expanded }
//                ) {
//
//                    OutlinedTextField(
//                        value = value,
//                        onValueChange = {},
//                        readOnly = true,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .menuAnchor(),
//                        trailingIcon = {
//                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
//                        },
//                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedBorderColor = MaterialTheme.colorScheme.primary,
//                            unfocusedBorderColor = Color.LightGray
//                        ),
//                        shape = RoundedCornerShape(8.dp)
//                    )
//
//                    ExposedDropdownMenu(
//                        expanded = expanded,
//                        onDismissRequest = { expanded = false }
//                    ) {
//                        genderOptions.forEach { option ->
//                            DropdownMenuItem(
//                                text = { Text(option) },
//                                onClick = {
//                                    onValueChange(option)
//                                    expanded = false
//                                }
//                            )
//                        }
//                    }
//                }
//
//            } else {
//                Text(
//                    text = value,
//                    style = MaterialTheme.typography.bodyLarge,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdownRow(
    label: String,
    value: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    isEditMode: Boolean,
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
                        !isEditMode -> Color.Transparent
                        else -> Color.LightGray
                    }

                    // 👇 BasicTextField as anchor
                    BasicTextField(
                        value = value,
                        onValueChange = {},
                        readOnly = true,
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .menuAnchor() // 🔑 REQUIRED
                            .fillMaxWidth()
                            .height(40.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),

                        ) { innerTextField ->

                        // 👇 Custom decoration (same as your other fields)
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
                                contentDescription = null,
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
                        val options = listOf(
                            "Male",
                            "Female",
                            "Transgender",
                            "Other"
                        )
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

fun statusToText(status: Int): String =
    if (status == 1) "Active" else "Inactive"


