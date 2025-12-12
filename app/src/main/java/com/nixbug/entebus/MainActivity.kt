package com.nixbug.entebus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nixbug.entebus.presentation.navigation.AppNavigation
import com.nixbug.entebus.presentation.navigation.AppNavigationViewModel
import com.nixbug.entebus.ui.theme.EntebusandroidoperatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: AppNavigationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            viewModel.locationState.value.isLoading
        }
        enableEdgeToEdge()
        setContent {
            EntebusandroidoperatorTheme {
                AppNavigation(activity = this)
            }
        }
    }
}
