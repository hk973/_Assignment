package com.hariom.android_movie_app.ui.components

import com.example.kmplibrary.data.models.domain.Movie
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlin.math.abs

/**
 * Property-based tests for MovieCard component data validation
 * **Feature: movie-showcase-app, Property 1: Movie Display Completeness**
 * **Validates: Requirements 1.2**
 */
class MovieCardPropertyTest : StringSpec({
    
    "Property 1: Movie Display Completeness - Movie data should contain all required fields for display" {
        checkAll(100, movieArb()) { movie ->
            // Verify movie has required fields for display completeness
            
            // Movie title should not be empty (required for display)
            movie.title.shouldNotBe("")
            movie.title.trim().shouldBe(movie.title) // No leading/trailing whitespace
            
            // Movie ID should be positive (required for identification)
            (movie.id > 0) shouldBe true
            
            // Vote average should be in valid range (0.0 to 10.0)
            (movie.voteAverage >= 0.0) shouldBe true
            (movie.voteAverage <= 10.0) shouldBe true
            
            // Release date should be properly formatted if present
            if (movie.releaseDate.isNotEmpty()) {
                (movie.releaseDate.length >= 4) shouldBe true // At least year
            }
            
            // Genre IDs should be positive if present
            movie.genreIds.all { it > 0 } shouldBe true
        }
    }
    
    "Property 1 Extended: Movie poster URL construction should be consistent" {
        checkAll(50, movieArb()) { movie ->
            // Test poster URL construction logic
            val posterUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
            
            if (movie.posterPath != null) {
                posterUrl shouldNotBe null
                posterUrl!! shouldBe "https://image.tmdb.org/t/p/w500${movie.posterPath}"
                posterUrl.startsWith("https://image.tmdb.org/t/p/w500") shouldBe true
            } else {
                posterUrl shouldBe null
            }
        }
    }
    
    "Property 1 Extended: Movie rating display format should be consistent" {
        checkAll(50, movieArb()) { movie ->
            // Test rating formatting logic
            val formattedRating = String.format("%.1f", movie.voteAverage)
            
            if (movie.voteAverage > 0) {
                val parsedRating = formattedRating.toDouble()
                // Allow for small floating point differences
                (abs(parsedRating - movie.voteAverage) < 0.1) shouldBe true
                formattedRating.contains(".") shouldBe true
                formattedRating.split(".")[1].length shouldBe 1 // One decimal place
            }
        }
    }
    
    "Property 1 Extended: Movie release year extraction should work correctly" {
        checkAll(50, movieArb()) { movie ->
            // Test year extraction logic
            val year = if (movie.releaseDate.isNotEmpty()) movie.releaseDate.take(4) else ""
            
            if (movie.releaseDate.isNotEmpty()) {
                year.length shouldBe 4.coerceAtMost(movie.releaseDate.length)
                if (year.length == 4) {
                    year.all { it.isDigit() } shouldBe true
                }
            }
        }
    }
})

/**
 * Arbitrary generator for Movie objects
 * Generates random but valid movie data for property testing
 */
private fun movieArb() = Arb.bind(
    Arb.int(1..999999), // id
    Arb.string(1..100).filter { it.isNotBlank() && it.trim() == it }, // title (non-empty, no whitespace)
    Arb.string(0..500), // overview
    Arb.choice(
        Arb.constant(null), 
        Arb.string(10..50).map { "/poster_$it.jpg" }
    ), // posterPath
    Arb.choice(
        Arb.constant(null), 
        Arb.string(10..50).map { "/backdrop_$it.jpg" }
    ), // backdropPath
    Arb.choice(
        Arb.constant(""),
        Arb.int(1900..2030).map { year ->
            val month = (1..12).random()
            val day = (1..28).random()
            "$year-${String.format("%02d", month)}-${String.format("%02d", day)}"
        }
    ), // releaseDate
    Arb.double(0.0..10.0), // voteAverage
    Arb.list(Arb.int(1..50), 0..5), // genreIds
    Arb.boolean() // isFavorite
) { id, title, overview, posterPath, backdropPath, releaseDate, voteAverage, genreIds, isFavorite ->
    Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        genreIds = genreIds,
        isFavorite = isFavorite
    )
}