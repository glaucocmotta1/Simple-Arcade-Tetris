package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArcadeColorScheme = darkColorScheme(
  primary = ArcadeCyan,
  onPrimary = ArcadeBackground,
  primaryContainer = ArcadePurple,
  onPrimaryContainer = ArcadeText,
  secondary = ArcadeGold,
  onSecondary = ArcadeBackground,
  tertiary = ArcadePink,
  background = ArcadeBackground,
  onBackground = ArcadeText,
  surface = ArcadeSurface,
  onSurface = ArcadeText,
  surfaceVariant = ArcadeSurface,
  onSurfaceVariant = ArcadeTextSecondary
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = ArcadeColorScheme,
    typography = Typography,
    content = content
  )
}
