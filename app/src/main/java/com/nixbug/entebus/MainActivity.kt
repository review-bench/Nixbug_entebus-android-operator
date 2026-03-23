package com.nixbug.entebus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.nixbug.entebus.presentation.navigation.AppNavigation
import com.nixbug.entebus.presentation.navigation.AppNavigationViewModel
import com.nixbug.entebus.ui.theme.EntebusandroidoperatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AppNavigationViewModel = hiltViewModel()
            val startDestination by viewModel.startDestination.collectAsState()

            splashScreen.setKeepOnScreenCondition {
                startDestination == null
            }
            EntebusandroidoperatorTheme {
                AppNavigation(activity = this)
            }
        }
    }
}
