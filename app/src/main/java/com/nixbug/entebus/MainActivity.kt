package com.nixbug.entebus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nixbug.entebus.presentation.authScreen.Auth
import com.nixbug.entebus.ui.theme.EntebusandroidoperatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EntebusandroidoperatorTheme {
                    Auth()
            }
        }
    }
}
