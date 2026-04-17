package com.nixbug.entebus.presentation.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.nixbug.entebus.R
import com.nixbug.entebus.domain.model.PublicCompanyModel
import com.nixbug.entebus.presentation.navigation.AppNavItem
import com.nixbug.entebus.utils.LocationPermissionHelper
import com.nixbug.entebus.utils.snackbar.SnackbarManager
import com.nixbug.entebus.utils.snackbar.SnackbarType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel(),
    activity: Activity
) {

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val state = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val usernameFocusRequester = remember { FocusRequester() }

    // The Launcher that handles the result of the System GPS Dialog
    val gpsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.fetchLocation(
                onSuccess = {},
                onError = {}
            )
        } else {
            viewModel.fetchPublicCompanies()
            SnackbarManager.show(
                "Location disabled. Company listings won’t be based on your current location.",
                SnackbarType.WARNING
            )
        }
    }

    // The Logic to check and trigger the System Modal
    val checkAndEnableGps = {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            viewModel.fetchLocation(
                onSuccess = {},
                onError = {}
            )
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution.intentSender).build()
                    gpsLauncher.launch(intentSenderRequest)
                } catch (sendEx: Exception) {
                    SnackbarManager.show(
                        message = "Unable to open GPS settings.",
                        type = SnackbarType.ERROR
                    )
                    viewModel.fetchPublicCompanies()
                }
            } else {
                SnackbarManager.show("Location services unavailable.", SnackbarType.ERROR)
                viewModel.fetchPublicCompanies()
            }
        }
    }

    // TRIGGER: Call the check when the screen loads
    LaunchedEffect(Unit) {
        if (LocationPermissionHelper.isPermissionGranted(context)) {
            checkAndEnableGps()
        } else {
            viewModel.fetchPublicCompanies()
        }
    }

    LaunchedEffect(state.loginError) {

        state.loginError?.let {
            SnackbarManager.show(
                message = it,
                type = SnackbarType.ERROR
            )
        }
    }

    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            navController.navigate(AppNavItem.MainScreen.route) {
                popUpTo(AppNavItem.Auth.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(state.companyListError) {

        state.companyListError?.let {
            SnackbarManager.show(
                message = it,
                type = SnackbarType.ERROR
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
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
        ) {

            Image(
                painter = painterResource(id = R.drawable.entebus_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color.White, CircleShape)
                    .shadow(6.dp, CircleShape),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Entebus Conductor",
                color = Color.Black,
                style = MaterialTheme.typography.headlineSmall,
            )

            Text(
                text = "Sign in to your account",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            CompanyDropdown(
                uiState = state,
                expanded = state.expanded,
                onExpandChange = viewModel::expandedFunc,
                onQueryChange = viewModel::onCompanyQueryChange,
                onSelect = {
                    viewModel.onCompanySelected(it)
                    usernameFocusRequester.requestFocus()
                },
                clearSelectedCompany = viewModel::clearSelectedCompany,
                clearSearchQuery = viewModel::clearSearchQuery,
                showLocationLoader = state.locationFetchLoading,
                showCompanyLoader = state.companyListLoading,
                showSearchLoader = state.companySearchLoading,
                error = state.companyError,
                onLoadMore = { viewModel.fetchPublicCompanies(isPagination = true) }
            )

            Spacer(modifier = Modifier.height(5.dp))

            UsernameField(
                value = state.userName,
                onChange = viewModel::onUserNameChange,
                focusManager = focusManager,
                error = state.userNameError,
                focusRequester = usernameFocusRequester
            )

            Spacer(modifier = Modifier.height(5.dp))

            PasswordField(
                value = state.password,
                onChange = viewModel::onPasswordChange,
                visible = state.passwordVisible,
                toggle = viewModel::onTogglePasswordVisibility,
                error = state.passwordError
            )

            Spacer(modifier = Modifier.height(10.dp))

            SignInButton(
                isLoading = state.isLoading || state.isNavigating,
                onClick = {
                    keyboardController?.hide()
                    viewModel.login()
                },
                isCompanyFetching = state.companyListLoading,
                isLocationFetching = state.locationFetchLoading
            )

            Spacer(modifier = Modifier.height(16.dp))
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
    onSelect: (PublicCompanyModel) -> Unit,
    clearSelectedCompany: () -> Unit,
    clearSearchQuery: () -> Unit,
    showLocationLoader: Boolean,
    showCompanyLoader: Boolean,
    error: String?,
    onLoadMore: () -> Unit,
    showSearchLoader: Boolean,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    FieldName("Company")
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (!showLocationLoader && !showCompanyLoader) onExpandChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (error == null) 12.dp else 0.dp)
    ) {

        LaunchedEffect(expanded, uiState.selectedCompany) {
            if (!expanded && uiState.selectedCompany == null) {
                clearSearchQuery()
            }
            if ((uiState.selectedCompany?.name != uiState.companySearchQuery) && !expanded) {
                clearSelectedCompany()
            }
        }
        LaunchedEffect(expanded) {
            if (expanded) {
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        }

        OutlinedTextField(
            value = if (expanded) uiState.companySearchQuery else uiState.selectedCompany?.name
                ?: "",
            onValueChange = { onQueryChange(it) },
            singleLine = true,
            readOnly = !expanded,
            enabled = !showLocationLoader && !showCompanyLoader,
            placeholder = {
                Text(
                    if (expanded) "Search your company"
                    else if (showLocationLoader) "Fetching your current location"
                    else if (showCompanyLoader) "Fetching companies"
                    else "Select your company",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
            },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
                .focusRequester(focusRequester),
            trailingIcon = {
                if (showLocationLoader || showCompanyLoader || showSearchLoader) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.Gray
                    )
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = defaultTextFieldColors(),
            isError = error != null,
            supportingText = if (error != null) {
                {
                    Text(error)
                }
            } else null
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.8.dp, Color(0xFF47C7FF), RoundedCornerShape(7.dp))
                .heightIn(max = 160.dp),
        ) {
            uiState.companyList.forEachIndexed { index, company ->
                val isSingleItem = uiState.companyList.size == 1
                DropdownMenuItem(
                    text = { Text(company.name) },
                    modifier = Modifier
                        .padding(
                            horizontal = 6.dp,
                            vertical = if (isSingleItem) 2.dp else 0.dp
                        ),
                    onClick = {
                        onSelect(company)
                        onExpandChange(false)
                    }
                )
                if (index < uiState.companyList.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        thickness = 0.5.dp,
                        color = Color(0xFFE0E0E0)
                    )
                }
            }
            if (
                uiState.hasMore &&
                !showCompanyLoader &&
                !uiState.isPaginationLoading &&
                uiState.companyList.isNotEmpty()
                && uiState.companyList.size >= uiState.limit
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "View more...",
                        fontSize = 15.sp,
                        modifier = Modifier
                            .clickable { onLoadMore() }
                            .padding(horizontal = 10.dp),
                        style = MaterialTheme.typography.labelMedium,
                        textDecoration = TextDecoration.Underline,
                        color = Color(0xFF3098DB)
                    )
                }
            }
            if (uiState.companyList.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No results", color = Color.Gray) },
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun UsernameField(
    value: String,
    onChange: (String) -> Unit,
    focusManager: FocusManager,
    error: String?,
    focusRequester: FocusRequester
) {
    FieldName("Username")
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = {
            Text(
                "Enter your username",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )
        },
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(error)
            }
        },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        colors = defaultTextFieldColors()
    )

}

@Composable
fun PasswordField(
    value: String,
    onChange: (String) -> Unit,
    visible: Boolean,
    toggle: () -> Unit,
    error: String?
) {
    FieldName("Password")
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        isError = error != null,
        supportingText = {
            error?.let { Text(it) }
        },
        placeholder = {
            Text(
                "Enter your password",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )
        },
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
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = defaultTextFieldColors()
    )
}

@Composable
fun SignInButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    isCompanyFetching: Boolean,
    isLocationFetching: Boolean
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF8BC34A),
            disabledContainerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 7.dp,
            focusedElevation = 8.dp
        ),
        enabled = !isLoading && !isCompanyFetching && !isLocationFetching
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = Color.White
            )
        } else {
            Text("Sign in", color = Color.White, style = MaterialTheme.typography.titleMedium)
        }

    }
}

@Composable
fun FieldName(name: String) {
    Text(
        text = name,
        color = Color.Black,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp)
    )
}

@Composable
fun defaultTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedBorderColor = Color(0xFF47C7FF),
    unfocusedBorderColor = Color(0xFFC0C0C0),
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    disabledContainerColor = Color.White,
    disabledTextColor = Color.Black,
    disabledBorderColor = Color(0xFFC0C0C0),
)