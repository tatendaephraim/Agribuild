package com.example.agribid.presentation.theme // <-- FIX: Package name corrected

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = LightGreen,
    onPrimaryContainer = DarkGreen,
    secondary = AccentBrown,
    onSecondary = Color.White,
    background = LightGray,
    onBackground = DarkGray,
    surface = Color.White,
    onSurface = DarkGray,
    surfaceVariant = LightGray,
    onSurfaceVariant = DarkGray,
    tertiaryContainer = Color(0xFFFFF0E0), // For breakdown card
    onTertiaryContainer = DarkGray
)

private val DarkColorScheme = darkColorScheme(
    primary = LightGreen,
    onPrimary = DarkGreen,
    primaryContainer = DarkGreen,
    onPrimaryContainer = LightGreen,
    secondary = AccentBrown,
    onSecondary = Color.White,
    background = DarkGray,
    onBackground = LightGray,
    surface = Color(0xFF212121),
    onSurface = LightGray,
    surfaceVariant = Color(0xFF333333),
    onSurfaceVariant = LightGray,
    tertiaryContainer = Color(0xFF5A4433), // For breakdown card
    onTertiaryContainer = LightGray
)

@Composable
fun AgriBidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
