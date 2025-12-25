package com.example.kmplibrary.presentation.viewmodel

import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.domain.usecase.GetMoviesUseCase
import com.example.kmplibrary.domain.usecase.SearchMoviesUseCase
import com.example.kmplibrary.domain.usecase.ToggleFavoriteUseCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

/**
 * **Feature: movie-showcase-app, Property 2: Search Filtering Accuracy**
 * **Feature: movie-showcase-app, Property 4: Search Results State Update**
 * **Feature: movie-showcase-app, Property 5: Search Clear Restoration**
 * Property-based tests for MoviesViewModel search functionality
 */
class MoviesViewModelPropertyTest : StringSpec({
    
    "Search filtering should only return movies whose titles contain the search query" {
        checkAll(100, Arb.string(2..10), Arb.list(movieArb(), 5..20)) { searchQuery, movies ->
            val mockGetMoviesUseCase = MockGetMoviesUseCase(movies)
            val mockSearchUseCase = MockSearchMoviesUseCase()
            val mockToggleFavoriteUseCase = MockToggleFavoriteUseCase()
            
            val viewModel = MoviesViewModel(
                mockGetMoviesUseCase,
                mockSearchUseCase,
                mockToggleFavoriteUseCase
            )
            
            // Set up search results that match the query
            val matchingMovies = movies.filter { 
                it.title.contains(searchQuery, ignoreCase = true) 
            }
            mockSearchUseCase.setSearchResults(searchQuery, matchingMovies)
            
            // Perform search
            viewModel.searchMovies(searchQuery)
            delay(350) // Wait for debounce
            
            val uiState = viewModel.uiState.first()
            
            // All returned movies should contain the search query in their title
            uiState.movies.all { movie ->
                movie.title.contains(searchQuery, ignoreCase = true)
            } shouldBe true
        }
    }
    
    "Search results state should update when search is performed" {
        checkAll(100, Arb.string(2..10)) { searchQuery ->
            val mockGetMoviesUseCase = MockGetMoviesUseCase(emptyList())
            val mockSearchUseCase = MockSearchMoviesUseCase()
            val mockToggleFavoriteUseCase = MockToggleFavoriteUseCase()
            
            val viewModel = MoviesViewModel(
                mockGetMoviesUseCase,
                mockSearchUseCase,
                mockToggleFavoriteUseCase
            )
            
            val searchResults = listOf(createMockMovie(1, "Search Result"))
            mockSearchUseCase.setSearchResults(searchQuery, searchResults)
            
            // Perform search
            viewModel.searchMovies(searchQuery)
            delay(350) // Wait for debounce
            
            val uiState = viewModel.uiState.first()
            
            // UI state should reflect search results
            uiState.searchQuery shouldBe searchQuery
            uiState.isSearching shouldBe true
            uiState.movies shouldBe searchResults
        }
    }
    
    "Clearing search should restore original movie list" {
        checkAll(100, Arb.list(movieArb(), 5..10)) { originalMovies ->
            val mockGetMoviesUseCase = MockGetMoviesUseCase(originalMovies)
            val mockSearchUseCase = MockSearchMoviesUseCase()
            val mockToggleFavoriteUseCase = MockToggleFavoriteUseCase()
            
            val viewModel = MoviesViewModel(
                mockGetMoviesUseCase,
                mockSearchUseCase,
                mockToggleFavoriteUseCase
            )
            
            // Wait for initial load
            delay(100)
            val initialState = viewModel.uiState.first()
            
            // Perform search
            viewModel.searchMovies("test")
            delay(350)
            
            // Clear search
            viewModel.clearSearch()
            delay(100)
            
            val finalState = viewModel.uiState.first()
            
            // Should restore original state
            finalState.searchQuery shouldBe ""
            finalState.isSearching shouldBe false
            finalState.movies shouldBe initialState.movies
        }
    }
    
    "Search with empty query should not trigger search" {
        checkAll(100, Arb.string(0..1)) { emptyQuery ->
            val mockGetMoviesUseCase = MockGetMoviesUseCase(emptyList())
            val mockSearchUseCase = MockSearchMoviesUseCase()
            val mockToggleFavoriteUseCase = MockToggleFavoriteUseCase()
            
            val viewModel = MoviesViewModel(
                mockGetMoviesUseCase,
                mockSearchUseCase,
                mockToggleFavoriteUseCase
            )
            
            // Perform search with empty/short query
            viewModel.searchMovies(emptyQuery)
            delay(350)
            
            // Search should not have been called
            mockSearchUseCase.searchCallCount shouldBe 0
        }
    }
    
    "Toggle favorite should update movie state immediately" {
        checkAll(100, movieArb()) { movie ->
            val mockGetMoviesUseCase = MockGetMoviesUseCase(listOf(movie))
            val mockSearchUseCase = MockSearchMoviesUseCase()
            val mockToggleFavoriteUseCase = MockToggleFavoriteUseCase()
            
            val viewModel = MoviesViewModel(
                mockGetMoviesUseCase,
                mockSearchUseCase,
                mockToggleFavoriteUseCase
            )
            
            delay(100) // Wait for initial load
            
            // Toggle favorite
            viewModel.toggleFavorite(movie)
            delay(100)
            
            val uiState = viewModel.uiState.first()
            
            // Favorite status should be updated
            val updatedMovie = uiState.movies.find { it.id == movie.id }
            updatedMovie?.isFavorite shouldBe !movie.isFavorite
        }
    }
})

// Mock implementations for testing
class MockGetMoviesUseCase(private val movies: List<Movie>) : GetMoviesUseCase {
    override suspend fun getPopularMovies(page: Int): Flow<List<Movie>> = flowOf(movies)
    override suspend fun getNowPlayingMovies(page: Int): Flow<List<Movie>> = flowOf(movies)
    override suspend fun getTopRatedMovies(page: Int): Flow<List<Movie>> = flowOf(movies)
    override suspend fun getUpcomingMovies(page: Int): Flow<List<Movie>> = flowOf(movies)
    override suspend fun refreshMovies(): Result<Unit> = Result.success(Unit)
}

class MockSearchMoviesUseCase : SearchMoviesUseCase {
    var searchCallCount = 0
    private val searchResults = mutableMapOf<String, List<Movie>>()
    
    fun setSearchResults(query: String, results: List<Movie>) {
        searchResults[query] = results
    }
    
    override fun searchMovies(query: String): Flow<List<Movie>> {
        searchCallCount++
        return flowOf(searchResults[query] ?: emptyList())
    }
    
    override suspend fun getSearchSuggestions(query: String): List<String> = emptyList()
    override suspend fun clearSearch() {}
}

class MockToggleFavoriteUseCase : ToggleFavoriteUseCase {
    override suspend fun toggleFavorite(movie: Movie): Result<Boolean> {
        return Result.success(!movie.isFavorite)
    }
    
    override suspend fun addToFavorites(movie: Movie): Result<Unit> = Result.success(Unit)
    override suspend fun removeFromFavorites(movieId: Int): Result<Unit> = Result.success(Unit)
    override suspend fun isFavorite(movieId: Int): Boolean = false
}

// Arbitrary generator for Movie domain model
private fun movieArb() = arbitrary {
    Movie(
        id = Arb.int(1..1000000).bind(),
        title = Arb.string(1..100).bind(),
        overview = Arb.string(0..500).bind(),
        posterPath = Arb.string(1..50).orNull().bind(),
        backdropPath = Arb.string(1..50).orNull().bind(),
        releaseDate = Arb.string(10..10).bind(),
        voteAverage = Arb.double(0.0..10.0).bind(),
        genreIds = Arb.list(Arb.int(1..100), 0..5).bind(),
        isFavorite = Arb.boolean().bind()
    )
}

private fun createMockMovie(id: Int, title: String) = Movie(
    id = id,
    title = title,
    overview = "Mock overview",
    posterPath = "/mock.jpg",
    backdropPath = "/mock_backdrop.jpg",
    releaseDate = "2024-01-01",
    voteAverage = 8.0,
    genreIds = listOf(28),
    isFavorite = false
)