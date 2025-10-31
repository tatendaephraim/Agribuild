package com.example.agribid // Correct package

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.agribid.presentation.navigation.AppNavigation // Correct import
import com.example.agribid.presentation.theme.AgriBidTheme // Correct import
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Essential for Hilt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Recommended for modern UI
        setContent {
            AgriBidApp() // Call the main app composable
        }
    }
}

@Composable
fun AgriBidApp() {
    AgriBidTheme { // Apply the custom theme
        val navController = rememberNavController()
        // AppNavigation now relies on Hilt to provide ViewModels to screens directly
        AppNavigation(navController = navController)
    }
}

