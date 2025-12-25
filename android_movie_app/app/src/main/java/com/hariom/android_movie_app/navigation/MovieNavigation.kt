package com.hariom.android_movie_app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hariom.android_movie_app.ui.screens.favorites.FavoritesScreen
import com.hariom.android_movie_app.ui.screens.moviedetail.MovieDetailScreen
import com.hariom.android_movie_app.ui.screens.movies.MoviesScreenWithPaging

/**
 * Main navigation component for the Movie Showcase app
 * Handles navigation between different screens with animations
 */
@Composable
fun MovieNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Movies.route,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + 
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) +
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
        }
    ) {
        composable(
            route = Screen.Movies.route,
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300))
            }
        ) {
            MoviesScreenWithPaging(
                onMovieClick = { movie ->
                    navController.navigate(Screen.MovieDetail.createRoute(movie.id))
                },
                onFavoritesClick = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }
        
        composable(
            route = Screen.MovieDetail.route,
            arguments = Screen.MovieDetail.arguments,
            enterTransition = {
                fadeIn(animationSpec = tween(400)) +
                scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(400)) +
                scaleOut(
                    targetScale = 0.9f,
                    animationSpec = tween(400)
                )
            }
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            MovieDetailScreen(
                movieId = movieId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.Favorites.route,
            enterTransition = {
                fadeIn(animationSpec = tween(300)) +
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300)) +
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                )
            }
        ) {
            FavoritesScreen(
                onMovieClick = { movie ->
                    navController.navigate(Screen.MovieDetail.createRoute(movie.id))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}