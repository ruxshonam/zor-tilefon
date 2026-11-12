package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = HonorCyan,
    onPrimary = Color.Black,
    secondary = HonorTeal,
    onSecondary = Color.Black,
    tertiary = HonorAccentPink,
    background = HonorDarkBg,
    surface = HonorCardBg,
    onBackground = Color.White,
    onSurface = Color.White,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Color(0xFF0077B6),
    onPrimary = Color.White,
    secondary = Color(0xFF0096C7),
    onSecondary = Color.White,
    tertiary = Color(0xFFE63946),
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek Honor OLED dark UI
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
