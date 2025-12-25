package com.hariom.android_movie_app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material 3 Shapes for Movie Showcase app
 * Defines corner radius for different UI components
 */
val Shapes = Shapes(
    // Extra small - for chips, small buttons
    extraSmall = RoundedCornerShape(4.dp),
    
    // Small - for cards, small containers
    small = RoundedCornerShape(8.dp),
    
    // Medium - for dialogs, larger cards
    medium = RoundedCornerShape(12.dp),
    
    // Large - for bottom sheets, large containers
    large = RoundedCornerShape(16.dp),
    
    // Extra large - for full-screen dialogs
    extraLarge = RoundedCornerShape(28.dp)
)