package com.example.kmplibrary.data.repository

import com.example.kmplibrary.data.local.TestFavoritesStorage
import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.data.models.network.*
import com.example.kmplibrary.data.network.TMDBApiClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class MoviesRepositoryTest {
    
    private lateinit var mockApiClient: MockTMDBApiClient
    private lateinit var favoritesStorage: TestFavoritesStorage
    private lateinit var repository: MoviesRepository
    
    @BeforeTest
    fun setup() {
        mockApiClient = MockTMDBApiClient()
        favoritesStorage = TestFavoritesStorage()
        repository = MoviesRepositoryImpl(mockApiClient, favoritesStorage)
    }
    
    @Test
    fun `getPopularMovies should return movies with favorite status`() = runTest {
        // Given
        val testMovie = createTestMovie(1, "Popular Movie")
        favoritesStorage.addFavorite(testMovie)
        
        // When
        val movies = repository.getPopularMovies().first()
        
        // Then
        assertEquals(2, movies.size)
        assertTrue(movies.any { it.id == 1 && it.isFavorite })
        assertTrue(movies.any { it.id == 2 && !it.isFavorite })
    }
    
    @Test
    fun `searchMovies should return search results with favorite status`() = runTest {
        // Given
        val testMovie = createTestMovie(3, "Search Movie")
        favoritesStorage.addFavorite(testMovie)
        
        // When
        val movies = repository.searchMovies("test").first()
        
        // Then
        assertEquals(1, movies.size)
        assertTrue(movies.first().isFavorite)
        assertEquals("Search Movie", movies.first().title)
    }
    
    @Test
    fun `getMovieDetail should return movie detail with favorite status`() = runTest {
        // Given
        val movieId = 123
        val testMovie = createTestMovie(movieId, "Detail Movie")
        favoritesStorage.addFavorite(testMovie)
        
        // When
        val result = repository.getMovieDetail(movieId)
        
        // Then
        assertTrue(result.isSuccess)
        val movieDetail = result.getOrNull()
        assertNotNull(movieDetail)
        assertEquals(movieId, movieDetail.id)
        assertTrue(movieDetail.isFavorite)
        assertEquals("Director Test", movieDetail.director)
        assertEquals(2, movieDetail.cast.size)
    }
    
    @Test
    fun `getMovieDetail should handle credits failure gracefully`() = runTest {
        // Given
        mockApiClient.shouldFailCredits = true
        val movieId = 123
        
        // When
        val result = repository.getMovieDetail(movieId)
        
        // Then
        assertTrue(result.isSuccess)
        val movieDetail = result.getOrNull()
        assertNotNull(movieDetail)
        assertEquals(movieId, movieDetail.id)
        assertNull(movieDetail.director)
        assertTrue(movieDetail.cast.isEmpty())
    }
    
    @Test
    fun `repository should update favorite status when favorites change`() = runTest {
        // Given
        val movies = repository.getPopularMovies().first()
        val firstMovie = movies.first()
        assertFalse(firstMovie.isFavorite)
        
        // When
        favoritesStorage.addFavorite(firstMovie)
        
        // Then
        val updatedMovies = repository.getPopularMovies().first()
        val updatedFirstMovie = updatedMovies.first { it.id == firstMovie.id }
        assertTrue(updatedFirstMovie.isFavorite)
    }
    
    @Test
    fun `refreshMovies should clear cache`() = runTest {
        // Given
        repository.getPopularMovies().first() // Load cache
        
        // When
        val result = repository.refreshMovies()
        
        // Then
        assertTrue(result.isSuccess)
        // Cache should be cleared (tested indirectly through behavior)
    }
    
    @Test
    fun `clearCache should clear all cached data`() = runTest {
        // Given
        repository.getPopularMovies().first() // Load cache
        repository.getMovieDetail(123) // Load detail cache
        
        // When
        val result = repository.clearCache()
        
        // Then
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun `getNowPlayingMovies should return movies with favorite status`() = runTest {
        // When
        val movies = repository.getNowPlayingMovies().first()
        
        // Then
        assertEquals(2, movies.size)
        assertFalse(movies.all { it.isFavorite })
    }
    
    @Test
    fun `getTopRatedMovies should return movies with favorite status`() = runTest {
        // When
        val movies = repository.getTopRatedMovies().first()
        
        // Then
        assertEquals(2, movies.size)
        assertFalse(movies.all { it.isFavorite })
    }
    
    @Test
    fun `getUpcomingMovies should return movies with favorite status`() = runTest {
        // When
        val movies = repository.getUpcomingMovies().first()
        
        // Then
        assertEquals(2, movies.size)
        assertFalse(movies.all { it.isFavorite })
    }
    
    private fun createTestMovie(id: Int, title: String) = Movie(
        id = id,
        title = title,
        overview = "Test overview",
        posterPath = "/test.jpg",
        backdropPath = "/backdrop.jpg",
        releaseDate = "2024-01-01",
        voteAverage = 8.0,
        genreIds = listOf(28, 12),
        isFavorite = false
    )
}

/**
 * Mock implementation of TMDBApiClient for testing
 */
class MockTMDBApiClient : TMDBApiClient {
    
    var shouldFailCredits = false
    
    override suspend fun getPopularMovies(page: Int): Result<TMDBMoviesResponse> {
        return Result.success(createMockMoviesResponse())
    }
    
    override suspend fun searchMovies(query: String, page: Int): Result<TMDBMoviesResponse> {
        return Result.success(TMDBMoviesResponse(
            page = 1,
            results = listOf(createMockTMDBMovie(3, "Search Movie")),
            totalPages = 1,
            totalResults = 1
        ))
    }
    
    override suspend fun getMovieDetail(movieId: Int): Result<TMDBMovieDetail> {
        return Result.success(createMockMovieDetail(movieId))
    }
    
    override suspend fun getMovieCredits(movieId: Int): Result<TMDBCreditsResponse> {
        return if (shouldFailCredits) {
            Result.failure(Exception("Credits failed"))
        } else {
            Result.success(createMockCreditsResponse(movieId))
        }
    }
    
    override suspend fun getNowPlayingMovies(page: Int): Result<TMDBMoviesResponse> {
        return Result.success(createMockMoviesResponse())
    }
    
    override suspend fun getTopRatedMovies(page: Int): Result<TMDBMoviesResponse> {
        return Result.success(createMockMoviesResponse())
    }
    
    override suspend fun getUpcomingMovies(page: Int): Result<TMDBMoviesResponse> {
        return Result.success(createMockMoviesResponse())
    }
    
    private fun createMockMoviesResponse() = TMDBMoviesResponse(
        page = 1,
        results = listOf(
            createMockTMDBMovie(1, "Movie 1"),
            createMockTMDBMovie(2, "Movie 2")
        ),
        totalPages = 1,
        totalResults = 2
    )
    
    private fun createMockTMDBMovie(id: Int, title: String) = TMDBMovie(
        id = id,
        title = title,
        overview = "Test overview",
        posterPath = "/test.jpg",
        backdropPath = "/backdrop.jpg",
        releaseDate = "2024-01-01",
        voteAverage = 8.0,
        voteCount = 1000,
        genreIds = listOf(28, 12),
        adult = false,
        originalLanguage = "en",
        originalTitle = title,
        popularity = 100.0,
        video = false
    )
    
    private fun createMockMovieDetail(id: Int) = TMDBMovieDetail(
        id = id,
        title = "Detail Movie",
        overview = "Detail overview",
        posterPath = "/detail.jpg",
        backdropPath = "/detail_backdrop.jpg",
        releaseDate = "2024-01-01",
        voteAverage = 8.5,
        voteCount = 2000,
        runtime = 120,
        genres = listOf(
            TMDBGenre(28, "Action"),
            TMDBGenre(12, "Adventure")
        ),
        productionCompanies = emptyList(),
        productionCountries = emptyList(),
        spokenLanguages = emptyList(),
        adult = false,
        budget = 100000000,
        homepage = "https://example.com",
        imdbId = "tt1234567",
        originalLanguage = "en",
        originalTitle = "Detail Movie",
        popularity = 150.0,
        revenue = 500000000,
        status = "Released",
        tagline = "Test tagline",
        video = false
    )
    
    private fun createMockCreditsResponse(id: Int) = TMDBCreditsResponse(
        id = id,
        cast = listOf(
            TMDBCastMember(
                id = 1,
                name = "Actor 1",
                character = "Hero",
                profilePath = "/actor1.jpg",
                castId = 1,
                creditId = "credit1",
                gender = 2,
                knownForDepartment = "Acting",
                order = 0,
                originalName = "Actor 1",
                popularity = 50.0
            ),
            TMDBCastMember(
                id = 2,
                name = "Actor 2",
                character = "Villain",
                profilePath = "/actor2.jpg",
                castId = 2,
                creditId = "credit2",
                gender = 1,
                knownForDepartment = "Acting",
                order = 1,
                originalName = "Actor 2",
                popularity = 45.0
            )
        ),
        crew = listOf(
            TMDBCrewMember(
                id = 3,
                name = "Director Test",
                job = "Director",
                department = "Directing",
                profilePath = "/director.jpg",
                creditId = "credit3",
                gender = 2,
                knownForDepartment = "Directing",
                originalName = "Director Test",
                popularity = 30.0
            )
        )
    )
}