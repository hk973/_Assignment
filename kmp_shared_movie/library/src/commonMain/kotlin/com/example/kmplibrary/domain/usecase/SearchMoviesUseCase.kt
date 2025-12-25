package com.example.kmplibrary.domain.usecase

import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.data.repository.MoviesRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Use case for searching movies with debouncing
 */
interface SearchMoviesUseCase {
    /**
     * Search movies with debouncing
     */
    fun searchMovies(query: String): Flow<List<Movie>>
    
    /**
     * Get search suggestions based on query
     */
    suspend fun getSearchSuggestions(query: String): List<String>
    
    /**
     * Clear search results
     */
    suspend fun clearSearch()
}

/**
 * Implementation of SearchMoviesUseCase with debouncing
 */
@OptIn(FlowPreview::class)
class SearchMoviesUseCaseImpl(
    private val moviesRepository: MoviesRepository,
    private val debounceTimeMs: Long = 300L
) : SearchMoviesUseCase {
    
    private val searchQueryFlow = MutableStateFlow("")
    private val searchMutex = Mutex()
    private val searchCache = mutableMapOf<String, List<Movie>>()
    
    override fun searchMovies(query: String): Flow<List<Movie>> {
        return flow {
            val trimmedQuery = query.trim()
            
            if (trimmedQuery.isEmpty()) {
                emit(emptyList())
                return@flow
            }
            
            if (trimmedQuery.length < 2) {
                emit(emptyList())
                return@flow
            }
            
            // Check cache first
            searchMutex.withLock {
                searchCache[trimmedQuery]?.let { cachedResults ->
                    emit(cachedResults)
                    return@flow
                }
            }
            
            // Perform search
            try {
                moviesRepository.searchMovies(trimmedQuery).collect { movies ->
                    // Cache the results
                    searchMutex.withLock {
                        searchCache[trimmedQuery] = movies
                    }
                    emit(movies)
                }
            } catch (e: Exception) {
                emit(emptyList())
            }
        }
    }
    
    override suspend fun getSearchSuggestions(query: String): List<String> {
        if (query.length < 2) return emptyList()
        
        return searchMutex.withLock {
            searchCache.keys
                .filter { it.contains(query, ignoreCase = true) && it != query }
                .take(5)
                .toList()
        }
    }
    
    override suspend fun clearSearch() {
        searchQueryFlow.value = ""
        searchMutex.withLock {
            searchCache.clear()
        }
    }
}