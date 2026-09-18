package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EgyptooColorScheme = darkColorScheme(
    primary = EgyptooPrimary,
    onPrimary = Color.White,
    primaryContainer = EgyptooBubbleUser,
    onPrimaryContainer = EgyptooTextPrimary,
    secondary = EgyptooSecondary,
    onSecondary = Color.Black,
    secondaryContainer = EgyptooDarkCardHover,
    onSecondaryContainer = EgyptooTextPrimary,
    tertiary = EgyptooTertiary,
    onTertiary = Color.White,
    background = EgyptooDarkBg,
    onBackground = EgyptooTextPrimary,
    surface = EgyptooDarkCard,
    onSurface = EgyptooTextPrimary,
    surfaceVariant = EgyptooDarkCardHover,
    onSurfaceVariant = EgyptooTextSecondary,
    outline = EgyptooBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EgyptooColorScheme,
        typography = Typography,
        content = content
    )
}
