package com.example.kmplibrary.data.local

import com.example.kmplibrary.data.models.domain.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Implementation of FavoritesStorage using platform-specific preferences
 */
class FavoritesStorageImpl(
    private val preferencesManager: PreferencesManager
) : FavoritesStorage {
    
    private val mutex = Mutex()
    private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    companion object {
        private const val FAVORITES_KEY = "favorites_movies"
    }
    
    init {
        // Load favorites on initialization
        loadFavoritesFromStorage()
    }
    
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
                saveFavoritesToStorage(currentFavorites)
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
                val removed = currentFavorites.removeAll { it.id == movieId }
                
                if (removed) {
                    saveFavoritesToStorage(currentFavorites)
                    _favorites.value = currentFavorites
                }
                
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
                preferencesManager.remove(FAVORITES_KEY)
                _favorites.value = emptyList()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(StorageException.WriteError("Failed to clear favorites", e))
            }
        }
    }
    
    private fun loadFavoritesFromStorage() {
        try {
            val favoritesJson = preferencesManager.getString(FAVORITES_KEY, "[]")
            val favorites = json.decodeFromString<List<Movie>>(favoritesJson)
            _favorites.value = favorites
        } catch (e: Exception) {
            // If loading fails, start with empty list
            _favorites.value = emptyList()
        }
    }
    
    private fun saveFavoritesToStorage(favorites: List<Movie>) {
        val favoritesJson = json.encodeToString(favorites)
        preferencesManager.putString(FAVORITES_KEY, favoritesJson)
    }
}

/**
 * Storage exception types for better error handling
 */
sealed class StorageException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class ReadError(message: String, cause: Throwable? = null) : StorageException(message, cause)
    class WriteError(message: String, cause: Throwable? = null) : StorageException(message, cause)
    class SerializationError(message: String, cause: Throwable? = null) : StorageException(message, cause)
}