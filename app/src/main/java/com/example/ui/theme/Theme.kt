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

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccentGlow,
    onPrimary = SlateDark950,
    primaryContainer = SlateDark800,
    onPrimaryContainer = CyanAccentLight,
    secondary = SecurityEmerald,
    onSecondary = Color.White,
    secondaryContainer = SecurityEmeraldDark,
    onSecondaryContainer = SecurityEmeraldContainer,
    tertiary = LegalAmber,
    onTertiary = SlateDark950,
    background = SlateDark950,
    onBackground = TextPrimaryDark,
    surface = SlateDark900,
    onSurface = TextPrimaryDark,
    surfaceVariant = CardBackgroundDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = CardBorderDark,
    error = AlertCrimson,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CyanAccent,
    onPrimary = Color.White,
    primaryContainer = CyanAccentLight,
    onPrimaryContainer = SlateDark950,
    secondary = SecurityEmerald,
    onSecondary = Color.White,
    secondaryContainer = SecurityEmeraldContainer,
    onSecondaryContainer = SecurityEmeraldDark,
    tertiary = LegalAmberDark,
    onTertiary = Color.White,
    background = Color(0xFFF1F5F9),
    onBackground = SlateDark950,
    surface = Color.White,
    onSurface = SlateDark950,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = SlateDark700,
    outline = Color(0xFFCBD5E1),
    error = AlertCrimson,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to high-contrast Dark theme preferred by analysts
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
