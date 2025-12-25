package com.hariom.android_movie_app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Dark color scheme for the Movie Showcase app
 * Purple gradient theme inspired by modern streaming apps
 */
private val DarkColorScheme = darkColorScheme(
    // Primary colors - Purple theme
    primary = PurpleAccent,
    onPrimary = Color.White,
    primaryContainer = PurpleMedium,
    onPrimaryContainer = Color.White,
    
    // Secondary colors
    secondary = PurpleLight,
    onSecondary = Color.White,
    secondaryContainer = PurpleMedium,
    onSecondaryContainer = Color.White,
    
    // Tertiary colors
    tertiary = RatingGold,
    onTertiary = Color.Black,
    tertiaryContainer = PurpleDark,
    onTertiaryContainer = Color.White,
    
    // Background colors - Purple gradient
    background = GradientStart,
    onBackground = TextPrimary,
    
    // Surface colors
    surface = PurpleDark,
    onSurface = TextPrimary,
    surfaceVariant = PurpleMedium,
    onSurfaceVariant = TextSecondary,
    
    // Container colors
    surfaceContainer = CardBackground,
    surfaceContainerHigh = PurpleMedium,
    surfaceContainerHighest = PurpleLight,
    
    // Outline colors
    outline = PurpleLight,
    outlineVariant = PurpleMedium,
    
    // Error colors
    error = ErrorRed80,
    onError = Color.Black,
    errorContainer = ErrorRed40,
    onErrorContainer = Color.White,
    
    // Inverse colors
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = PurpleDark,
    inversePrimary = PurpleAccent
)

/**
 * Light color scheme for the Movie Showcase app
 * Clean light theme with cinema-inspired accents
 */
private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = MovieRed40,
    onPrimary = Color.White,
    primaryContainer = MovieRed80,
    onPrimaryContainer = Color.Black,
    
    // Secondary colors
    secondary = CinemaBlue40,
    onSecondary = Color.White,
    secondaryContainer = CinemaBlue80,
    onSecondaryContainer = Color.Black,
    
    // Tertiary colors
    tertiary = AccentOrange40,
    onTertiary = Color.White,
    tertiaryContainer = AccentOrange80,
    onTertiaryContainer = Color.Black,
    
    // Background colors
    background = LightBackground,
    onBackground = OnLightSurface,
    
    // Surface colors
    surface = LightSurface,
    onSurface = OnLightSurface,
    surfaceVariant = Color(0xFFF3F0F4),
    onSurfaceVariant = Color(0xFF49454F),
    
    // Container colors
    surfaceContainer = Color(0xFFF7F2FA),
    surfaceContainerHigh = Color(0xFFF1ECF4),
    surfaceContainerHighest = Color(0xFFECE6F0),
    
    // Outline colors
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    
    // Error colors
    error = ErrorRed40,
    onError = Color.White,
    errorContainer = ErrorRed80,
    onErrorContainer = Color.Black,
    
    // Inverse colors
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = MovieRed80
)

/**
 * Main theme composable for the Movie Showcase app
 * Supports both light and dark themes with dynamic color on Android 12+
 */
@Composable
fun Android_movie_appTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to maintain consistent movie theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = GradientStart.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}