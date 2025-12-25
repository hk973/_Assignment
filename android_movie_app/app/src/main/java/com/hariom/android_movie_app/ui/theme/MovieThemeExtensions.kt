package com.hariom.android_movie_app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Extended color palette for movie-specific UI elements
 * Provides additional colors not covered by Material 3 color scheme
 */
data class MovieColors(
    val favoriteRed: Color,
    val ratingGold: Color,
    val genreChip: Color,
    val onGenreChip: Color,
    val shimmerBase: Color,
    val shimmerHighlight: Color,
    val success: Color,
    val warning: Color
)

/**
 * Light theme movie colors
 */
private val LightMovieColors = MovieColors(
    favoriteRed = MovieRed40,
    ratingGold = MovieGold40,
    genreChip = CinemaGray40,
    onGenreChip = Color.White,
    shimmerBase = Color(0xFFF1F1F1),
    shimmerHighlight = Color.White,
    success = SuccessGreen40,
    warning = WarningYellow40
)

/**
 * Dark theme movie colors
 */
private val DarkMovieColors = MovieColors(
    favoriteRed = MovieRed80,
    ratingGold = MovieGold80,
    genreChip = CinemaGray80,
    onGenreChip = Color.Black,
    shimmerBase = Color(0xFF2A2A2A),
    shimmerHighlight = Color(0xFF3A3A3A),
    success = SuccessGreen80,
    warning = WarningYellow80
)

/**
 * Extension property to access movie-specific colors from MaterialTheme
 */
val MaterialTheme.movieColors: MovieColors
    @Composable
    @ReadOnlyComposable
    get() = if (colorScheme.surface == DarkSurface) DarkMovieColors else LightMovieColors

/**
 * Movie-specific dimensions for consistent spacing
 */
object MovieDimensions {
    // Padding values
    val paddingExtraSmall = 4.dp
    val paddingSmall = 8.dp
    val paddingMedium = 16.dp
    val paddingLarge = 24.dp
    val paddingExtraLarge = 32.dp
    
    // Movie card dimensions
    val movieCardWidth = 160.dp
    val movieCardHeight = 240.dp
    val movieCardElevation = 4.dp
    
    // Movie poster aspect ratio (2:3)
    val posterAspectRatio = 2f / 3f
    
    // Icon sizes
    val iconSmall = 16.dp
    val iconMedium = 24.dp
    val iconLarge = 32.dp
    
    // Corner radius
    val cornerRadiusSmall = 8.dp
    val cornerRadiusMedium = 12.dp
    val cornerRadiusLarge = 16.dp
}