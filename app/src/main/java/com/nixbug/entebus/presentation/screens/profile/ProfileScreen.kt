package com.nixbug.entebus.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nixbug.entebus.presentation.components.DialogBox
import com.nixbug.entebus.presentation.navigation.AppNavItem
import com.nixbug.entebus.ui.theme.DisabledButtonColor

@Composable
fun ProfileScreen(
    rootNavController: NavHostController,
    viewModel: ProfileScreenViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showLogOutDialog by remember { mutableStateOf(false) }

    if (showLogOutDialog) {
        DialogBox(
            title = "Logout",
            message = "Are you sure you want to log out of your account?",
            confirmText = "Logout",
            onConfirmClick = {
                showLogOutDialog = false
                viewModel.logout()
            },
            dismissible = true,
            onDismiss = { showLogOutDialog = false },
            showExitButton = true,
            exitText = "Cancel",
            onExitClick = { showLogOutDialog = false },
            titleColor = MaterialTheme.colorScheme.primary,
            confirmBtnColor = MaterialTheme.colorScheme.primary,
            exitBtnBorderColor = MaterialTheme.colorScheme.primary
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(bottom = 80.dp)
        ) {

            ProfileScreenTop(
                modifier = Modifier.weight(0.4f),
                imageId = state.imageId,
                isImageLoading = state.isImageLoading
            )

            ProfileScreenBottom(
                modifier = Modifier.weight(0.6f),
                rootNavController = rootNavController,
                onLogoutClick = { showLogOutDialog = true }
            )

        }
        Version(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .align(Alignment.BottomCenter)
                .padding(bottom = 82.dp)
        )
    }
}

@Composable
fun ProfileScreenTop(modifier: Modifier = Modifier, imageId: Int?, isImageLoading: Boolean) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(
                    Color(0xFFE1E8F4)
                ),
            contentAlignment = Alignment.Center
        ) {
            val context = LocalContext.current
            if (isImageLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
            } else {
                if (imageId != null) {
                    val imageUrl =
                        "https://dev-api.entebus.com/operator/company/account/picture/$imageId"
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageUrl)
                            .memoryCacheKey(imageId.toString())
                            .diskCacheKey(imageId.toString())
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(148.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "A",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Gray
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Amal",
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProfileScreenBottom(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    onLogoutClick: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        ProfileOptionCard(
            leadingIcon = Icons.Default.Person2,
            leadingIconBg = DisabledButtonColor,
            title = "Personal Details",
            subtitle = "View your personal info",
            onClick = { rootNavController.navigate(AppNavItem.PersonalDetailsScreen.route) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        ProfileOptionCard(
            leadingIcon = Icons.Filled.Policy,
            leadingIconBg = DisabledButtonColor,
            title = "Terms & Conditions",
            subtitle = "View our terms and policies",
            onClick = { rootNavController.navigate(AppNavItem.TermsAndConditionsScreen.route) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        ProfileOptionCard(
            leadingIcon = Icons.AutoMirrored.Filled.Logout,
            leadingIconBg = DisabledButtonColor,
            title = "Log Out",
            subtitle = "Sign out from this account",
            onClick = onLogoutClick
        )

        Spacer(modifier = Modifier.height(24.dp))

    }

}

@Composable
fun ProfileOptionCard(
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector,
    leadingIconBg: Color,
    title: String,
    subtitle: String,
    trailingIcon: ImageVector = Icons.Default.DoubleArrow,
    onClick: (() -> Unit)? = null
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = { onClick?.invoke() }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(leadingIconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = trailingIcon,
                contentDescription = "Go to details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun Version(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = "Version 0.1.0",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}
