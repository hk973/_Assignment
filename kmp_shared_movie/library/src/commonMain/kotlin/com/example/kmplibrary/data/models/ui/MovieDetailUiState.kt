package com.example.kmplibrary.data.models.ui

import com.example.kmplibrary.data.models.domain.MovieDetail

/**
 * UI state for the movie detail screen
 */
data class MovieDetailUiState(
    val movieDetail: MovieDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false,
    val isTogglingFavorite: Boolean = false
) {
    /**
     * Check if the movie detail is available
     */
    fun hasMovieDetail(): Boolean {
        return movieDetail != null && error == null
    }
    
    /**
     * Check if the state represents an error state
     */
    fun isErrorState(): Boolean {
        return error != null && movieDetail == null
    }
    
    /**
     * Check if the state is in initial loading state
     */
    fun isInitialLoading(): Boolean {
        return isLoading && movieDetail == null && error == null
    }
}