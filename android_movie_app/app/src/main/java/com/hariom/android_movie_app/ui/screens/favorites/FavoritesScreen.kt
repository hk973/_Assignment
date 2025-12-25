package com.hariom.android_movie_app.ui.screens.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.presentation.viewmodel.FavoritesViewModel
import com.hariom.android_movie_app.ui.components.MovieGrid
import com.hariom.android_movie_app.ui.components.NoFavoritesEmptyState
import org.koin.androidx.compose.koinViewModel

/**
 * Favorites screen composable - displays user's favorite movies
 * Features:
 * - Grid layout of favorite movie cards
 * - Empty state when no favorites
 * - Loading and error states
 * - Remove from favorites functionality
 * - Back navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onMovieClick: (Movie) -> Unit,
    onBackClick: () -> Unit,
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Favorites",
                        style = MaterialTheme.typography.headlineMedium
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    // Loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                uiState.error != null && uiState.favorites.isEmpty() -> {
                    // Error state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = uiState.error ?: "Unknown error",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { viewModel.loadFavorites() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                
                uiState.favorites.isEmpty() -> {
                    // Empty state
                    NoFavoritesEmptyState()
                }
                
                else -> {
                    // Favorites grid
                    MovieGrid(
                        movies = uiState.favorites,
                        favorites = uiState.favorites.map { it.id }.toSet(),
                        onMovieClick = onMovieClick,
                        onFavoriteClick = { movie -> viewModel.toggleFavorite(movie) },
                        onMovieLongPress = { /* Handle long press if needed */ },
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 16.dp
                        )
                    )
                }
            }
            
            // Show error snackbar if there's an error but favorites are still displayed
            if (uiState.error != null && uiState.favorites.isNotEmpty()) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(uiState.error ?: "Unknown error")
                }
            }
        }
    }
}