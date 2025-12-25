package com.hariom.android_movie_app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Utility functions for theme-related operations
 */
object ThemeUtils {
    
    /**
     * Determines if a color is considered "light" based on its luminance
     */
    fun Color.isLight(): Boolean = luminance() > 0.5
    
    /**
     * Returns appropriate text color (black or white) based on background color
     */
    fun Color.contrastingTextColor(): Color = if (isLight()) Color.Black else Color.White
    
    /**
     * Creates a semi-transparent version of the color
     */
    fun Color.withAlpha(alpha: Float): Color = copy(alpha = alpha)
}

/**
 * Common alpha values for consistent transparency across the app
 */
object MovieAlpha {
    const val Disabled = 0.38f
    const val Medium = 0.6f
    const val High = 0.87f
    const val Overlay = 0.5f
    const val Shimmer = 0.1f
}

/**
 * Provides theme-aware colors for common movie app elements
 */
@Composable
fun getMovieCardColors() = CardDefaults.cardColors(
    containerColor = MaterialTheme.colorScheme.surface,
    contentColor = MaterialTheme.colorScheme.onSurface
)

@Composable
fun getFavoriteButtonColors() = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.movieColors.favoriteRed,
    contentColor = Color.White
)

@Composable
fun getRatingColors() = Triple(
    first = MaterialTheme.movieColors.ratingGold,
    second = Color.Black,
    third = MaterialTheme.movieColors.ratingGold.copy(alpha = 0.5f)
)

@Composable
fun getGenreChipColors() = AssistChipDefaults.assistChipColors(
    containerColor = MaterialTheme.movieColors.genreChip,
    labelColor = MaterialTheme.movieColors.onGenreChip
)

/**
 * Extension functions for common theme operations
 */
@Composable
fun isDarkTheme(): Boolean = isSystemInDarkTheme()

@Composable
fun getShimmerColors() = listOf(
    MaterialTheme.movieColors.shimmerBase,
    MaterialTheme.movieColors.shimmerHighlight,
    MaterialTheme.movieColors.shimmerBase
)