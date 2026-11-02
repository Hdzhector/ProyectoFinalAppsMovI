package com.aquihay.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Colors = lightColorScheme(
    primary = Color(0xFF086746),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD5F3E0),
    onPrimaryContainer = Color(0xFF093D2B),
    secondary = Color(0xFF4D655A),
    secondaryContainer = Color(0xFFE4F0E9),
    background = Color(0xFFF8FAF8),
    onBackground = Color(0xFF17221C),
    surface = Color.White,
    onSurface = Color(0xFF17221C),
    surfaceVariant = Color(0xFFE7EFEB),
    outline = Color(0xFF718178),
    error = Color(0xFFB3261E)
)

@Composable
fun AquiHayTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colors, content = content)
}
