package com.nixbug.entebus.presentation.screens.authScreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nixbug.entebus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Auth(viewModel: AuthViewModel = hiltViewModel()) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val state = viewModel.uiState.collectAsState().value

    val location = viewModel.location.collectAsState()

    LaunchedEffect(location.value) {
        Log.d(
            "AuthDebug",
            "Latitude=${location.value.latitude}, Longitude=${location.value.longitude}"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp)
                .wrapContentHeight()
                .verticalScroll(scrollState)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Image(
                painter = painterResource(
                    id = R.drawable.entebus_logo
                ),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color.White, CircleShape)
                    .shadow(6.dp, CircleShape),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Entebus Conductor",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color.Black
            )

            Text(
                text = "Sign in to your account",
                fontSize = 18.sp,
                color = Color.Black.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            //Company Drop Down
            CompanyDropdown(
                expanded = state.expanded,
                onExpandChange = viewModel::expandedFunc,
                onSelect = viewModel::onCompanySelected,
                onQueryChange = viewModel::onCompanyQueryChange,
                clearSelectedCompany = viewModel::clearSelectedCompany,
                clearSearchQuery = viewModel::clearSearchQuery,
                uiState = state
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Username Field
            UsernameField(
                value = state.userName,
                onChange = viewModel::onUserNameChange,
                focusManager = focusManager
            )

            Spacer(modifier = Modifier.height(16.dp))

            //password Field
            PasswordField(
                value = state.password,
                onChange = viewModel::onPasswordChange,
                visible = state.passwordVisible,
                toggle = viewModel::onTogglePasswordVisibility
            )

            Spacer(modifier = Modifier.height(20.dp))

            //Sign in Button
            SignInButton(onClick = { /* TODO: Handle sign-in logic*/ })

            Spacer(modifier = Modifier.height(15.dp))

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDropdown(
    uiState: AuthUiState,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit,
    onSelect: (String) -> Unit,
    clearSelectedCompany: () -> Unit,
    clearSearchQuery: () -> Unit
) {
    Text(
        text = "Company",
        fontSize = 16.sp,
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp)
    )
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        LaunchedEffect(expanded, uiState.selectedCompany) {
            if (!expanded && uiState.selectedCompany == null) {
                clearSearchQuery()
            }
            if ((uiState.selectedCompany != uiState.companySearchQuery) && !expanded) {
                clearSelectedCompany()
            }
        }
        OutlinedTextField(
            value = if (expanded) uiState.companySearchQuery else (uiState.selectedCompany ?: ""),
            onValueChange = { onQueryChange(it) },
            singleLine = true,
            readOnly = !expanded,
            placeholder = { Text(if (expanded) "Search your company" else "Select your company") },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(12.dp)),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF47C7FF),
                unfocusedBorderColor = Color(0xFFC0C0C0),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF47C7FF), RoundedCornerShape(12.dp))
                .heightIn(max = 160.dp)
        ) {
            uiState.filteredCompanies.forEach { company ->
                DropdownMenuItem(
                    text = { Text(company) },
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    onClick = {
                        onSelect(company)
                        onExpandChange(false)
                    }
                )
            }
            if (uiState.filteredCompanies.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No results", color = Color.Gray) },
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun UsernameField(value: String, onChange: (String) -> Unit, focusManager: FocusManager) {
    Text(
        text = "Username",
        fontSize = 16.sp,
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp)
    )

    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text("Enter your username") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF47C7FF),
            unfocusedBorderColor = Color(0xFFC0C0C0),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
        )
    )
}

@Composable
fun PasswordField(value: String, onChange: (String) -> Unit, visible: Boolean, toggle: () -> Unit) {
    Text(
        text = "Password",
        fontSize = 16.sp,
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp)
    )

    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text("Enter your password") },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = toggle) {
                Icon(
                    imageVector = if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (visible) "Hide password" else "Show password"
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF47C7FF),
            unfocusedBorderColor = Color(0xFFC0C0C0),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
        )
    )
}

@Composable
fun SignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
        shape = RoundedCornerShape(10.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp,
            focusedElevation = 8.dp
        )
    ) {
        Text("Sign in", color = Color.White, fontSize = 16.sp)
    }
}
