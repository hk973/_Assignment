package com.example.kmplibrary.data.local

import com.example.kmplibrary.data.models.domain.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Test implementation of FavoritesStorage for unit testing
 */
class TestFavoritesStorage : FavoritesStorage {
    
    private val mutex = Mutex()
    private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
    
    override fun getFavorites(): Flow<List<Movie>> = _favorites.asStateFlow()
    
    override suspend fun addFavorite(movie: Movie): Result<Unit> {
        return mutex.withLock {
            try {
                val currentFavorites = _favorites.value.toMutableList()
                
                // Check if movie is already in favorites
                if (currentFavorites.any { it.id == movie.id }) {
                    return@withLock Result.success(Unit)
                }
                
                currentFavorites.add(movie)
                _favorites.value = currentFavorites
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(StorageException.WriteError("Failed to add favorite", e))
            }
        }
    }
    
    override suspend fun removeFavorite(movieId: Int): Result<Unit> {
        return mutex.withLock {
            try {
                val currentFavorites = _favorites.value.toMutableList()
                currentFavorites.removeAll { it.id == movieId }
                _favorites.value = currentFavorites
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(StorageException.WriteError("Failed to remove favorite", e))
            }
        }
    }
    
    override suspend fun isFavorite(movieId: Int): Boolean {
        return _favorites.value.any { it.id == movieId }
    }
    
    override suspend fun getFavoriteIds(): Set<Int> {
        return _favorites.value.map { it.id }.toSet()
    }
    
    override suspend fun clearFavorites(): Result<Unit> {
        return mutex.withLock {
            try {
                _favorites.value = emptyList()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(StorageException.WriteError("Failed to clear favorites", e))
            }
        }
    }
}