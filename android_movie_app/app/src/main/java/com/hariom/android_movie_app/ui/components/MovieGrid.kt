package com.hariom.android_movie_app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kmplibrary.data.models.domain.Movie
import com.hariom.android_movie_app.ui.theme.Android_movie_appTheme
import com.hariom.android_movie_app.ui.theme.MovieDimensions

/**
 * Movie grid component - displays a grid of movie cards with pagination
 * Responsive grid that adapts to screen size
 */
@Composable
fun MovieGrid(
    movies: List<Movie>,
    favorites: Set<Int>,
    onMovieClick: (Movie) -> Unit,
    onFavoriteClick: (Movie) -> Unit,
    onMovieLongPress: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(MovieDimensions.paddingMedium),
    onLoadMore: (() -> Unit)? = null,
    isLoadingMore: Boolean = false,
    hasMorePages: Boolean = true
) {
    val gridState = rememberLazyGridState()
    
    // Detect when user scrolls near the bottom
    val shouldLoadMore = remember {
        derivedStateOf {
            if (!hasMorePages || isLoadingMore || movies.isEmpty()) {
                false
            } else {
                val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()
                val totalItems = gridState.layoutInfo.totalItemsCount
                lastVisibleItem != null && lastVisibleItem.index >= totalItems - 6
            }
        }
    }
    
    // Trigger load more when scrolling near bottom
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onLoadMore?.invoke()
        }
    }
    
    LazyVerticalGrid(
        columns = GridCells.Adaptive(MovieDimensions.movieCardWidth),
        state = gridState,
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(MovieDimensions.paddingSmall),
        verticalArrangement = Arrangement.spacedBy(MovieDimensions.paddingMedium)
    ) {
        items(
            items = movies,
            key = { movie -> movie.id }
        ) { movie ->
            MovieCard(
                movie = movie,
                isFavorite = favorites.contains(movie.id),
                onMovieClick = { onMovieClick(movie) },
                onFavoriteClick = { onFavoriteClick(movie) },
                onLongPress = { onMovieLongPress(movie) }
            )
        }
        
        // Loading indicator at the bottom
        if (isLoadingMore && hasMorePages) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

/**
 * Preview for MovieGrid
 */
@Preview(name = "Movie Grid")
@Composable
private fun MovieGridPreview() {
    val sampleMovies = listOf(
        Movie(
            id = 1,
            title = "The Dark Knight",
            overview = "Batman faces the Joker in this epic superhero film.",
            posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
            backdropPath = null,
            releaseDate = "2008-07-18",
            voteAverage = 9.0,
            genreIds = listOf(28, 80, 18),
            isFavorite = false
        ),
        Movie(
            id = 2,
            title = "Inception",
            overview = "A thief who steals corporate secrets through dream-sharing technology.",
            posterPath = "/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg",
            backdropPath = null,
            releaseDate = "2010-07-16",
            voteAverage = 8.8,
            genreIds = listOf(28, 878, 53),
            isFavorite = true
        ),
        Movie(
            id = 3,
            title = "Interstellar",
            overview = "A team of explorers travel through a wormhole in space.",
            posterPath = null,
            backdropPath = null,
            releaseDate = "2014-11-07",
            voteAverage = 8.6,
            genreIds = listOf(18, 878),
            isFavorite = false
        ),
        Movie(
            id = 4,
            title = "The Matrix",
            overview = "A computer hacker learns about the true nature of reality.",
            posterPath = "/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg",
            backdropPath = null,
            releaseDate = "1999-03-31",
            voteAverage = 8.7,
            genreIds = listOf(28, 878),
            isFavorite = true
        )
    )
    
    Android_movie_appTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MovieGrid(
                movies = sampleMovies,
                favorites = setOf(2, 4),
                onMovieClick = { },
                onFavoriteClick = { },
                onMovieLongPress = { }
            )
        }
    }
}