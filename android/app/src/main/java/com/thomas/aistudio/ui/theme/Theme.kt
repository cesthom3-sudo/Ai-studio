package com.thomas.aistudio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    background = BgDark,
    surface = SurfaceDark,
    surfaceVariant = CardDark,
    primary = AccentPurple,
    secondary = AccentPink,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onPrimary = TextPrimary
)

private val LightColors = lightColorScheme(
    primary = AccentPurple,
    secondary = AccentPink
)

@Composable
fun ThomasAiStudioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        content = content
    )
}
