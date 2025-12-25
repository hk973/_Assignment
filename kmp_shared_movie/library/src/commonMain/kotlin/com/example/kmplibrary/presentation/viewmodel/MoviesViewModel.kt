package com.example.kmplibrary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.data.models.ui.MoviesUiState
import com.example.kmplibrary.domain.usecase.GetMoviesUseCase
import com.example.kmplibrary.domain.usecase.SearchMoviesUseCase
import com.example.kmplibrary.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the movies screen
 * Manages state for movies list, search, and favorites
 */
@OptIn(FlowPreview::class)
class MoviesViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    private var currentMovieType = MovieType.POPULAR
    private var currentPage = 1
    
    enum class MovieType {
        POPULAR, NOW_PLAYING, TOP_RATED, UPCOMING
    }
    
    init {
        loadMovies()
        observeSearchQuery()
    }
    
    /**
     * Load movies based on current type
     */
    fun loadMovies(movieType: MovieType = MovieType.POPULAR) {
        currentMovieType = movieType
        currentPage = 1
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val moviesFlow = when (movieType) {
                    MovieType.POPULAR -> getMoviesUseCase.getPopularMovies(currentPage)
                    MovieType.NOW_PLAYING -> getMoviesUseCase.getNowPlayingMovies(currentPage)
                    MovieType.TOP_RATED -> getMoviesUseCase.getTopRatedMovies(currentPage)
                    MovieType.UPCOMING -> getMoviesUseCase.getUpcomingMovies(currentPage)
                }
                
                moviesFlow.collect { movies ->
                    val favoriteIds = movies.filter { it.isFavorite }.map { it.id }.toSet()
                    _uiState.value = _uiState.value.copy(
                        movies = movies,
                        isLoading = false,
                        error = null,
                        favorites = favoriteIds,
                        hasMorePages = movies.size >= 20 // TMDB typically returns 20 per page
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    /**
     * Search movies with debouncing
     */
    fun searchMovies(query: String) {
        _searchQuery.value = query.trim()
        _uiState.value = _uiState.value.copy(
            searchQuery = query.trim(),
            isSearching = query.isNotEmpty()
        )
    }
    
    /**
     * Clear search and return to normal movie list
     */
    fun clearSearch() {
        viewModelScope.launch {
            searchMoviesUseCase.clearSearch()
            _searchQuery.value = ""
            _uiState.value = _uiState.value.copy(
                searchQuery = "",
                isSearching = false
            )
            loadMovies(currentMovieType)
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
                        val updatedFavorites = if (isFavorite) {
                            _uiState.value.favorites + movie.id
                        } else {
                            _uiState.value.favorites - movie.id
                        }
                        
                        val updatedMovies = _uiState.value.movies.map { 
                            if (it.id == movie.id) it.copy(isFavorite = isFavorite) else it
                        }
                        
                        _uiState.value = _uiState.value.copy(
                            movies = updatedMovies,
                            favorites = updatedFavorites
                        )
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
     * Refresh movies data
     */
    fun refreshMovies() {
        viewModelScope.launch {
            _isRefreshing.value = true
            
            try {
                getMoviesUseCase.refreshMovies()
                loadMovies(currentMovieType)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to refresh movies"
                )
            } finally {
                _isRefreshing.value = false
            }
        }
    }
    
    /**
     * Load more movies (pagination)
     */
    fun loadMoreMovies() {
        if (_uiState.value.isLoading || !_uiState.value.hasMorePages) return
        
        currentPage++
        
        viewModelScope.launch {
            try {
                val moviesFlow = when (currentMovieType) {
                    MovieType.POPULAR -> getMoviesUseCase.getPopularMovies(currentPage)
                    MovieType.NOW_PLAYING -> getMoviesUseCase.getNowPlayingMovies(currentPage)
                    MovieType.TOP_RATED -> getMoviesUseCase.getTopRatedMovies(currentPage)
                    MovieType.UPCOMING -> getMoviesUseCase.getUpcomingMovies(currentPage)
                }
                
                moviesFlow.first().let { newMovies ->
                    val allMovies = _uiState.value.movies + newMovies
                    val favoriteIds = allMovies.filter { it.isFavorite }.map { it.id }.toSet()
                    
                    _uiState.value = _uiState.value.copy(
                        movies = allMovies,
                        favorites = favoriteIds,
                        hasMorePages = newMovies.size >= 20
                    )
                }
            } catch (e: Exception) {
                currentPage-- // Revert page increment on error
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to load more movies"
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
     * Observe search query changes and perform search
     */
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isEmpty()) {
                        // Return to normal movie list
                        loadMovies(currentMovieType)
                    } else if (query.length >= 2) {
                        // Perform search
                        _uiState.value = _uiState.value.copy(isLoading = true)
                        
                        try {
                            searchMoviesUseCase.searchMovies(query).collect { searchResults ->
                                val favoriteIds = searchResults.filter { it.isFavorite }.map { it.id }.toSet()
                                _uiState.value = _uiState.value.copy(
                                    movies = searchResults,
                                    isLoading = false,
                                    error = null,
                                    favorites = favoriteIds,
                                    hasMorePages = false // Search doesn't support pagination
                                )
                            }
                        } catch (e: Exception) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = e.message ?: "Search failed"
                            )
                        }
                    }
                }
        }
    }
}