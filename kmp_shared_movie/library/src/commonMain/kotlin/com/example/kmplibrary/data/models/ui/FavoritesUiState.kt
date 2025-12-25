package com.example.kmplibrary.data.models.ui

import com.example.kmplibrary.data.models.domain.Movie

/**
 * UI state for the favorites screen
 */
data class FavoritesUiState(
    val favorites: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false
) {
    /**
     * Check if there are no favorite movies
     */
    fun isEmpty(): Boolean {
        return favorites.isEmpty() && !isLoading && error == null
    }
    
    /**
     * Check if the state represents an error state
     */
    fun isErrorState(): Boolean {
        return error != null
    }
    
    /**
     * Check if the state is in initial loading state
     */
    fun isInitialLoading(): Boolean {
        return isLoading && favorites.isEmpty() && error == null
    }
    
    /**
     * Get the count of favorite movies
     */
    fun getFavoriteCount(): Int {
        return favorites.size
    }
}