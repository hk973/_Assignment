package com.hariom.android_movie_app.ui.screens.movies

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
    viewModel: MoviesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var showFavoritesOnly by rememberSaveable { mutableStateOf(false) }

    val displayedMovies = remember(
        uiState.movies,
        uiState.favorites,
        showFavoritesOnly
    ) {
        if (showFavoritesOnly) {
            uiState.movies.filter { movie ->
                uiState.favorites.contains(movie.id)
            }
        } else {
            uiState.movies
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (showFavoritesOnly) "Movies" else "Movies",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showFavoritesOnly = !showFavoritesOnly }
                    ) {
                        Icon(
                            imageVector = if (showFavoritesOnly)
                                Icons.Default.Favorite
                            else
                                Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle favorites",
                            tint = if (showFavoritesOnly)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.searchMovies(it) },
                placeholder = "Search movies..."
            )

            Box(modifier = Modifier.fillMaxSize()) {

                when {
                    uiState.isLoading && displayedMovies.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.error != null && displayedMovies.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.error ?: "Unknown error",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    displayedMovies.isEmpty() && showFavoritesOnly -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No favorite movies found.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    else -> {
                        MovieGrid(
                            movies = displayedMovies,
                            favorites = uiState.favorites,
                            onMovieClick = onMovieClick,
                            onFavoriteClick = { movie ->
                                viewModel.toggleFavorite(movie)
                            },
                            onMovieLongPress = {},
                            onLoadMore = {
                                if (!showFavoritesOnly && !uiState.isSearching) {
                                    viewModel.loadMoreMovies()
                                }
                            },
                            isLoadingMore = uiState.isLoading && displayedMovies.isNotEmpty(),
                            hasMorePages = !showFavoritesOnly && uiState.hasMorePages,
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp,
                                bottom = 16.dp
                            )
                        )
                    }
                }
            }
        }
    }
}
