package com.hariom.android_movie_app.ui.screens.movies

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.presentation.viewmodel.MoviesViewModel
import com.hariom.android_movie_app.ui.components.MovieGrid
import com.hariom.android_movie_app.ui.components.NoSearchResultsEmptyState
import com.hariom.android_movie_app.ui.components.SearchBar
import org.koin.androidx.compose.koinViewModel

/**
 * Movies screen composable - displays list of movies with search and favorites
 * Features:
 * - Grid layout of movie cards
 * - Search bar with debounced input
 * - Pull-to-refresh functionality
 * - Loading and error states
 * - Empty state for no search results
 * - Favorites button in top bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    onMovieClick: (Movie) -> Unit,
    onFavoritesClick: () -> Unit,
    viewModel: MoviesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Movies",
                        style = MaterialTheme.typography.headlineMedium
                    ) 
                },
                actions = {
                    IconButton(onClick = onFavoritesClick) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = MaterialTheme.colorScheme.primary
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.searchMovies(it) },
                placeholder = "Search movies..."
            )
            
            // Content area with pull-to-refresh
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading && uiState.movies.isEmpty() -> {
                        // Initial loading state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    
                    uiState.error != null && uiState.movies.isEmpty() -> {
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
                                Button(onClick = { viewModel.loadMovies() }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    
                    uiState.movies.isEmpty() && uiState.isSearching -> {
                        // Empty search results
                        NoSearchResultsEmptyState()
                    }
                    
                    else -> {
                        // Movies grid with pagination
                        MovieGrid(
                            movies = uiState.movies,
                            favorites = uiState.favorites,
                            onMovieClick = onMovieClick,
                            onFavoriteClick = { movie -> viewModel.toggleFavorite(movie) },
                            onMovieLongPress = { /* Handle long press if needed */ },
                            onLoadMore = { 
                                if (!uiState.isSearching) {
                                    viewModel.loadMoreMovies()
                                }
                            },
                            isLoadingMore = uiState.isLoading && uiState.movies.isNotEmpty(),
                            hasMorePages = uiState.hasMorePages,
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp,
                                bottom = 16.dp
                            )
                        )
                    }
                }
                
                // Show error snackbar if there's an error but movies are still displayed
                if (uiState.error != null && uiState.movies.isNotEmpty()) {
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
}