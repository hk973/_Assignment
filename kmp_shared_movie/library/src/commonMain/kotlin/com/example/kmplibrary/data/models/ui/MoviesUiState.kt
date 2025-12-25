package com.example.kmplibrary.data.models.ui

import com.example.kmplibrary.data.models.domain.Movie

/**
 * UI state for the movies screen
 */
data class MoviesUiState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val favorites: Set<Int> = emptySet(),
    val isSearching: Boolean = false,
    val hasMorePages: Boolean = true,
    val currentPage: Int = 1,
    val isTogglingFavorite: Boolean = false
) {
    /**
     * Get movies with favorite status applied
     */
    fun getMoviesWithFavorites(): List<MovieWithFavorite> {
        return movies.map { movie ->
            MovieWithFavorite(
                movie = movie,
                isFavorite = favorites.contains(movie.id)
            )
        }
    }
    
    /**
     * Check if the state represents an empty search result
     */
    fun isEmptySearchResult(): Boolean {
        return searchQuery.isNotEmpty() && movies.isEmpty() && !isLoading && error == null
    }
    
    /**
     * Check if the state represents an empty initial state
     */
    fun isEmptyInitialState(): Boolean {
        return searchQuery.isEmpty() && movies.isEmpty() && !isLoading && error == null
    }
}

/**
 * Data class combining a movie with its favorite status
 */
data class MovieWithFavorite(
    val movie: Movie,
    val isFavorite: Boolean
)