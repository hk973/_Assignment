package com.example.kmplibrary.data.local

import com.example.kmplibrary.data.models.domain.Movie
import kotlinx.coroutines.flow.Flow

/**
 * Interface for local favorites storage
 */
interface FavoritesStorage {
    /**
     * Get all favorite movies as a Flow for reactive updates
     */
    fun getFavorites(): Flow<List<Movie>>
    
    /**
     * Add a movie to favorites
     */
    suspend fun addFavorite(movie: Movie): Result<Unit>
    
    /**
     * Remove a movie from favorites by ID
     */
    suspend fun removeFavorite(movieId: Int): Result<Unit>
    
    /**
     * Check if a movie is in favorites
     */
    suspend fun isFavorite(movieId: Int): Boolean
    
    /**
     * Get all favorite movie IDs
     */
    suspend fun getFavoriteIds(): Set<Int>
    
    /**
     * Clear all favorites
     */
    suspend fun clearFavorites(): Result<Unit>
}