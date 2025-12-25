package com.example.kmplibrary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.data.models.ui.MovieDetailUiState
import com.example.kmplibrary.domain.usecase.GetMovieDetailUseCase
import com.example.kmplibrary.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the movie detail screen
 * Manages state for movie details and favorite toggle
 */
class MovieDetailViewModel(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()
    
    private var currentMovieId: Int? = null
    
    /**
     * Load movie details by ID
     */
    fun loadMovieDetail(movieId: Int) {
        if (currentMovieId == movieId && _uiState.value.movieDetail != null) {
            // Already loaded this movie, no need to reload
            return
        }
        
        currentMovieId = movieId
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            try {
                val result = getMovieDetailUseCase.getMovieDetail(movieId)
                result.fold(
                    onSuccess = { movieDetail ->
                        _uiState.value = _uiState.value.copy(
                            movieDetail = movieDetail,
                            isLoading = false,
                            error = null,
                            isFavorite = movieDetail.isFavorite
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load movie details"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    /**
     * Toggle favorite status of the current movie
     */
    fun toggleFavorite() {
        val movieDetail = _uiState.value.movieDetail ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTogglingFavorite = true)
            
            try {
                // Create a Movie object from MovieDetail for the use case
                val movie = Movie(
                    id = movieDetail.id,
                    title = movieDetail.title,
                    overview = movieDetail.overview,
                    posterPath = movieDetail.posterPath,
                    backdropPath = movieDetail.backdropPath,
                    releaseDate = movieDetail.releaseDate,
                    voteAverage = movieDetail.voteAverage,
                    genreIds = movieDetail.genres.map { it.id },
                    isFavorite = movieDetail.isFavorite
                )
                
                val result = toggleFavoriteUseCase.toggleFavorite(movie)
                result.fold(
                    onSuccess = { isFavorite ->
                        _uiState.value = _uiState.value.copy(
                            movieDetail = movieDetail.copy(isFavorite = isFavorite),
                            isFavorite = isFavorite,
                            isTogglingFavorite = false
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isTogglingFavorite = false,
                            error = error.message ?: "Failed to toggle favorite"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isTogglingFavorite = false,
                    error = e.message ?: "Failed to toggle favorite"
                )
            }
        }
    }
    
    /**
     * Refresh movie details
     */
    fun refreshMovieDetail() {
        currentMovieId?.let { movieId ->
            viewModelScope.launch {
                try {
                    getMovieDetailUseCase.refreshMovieDetail()
                    loadMovieDetail(movieId)
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Failed to refresh movie details"
                    )
                }
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
     * Clear the current movie detail (useful when navigating away)
     */
    fun clearMovieDetail() {
        _uiState.value = MovieDetailUiState()
        currentMovieId = null
    }
}