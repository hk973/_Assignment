package com.example.kmplibrary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.data.models.ui.FavoritesUiState
import com.example.kmplibrary.data.repository.FavoritesRepository
import com.example.kmplibrary.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the favorites screen
 * Manages state for favorites list
 */
class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()
    
    init {
        loadFavorites()
    }
    
    /**
     * Load favorites from repository
     */
    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            try {
                favoritesRepository.getFavorites().collect { favorites ->
                    _uiState.value = _uiState.value.copy(
                        favorites = favorites,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load favorites"
                )
            }
        }
    }
    
    /**
     * Remove a movie from favorites
     */
    fun removeFavorite(movie: Movie) {
        viewModelScope.launch {
            try {
                val result = toggleFavoriteUseCase.removeFromFavorites(movie.id)
                result.fold(
                    onSuccess = {
                        // The favorites list will be updated automatically through the Flow
                        // No need to manually update the state
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = error.message ?: "Failed to remove favorite"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to remove favorite"
                )
            }
        }
    }
    
    /**
     * Toggle favorite status of a movie
     */
    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            try {
                val result = toggleFavoriteUseCase.toggleFavorite(movie)
                result.fold(
                    onSuccess = { isFavorite ->
                        // The favorites list will be updated automatically through the Flow
                        // No need to manually update the state
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = error.message ?: "Failed to toggle favorite"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to toggle favorite"
                )
            }
        }
    }
    
    /**
     * Refresh favorites list
     */
    fun refreshFavorites() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            
            try {
                // Simply reload favorites - the Flow will automatically update
                loadFavorites()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to refresh favorites"
                )
            } finally {
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }
    
    /**
     * Clear all favorites
     */
    fun clearAllFavorites() {
        viewModelScope.launch {
            try {
                val result = favoritesRepository.clearFavorites()
                result.fold(
                    onSuccess = {
                        // The favorites list will be updated automatically through the Flow
                        // No need to manually update the state
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = error.message ?: "Failed to clear favorites"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to clear favorites"
                )
            }
        }
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Get the count of favorite movies
     */
    fun getFavoriteCount(): Int {
        return _uiState.value.favorites.size
    }
    
    /**
     * Check if favorites list is empty
     */
    fun isEmpty(): Boolean {
        return _uiState.value.favorites.isEmpty() && !_uiState.value.isLoading
    }
}