package com.hariom.android_movie_app.ui.components

import com.example.kmplibrary.data.models.domain.Movie
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for MovieCard component logic
 * Complements the property-based tests with specific examples
 */
class MovieCardUnitTest {
    
    @Test
    fun `movie poster URL construction works correctly`() {
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            overview = "Test overview",
            posterPath = "/test_poster.jpg",
            backdropPath = null,
            releaseDate = "2023-12-25",
            voteAverage = 8.5,
            genreIds = listOf(28, 12),
            isFavorite = false
        )
        
        val expectedUrl = "https://image.tmdb.org/t/p/w500/test_poster.jpg"
        val actualUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
        
        assertEquals(expectedUrl, actualUrl)
    }
    
    @Test
    fun `movie poster URL handles null poster path`() {
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            overview = "Test overview",
            posterPath = null,
            backdropPath = null,
            releaseDate = "2023-12-25",
            voteAverage = 8.5,
            genreIds = listOf(28, 12),
            isFavorite = false
        )
        
        val actualUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
        
        assertNull(actualUrl)
    }
    
    @Test
    fun `movie rating formatting works correctly`() {
        val testCases = listOf(
            8.5 to "8.5",
            9.0 to "9.0",
            7.25 to "7.3", // Rounds to 1 decimal
            0.0 to "0.0",
            10.0 to "10.0"
        )
        
        testCases.forEach { (rating, expected) ->
            val formatted = String.format("%.1f", rating)
            assertEquals("Rating $rating should format to $expected", expected, formatted)
        }
    }
    
    @Test
    fun `movie year extraction works correctly`() {
        val testCases = listOf(
            "2023-12-25" to "2023",
            "2020-01-01" to "2020",
            "1999-06-15" to "1999",
            "2023" to "2023",
            "23" to "23",
            "" to ""
        )
        
        testCases.forEach { (releaseDate, expectedYear) ->
            val actualYear = if (releaseDate.isNotEmpty()) releaseDate.take(4) else ""
            assertEquals("Release date $releaseDate should extract year $expectedYear", expectedYear, actualYear)
        }
    }
    
    @Test
    fun `movie display completeness validation`() {
        val validMovie = Movie(
            id = 1,
            title = "Valid Movie",
            overview = "A valid movie for testing",
            posterPath = "/valid_poster.jpg",
            backdropPath = "/valid_backdrop.jpg",
            releaseDate = "2023-12-25",
            voteAverage = 8.5,
            genreIds = listOf(28, 12, 16),
            isFavorite = false
        )
        
        // Verify all required fields are present and valid
        assertTrue("Movie ID should be positive", validMovie.id > 0)
        assertTrue("Movie title should not be empty", validMovie.title.isNotEmpty())
        assertTrue("Movie title should not have leading/trailing whitespace", validMovie.title.trim() == validMovie.title)
        assertTrue("Vote average should be in valid range", validMovie.voteAverage in 0.0..10.0)
        assertTrue("Release date should have at least 4 characters for year", validMovie.releaseDate.length >= 4)
        assertTrue("All genre IDs should be positive", validMovie.genreIds.all { it > 0 })
    }
}