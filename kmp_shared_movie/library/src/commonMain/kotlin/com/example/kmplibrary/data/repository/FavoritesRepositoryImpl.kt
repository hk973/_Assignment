package com.example.kmplibrary.data.repository

import com.example.kmplibrary.data.local.FavoritesStorage
import com.example.kmplibrary.data.models.domain.Movie
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of FavoritesRepository
 */
class FavoritesRepositoryImpl(
    private val favoritesStorage: FavoritesStorage
) : FavoritesRepository {
    
    override fun getFavorites(): Flow<List<Movie>> {
        return favoritesStorage.getFavorites()
    }
    
    override suspend fun addFavorite(movie: Movie): Result<Unit> {
        return favoritesStorage.addFavorite(movie)
    }
    
    override suspend fun removeFavorite(movieId: Int): Result<Unit> {
        return favoritesStorage.removeFavorite(movieId)
    }
    
    override suspend fun isFavorite(movieId: Int): Boolean {
        return favoritesStorage.isFavorite(movieId)
    }
    
    override suspend fun getFavoriteIds(): Set<Int> {
        return favoritesStorage.getFavoriteIds()
    }
    
    override suspend fun toggleFavorite(movie: Movie): Result<Boolean> {
        return try {
            val isFavorite = isFavorite(movie.id)
            
            if (isFavorite) {
                removeFavorite(movie.id).fold(
                    onSuccess = { Result.success(false) },
                    onFailure = { Result.failure(it) }
                )
            } else {
                addFavorite(movie).fold(
                    onSuccess = { Result.success(true) },
                    onFailure = { Result.failure(it) }
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun clearFavorites(): Result<Unit> {
        return favoritesStorage.clearFavorites()
    }
}