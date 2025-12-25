package com.example.kmplibrary.domain.usecase

import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.data.models.network.TMDBMoviesResponse
import com.example.kmplibrary.data.repository.MoviesRepository
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
 * **Feature: movie-showcase-app, Property 3: Search Debounce Optimization**
 * Property-based tests for search debouncing functionality
 */
class SearchMoviesUseCasePropertyTest : StringSpec({
    
    "Search debounce should only trigger final query in rapid sequence" {
        checkAll(100, Arb.list(Arb.string(1..10), 2..5)) { queries ->
            val mockRepository = MockMoviesRepository()
            val searchUseCase = SearchMoviesUseCaseImpl(mockRepository, debounceTimeMs = 100L)
            
            // Simulate rapid queries
            queries.forEach { query ->
                searchUseCase.searchMovies(query)
                delay(50) // Less than debounce time
            }
            
            // Wait for debounce to complete
            delay(200)
            
            // Only the last query should have been executed
            val finalQuery = queries.last()
            mockRepository.searchCallCount shouldBe 1
            mockRepository.lastSearchQuery shouldBe finalQuery
        }
    }
    
    "Search with empty query should return empty results" {
        checkAll(100, Arb.string(0..0)) { emptyQuery ->
            val mockRepository = MockMoviesRepository()
            val searchUseCase = SearchMoviesUseCaseImpl(mockRepository)
            
            val results = searchUseCase.searchMovies(emptyQuery).first()
            
            results.isEmpty() shouldBe true
            mockRepository.searchCallCount shouldBe 0
        }
    }
    
    "Search with query less than 2 characters should return empty results" {
        checkAll(100, Arb.string(1..1)) { shortQuery ->
            val mockRepository = MockMoviesRepository()
            val searchUseCase = SearchMoviesUseCaseImpl(mockRepository)
            
            val results = searchUseCase.searchMovies(shortQuery).first()
            
            results.isEmpty() shouldBe true
            mockRepository.searchCallCount shouldBe 0
        }
    }
    
    "Search caching should prevent duplicate API calls for same query" {
        checkAll(100, Arb.string(2..10)) { query ->
            val mockRepository = MockMoviesRepository()
            val searchUseCase = SearchMoviesUseCaseImpl(mockRepository)
            
            // Search same query multiple times
            searchUseCase.searchMovies(query).first()
            delay(150) // Wait for debounce
            searchUseCase.searchMovies(query).first()
            delay(150) // Wait for debounce
            
            // Should only call repository once due to caching
            mockRepository.searchCallCount shouldBe 1
        }
    }
    
    "Clear search should reset state and cache" {
        checkAll(100, Arb.string(2..10)) { query ->
            val mockRepository = MockMoviesRepository()
            val searchUseCase = SearchMoviesUseCaseImpl(mockRepository)
            
            // Perform search
            searchUseCase.searchMovies(query).first()
            delay(150)
            
            // Clear search
            searchUseCase.clearSearch()
            
            // Search again with same query should call repository again
            searchUseCase.searchMovies(query).first()
            delay(150)
            
            mockRepository.searchCallCount shouldBe 2
        }
    }
    
    "Search suggestions should return relevant cached queries" {
        checkAll(100, Arb.list(Arb.string(3..10), 2..5)) { queries ->
            val mockRepository = MockMoviesRepository()
            val searchUseCase = SearchMoviesUseCaseImpl(mockRepository)
            
            // Perform searches to populate cache
            queries.forEach { query ->
                searchUseCase.searchMovies(query).first()
                delay(150)
            }
            
            // Get suggestions for partial query
            val partialQuery = queries.first().take(2)
            val suggestions = searchUseCase.getSearchSuggestions(partialQuery)
            
            // All suggestions should contain the partial query
            suggestions.all { it.contains(partialQuery, ignoreCase = true) } shouldBe true
        }
    }
})

/**
 * Mock MoviesRepository for testing search functionality
 */
class MockMoviesRepository : MoviesRepository {
    var searchCallCount = 0
    var lastSearchQuery = ""
    
    override suspend fun getPopularMovies(page: Int): Flow<List<Movie>> = flowOf(emptyList())
    override suspend fun searchMovies(query: String, page: Int): Flow<List<Movie>> {
        searchCallCount++
        lastSearchQuery = query
        return flowOf(listOf(createMockMovie(query)))
    }
    override suspend fun getMovieDetail(movieId: Int) = Result.success(createMockMovieDetail())
    override suspend fun getNowPlayingMovies(page: Int): Flow<List<Movie>> = flowOf(emptyList())
    override suspend fun getTopRatedMovies(page: Int): Flow<List<Movie>> = flowOf(emptyList())
    override suspend fun getUpcomingMovies(page: Int): Flow<List<Movie>> = flowOf(emptyList())
    override suspend fun refreshMovies() = Result.success(Unit)
    override suspend fun clearCache() = Result.success(Unit)
    
    private fun createMockMovie(title: String) = Movie(
        id = title.hashCode(),
        title = title,
        overview = "Mock overview",
        posterPath = "/mock.jpg",
        backdropPath = "/mock_backdrop.jpg",
        releaseDate = "2024-01-01",
        voteAverage = 8.0,
        genreIds = listOf(28),
        isFavorite = false
    )
    
    private fun createMockMovieDetail() = com.example.kmplibrary.data.models.domain.MovieDetail(
        id = 1,
        title = "Mock Detail",
        overview = "Mock detail overview",
        posterPath = "/mock_detail.jpg",
        backdropPath = "/mock_detail_backdrop.jpg",
        releaseDate = "2024-01-01",
        runtime = 120,
        voteAverage = 8.5,
        genres = emptyList(),
        director = "Mock Director",
        cast = emptyList(),
        isFavorite = false
    )
}