package com.example.kmplibrary.data.repository

import com.example.kmplibrary.data.models.domain.Movie
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing favorite movies
 */
interface FavoritesRepository {
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
     * Toggle favorite status of a movie
     */
    suspend fun toggleFavorite(movie: Movie): Result<Boolean>
    
    /**
     * Clear all favorites
     */
    suspend fun clearFavorites(): Result<Unit>
}