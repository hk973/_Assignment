package com.example.kmplibrary.domain.usecase

import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.data.repository.FavoritesRepository

/**
 * Use case for toggling favorite status of movies
 */
interface ToggleFavoriteUseCase {
    /**
     * Toggle favorite status of a movie
     * @param movie The movie to toggle favorite status for
     * @return Result with the new favorite status (true if now favorite, false if removed)
     */
    suspend fun toggleFavorite(movie: Movie): Result<Boolean>
    
    /**
     * Add a movie to favorites
     */
    suspend fun addToFavorites(movie: Movie): Result<Unit>
    
    /**
     * Remove a movie from favorites
     */
    suspend fun removeFromFavorites(movieId: Int): Result<Unit>
    
    /**
     * Check if a movie is in favorites
     */
    suspend fun isFavorite(movieId: Int): Boolean
}

/**
 * Implementation of ToggleFavoriteUseCase
 */
class ToggleFavoriteUseCaseImpl(
    private val favoritesRepository: FavoritesRepository
) : ToggleFavoriteUseCase {
    
    override suspend fun toggleFavorite(movie: Movie): Result<Boolean> {
        return try {
            val currentlyFavorite = favoritesRepository.isFavorite(movie.id)
            
            if (currentlyFavorite) {
                favoritesRepository.removeFavorite(movie.id).fold(
                    onSuccess = { Result.success(false) },
                    onFailure = { Result.failure(it) }
                )
            } else {
                favoritesRepository.addFavorite(movie).fold(
                    onSuccess = { Result.success(true) },
                    onFailure = { Result.failure(it) }
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addToFavorites(movie: Movie): Result<Unit> {
        return favoritesRepository.addFavorite(movie)
    }
    
    override suspend fun removeFromFavorites(movieId: Int): Result<Unit> {
        return favoritesRepository.removeFavorite(movieId)
    }
    
    override suspend fun isFavorite(movieId: Int): Boolean {
        return favoritesRepository.isFavorite(movieId)
    }
}