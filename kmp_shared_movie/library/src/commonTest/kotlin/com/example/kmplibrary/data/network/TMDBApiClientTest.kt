package com.example.kmplibrary.data.network

import com.example.kmplibrary.data.models.network.*
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.*

class TMDBApiClientTest {
    
    private lateinit var mockEngine: MockEngine
    private lateinit var apiClient: TMDBApiClient
    
    @BeforeTest
    fun setup() {
        mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.contains("/movie/popular") -> {
                    respond(
                        content = mockPopularMoviesResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                request.url.encodedPath.contains("/movie/now_playing") -> {
                    respond(
                        content = mockPopularMoviesResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                request.url.encodedPath.contains("/movie/top_rated") -> {
                    respond(
                        content = mockPopularMoviesResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                request.url.encodedPath.contains("/movie/upcoming") -> {
                    respond(
                        content = mockPopularMoviesResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                request.url.encodedPath.contains("/search/movie") -> {
                    respond(
                        content = mockSearchMoviesResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                request.url.encodedPath.contains("/movie/123") && !request.url.encodedPath.contains("/credits") -> {
                    respond(
                        content = mockMovieDetailResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                request.url.encodedPath.contains("/movie/123/credits") -> {
                    respond(
                        content = mockCreditsResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                request.url.encodedPath.contains("/movie/404") -> {
                    respond(
                        content = mockErrorResponse,
                        status = HttpStatusCode.NotFound,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> {
                    respond(
                        content = "Not Found",
                        status = HttpStatusCode.NotFound
                    )
                }
            }
        }
        
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
        }
        
        apiClient = TMDBApiClientImpl(httpClient)
    }
    
    @Test
    fun `getPopularMovies should return success result with movies`() = runTest {
        val result = apiClient.getPopularMovies(page = 1)
        
        assertTrue(result.isSuccess)
        val response = result.getOrNull()
        assertNotNull(response)
        assertEquals(1, response.page)
        assertEquals(2, response.results.size)
        assertEquals("Test Movie 1", response.results[0].title)
        assertEquals("Test Movie 2", response.results[1].title)
    }
    
    @Test
    fun `searchMovies should return success result with search results`() = runTest {
        val result = apiClient.searchMovies(query = "test", page = 1)
        
        assertTrue(result.isSuccess)
        val response = result.getOrNull()
        assertNotNull(response)
        assertEquals(1, response.page)
        assertEquals(1, response.results.size)
        assertEquals("Search Result Movie", response.results[0].title)
    }
    
    @Test
    fun `getMovieDetail should return success result with movie details`() = runTest {
        val result = apiClient.getMovieDetail(movieId = 123)
        
        assertTrue(result.isSuccess)
        val movieDetail = result.getOrNull()
        assertNotNull(movieDetail)
        assertEquals(123, movieDetail.id)
        assertEquals("Detailed Movie", movieDetail.title)
        assertEquals(120, movieDetail.runtime)
        assertEquals(2, movieDetail.genres.size)
    }
    
    @Test
    fun `getMovieCredits should return success result with cast and crew`() = runTest {
        val result = apiClient.getMovieCredits(movieId = 123)
        
        assertTrue(result.isSuccess)
        val credits = result.getOrNull()
        assertNotNull(credits)
        assertEquals(123, credits.id)
        assertEquals(2, credits.cast.size)
        assertEquals(1, credits.crew.size)
        assertEquals("Actor One", credits.cast[0].name)
        assertEquals("Director One", credits.crew[0].name)
    }
    
    @Test
    fun `API client should handle HTTP errors gracefully`() = runTest {
        val result = apiClient.getMovieDetail(movieId = 404)
        
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is NetworkException)
    }
    
    @Test
    fun `getNowPlayingMovies should return success result`() = runTest {
        val result = apiClient.getNowPlayingMovies(page = 1)
        
        assertTrue(result.isSuccess)
        val response = result.getOrNull()
        assertNotNull(response)
    }
    
    @Test
    fun `getTopRatedMovies should return success result`() = runTest {
        val result = apiClient.getTopRatedMovies(page = 1)
        
        assertTrue(result.isSuccess)
        val response = result.getOrNull()
        assertNotNull(response)
    }
    
    @Test
    fun `getUpcomingMovies should return success result`() = runTest {
        val result = apiClient.getUpcomingMovies(page = 1)
        
        assertTrue(result.isSuccess)
        val response = result.getOrNull()
        assertNotNull(response)
    }
    
    companion object {
        private const val mockPopularMoviesResponse = """
        {
            "page": 1,
            "results": [
                {
                    "id": 1,
                    "title": "Test Movie 1",
                    "overview": "Test overview 1",
                    "poster_path": "/test1.jpg",
                    "backdrop_path": "/backdrop1.jpg",
                    "release_date": "2024-01-01",
                    "vote_average": 8.5,
                    "vote_count": 1000,
                    "genre_ids": [28, 12],
                    "adult": false,
                    "original_language": "en",
                    "original_title": "Test Movie 1",
                    "popularity": 100.0,
                    "video": false
                },
                {
                    "id": 2,
                    "title": "Test Movie 2",
                    "overview": "Test overview 2",
                    "poster_path": "/test2.jpg",
                    "backdrop_path": "/backdrop2.jpg",
                    "release_date": "2024-02-01",
                    "vote_average": 7.8,
                    "vote_count": 800,
                    "genre_ids": [35, 18],
                    "adult": false,
                    "original_language": "en",
                    "original_title": "Test Movie 2",
                    "popularity": 90.0,
                    "video": false
                }
            ],
            "total_pages": 100,
            "total_results": 2000
        }
        """
        
        private const val mockSearchMoviesResponse = """
        {
            "page": 1,
            "results": [
                {
                    "id": 3,
                    "title": "Search Result Movie",
                    "overview": "Search result overview",
                    "poster_path": "/search.jpg",
                    "backdrop_path": "/search_backdrop.jpg",
                    "release_date": "2024-03-01",
                    "vote_average": 9.0,
                    "vote_count": 1500,
                    "genre_ids": [16, 10751],
                    "adult": false,
                    "original_language": "en",
                    "original_title": "Search Result Movie",
                    "popularity": 120.0,
                    "video": false
                }
            ],
            "total_pages": 10,
            "total_results": 100
        }
        """
        
        private const val mockMovieDetailResponse = """
        {
            "id": 123,
            "title": "Detailed Movie",
            "overview": "Detailed movie overview",
            "poster_path": "/detailed.jpg",
            "backdrop_path": "/detailed_backdrop.jpg",
            "release_date": "2024-04-01",
            "vote_average": 8.8,
            "vote_count": 2000,
            "runtime": 120,
            "genres": [
                {"id": 28, "name": "Action"},
                {"id": 12, "name": "Adventure"}
            ],
            "production_companies": [
                {
                    "id": 1,
                    "name": "Test Studio",
                    "logo_path": "/logo.jpg",
                    "origin_country": "US"
                }
            ],
            "production_countries": [
                {"iso_3166_1": "US", "name": "United States"}
            ],
            "spoken_languages": [
                {"english_name": "English", "iso_639_1": "en", "name": "English"}
            ],
            "adult": false,
            "budget": 100000000,
            "homepage": "https://example.com",
            "imdb_id": "tt1234567",
            "original_language": "en",
            "original_title": "Detailed Movie",
            "popularity": 150.0,
            "revenue": 500000000,
            "status": "Released",
            "tagline": "The ultimate test movie",
            "video": false
        }
        """
        
        private const val mockCreditsResponse = """
        {
            "id": 123,
            "cast": [
                {
                    "id": 1,
                    "name": "Actor One",
                    "character": "Hero",
                    "profile_path": "/actor1.jpg",
                    "cast_id": 1,
                    "credit_id": "credit1",
                    "gender": 2,
                    "known_for_department": "Acting",
                    "order": 0,
                    "original_name": "Actor One",
                    "popularity": 50.0
                },
                {
                    "id": 2,
                    "name": "Actor Two",
                    "character": "Villain",
                    "profile_path": "/actor2.jpg",
                    "cast_id": 2,
                    "credit_id": "credit2",
                    "gender": 1,
                    "known_for_department": "Acting",
                    "order": 1,
                    "original_name": "Actor Two",
                    "popularity": 45.0
                }
            ],
            "crew": [
                {
                    "id": 3,
                    "name": "Director One",
                    "job": "Director",
                    "department": "Directing",
                    "profile_path": "/director1.jpg",
                    "credit_id": "credit3",
                    "gender": 2,
                    "known_for_department": "Directing",
                    "original_name": "Director One",
                    "popularity": 30.0
                }
            ]
        }
        """
        
        private const val mockErrorResponse = """
        {
            "success": false,
            "status_code": 34,
            "status_message": "The resource you requested could not be found."
        }
        """
    }
}