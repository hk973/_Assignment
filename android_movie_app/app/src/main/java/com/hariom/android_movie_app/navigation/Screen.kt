package com.hariom.android_movie_app.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Sealed class representing different screens in the app
 * Provides type-safe navigation routes
 */
sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    
    /**
     * Movies list screen - main screen showing popular movies
     */
    object Movies : Screen("movies")
    
    /**
     * Movie detail screen - shows detailed information about a specific movie
     */
    object MovieDetail : Screen(
        route = "movie_detail/{movieId}",
        arguments = listOf(
            navArgument("movieId") {
                type = NavType.IntType
            }
        )
    ) {
        fun createRoute(movieId: Int): String {
            return "movie_detail/$movieId"
        }
    }
    
    /**
     * Favorites screen - shows user's favorite movies
     */
    object Favorites : Screen("favorites")
}