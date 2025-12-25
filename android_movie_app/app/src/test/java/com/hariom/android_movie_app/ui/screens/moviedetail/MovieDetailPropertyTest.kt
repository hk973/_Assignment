package com.hariom.android_movie_app.ui.screens.moviedetail

import com.example.kmplibrary.data.models.domain.CastMember
import com.example.kmplibrary.data.models.domain.Genre
import com.example.kmplibrary.data.models.domain.MovieDetail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll

/**
 * Property-based tests for movie detail display completeness
 * **Feature: movie-showcase-app, Property 10: Movie Detail Display Completeness**
 * **Validates: Requirements 4.3**
 * 
 * Tests that for any movie detail, the displayed information includes:
 * - Movie title
 * - Synopsis
 * - Rating
 * - Release date
 * - Director
 * - Cast
 */
class MovieDetailPropertyTest : StringSpec({
    
    "Property 10: Movie detail display should contain all required information" {
        checkAll(100, arbMovieDetail()) { movieDetail ->
            // Simulate rendering the movie detail to a string representation
            val renderedContent = renderMovieDetailToString(movieDetail)
            
            // Verify all required fields are present in the rendered content
            renderedContent shouldContain movieDetail.title
            renderedContent shouldContain movieDetail.overview
            renderedContent shouldContain movieDetail.voteAverage.toString()
            renderedContent shouldContain movieDetail.releaseDate
            
            val director = movieDetail.director
            if (director != null) {
                renderedContent shouldContain director
            }
            
            // Verify cast members are included
            movieDetail.cast.take(5).forEach { castMember ->
                renderedContent shouldContain castMember.name
                renderedContent shouldContain castMember.character
            }
        }
    }
    
    "Property 10: Movie detail should display formatted rating" {
        checkAll(100, arbMovieDetail()) { movieDetail ->
            val formattedRating = movieDetail.getFormattedRating()
            
            // Rating should be present and properly formatted
            formattedRating.toDoubleOrNull() shouldBe movieDetail.voteAverage
        }
    }
    
    "Property 10: Movie detail should display release year" {
        checkAll(100, arbMovieDetail()) { movieDetail ->
            val releaseYear = movieDetail.getReleaseYear()
            
            // If release date is valid, year should be extracted
            if (movieDetail.releaseDate.length >= 4) {
                releaseYear shouldBe movieDetail.releaseDate.substring(0, 4)
            }
        }
    }
    
    "Property 10: Movie detail should display formatted runtime" {
        checkAll(100, arbMovieDetail()) { movieDetail ->
            val formattedRuntime = movieDetail.getFormattedRuntime()
            
            // If runtime exists, it should be formatted
            if (movieDetail.runtime != null) {
                formattedRuntime shouldContain "h"
                formattedRuntime shouldContain "m"
            }
        }
    }
    
    "Property 10: Movie detail should display genres as comma-separated string" {
        checkAll(100, arbMovieDetail()) { movieDetail ->
            val genresString = movieDetail.getGenresString()
            
            // All genres should be included
            movieDetail.genres.forEach { genre ->
                genresString shouldContain genre.name
            }
        }
    }
})

/**
 * Arbitrary generator for MovieDetail
 */
private fun arbMovieDetail(): Arb<MovieDetail> = arbitrary {
    val id = Arb.int(1..100000).bind()
    val title = Arb.string(5..50).bind()
    val overview = Arb.string(20..200).bind()
    val posterPath = Arb.string(10..30).orNull().bind()
    val backdropPath = Arb.string(10..30).orNull().bind()
    val releaseDate = arbReleaseDate().bind()
    val runtime = Arb.int(60..240).orNull().bind()
    val voteAverage = Arb.double(0.0..10.0).bind()
    val genres = Arb.list(arbGenre(), 1..5).bind()
    val director = Arb.string(5..30).orNull().bind()
    val cast = Arb.list(arbCastMember(), 0..10).bind()
    val isFavorite = Arb.bool().bind()
    
    MovieDetail(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        runtime = runtime,
        voteAverage = voteAverage,
        genres = genres,
        director = director,
        cast = cast,
        isFavorite = isFavorite
    )
}

/**
 * Arbitrary generator for Genre
 */
private fun arbGenre(): Arb<Genre> = arbitrary {
    val id = Arb.int(1..100).bind()
    val name = Arb.element(
        "Action", "Adventure", "Comedy", "Drama", "Horror", 
        "Sci-Fi", "Thriller", "Romance", "Fantasy", "Mystery"
    ).bind()
    
    Genre(id = id, name = name)
}

/**
 * Arbitrary generator for CastMember
 */
private fun arbCastMember(): Arb<CastMember> = arbitrary {
    val id = Arb.int(1..100000).bind()
    val name = Arb.string(5..30).bind()
    val character = Arb.string(5..30).bind()
    val profilePath = Arb.string(10..30).orNull().bind()
    
    CastMember(
        id = id,
        name = name,
        character = character,
        profilePath = profilePath
    )
}

/**
 * Arbitrary generator for release date in YYYY-MM-DD format
 */
private fun arbReleaseDate(): Arb<String> = arbitrary {
    val year = Arb.int(1900..2024).bind()
    val month = Arb.int(1..12).bind()
    val day = Arb.int(1..28).bind()
    
    String.format("%04d-%02d-%02d", year, month, day)
}

/**
 * Simulate rendering movie detail to a string representation
 * This represents what would be displayed in the UI
 */
private fun renderMovieDetailToString(movieDetail: MovieDetail): String {
    return buildString {
        appendLine("Title: ${movieDetail.title}")
        appendLine("Rating: ${movieDetail.voteAverage}")
        appendLine("Release Date: ${movieDetail.releaseDate}")
        appendLine("Year: ${movieDetail.getReleaseYear()}")
        
        movieDetail.getFormattedRuntime()?.let {
            appendLine("Runtime: $it")
        }
        
        appendLine("Genres: ${movieDetail.getGenresString()}")
        appendLine("Synopsis: ${movieDetail.overview}")
        
        movieDetail.director?.let {
            appendLine("Director: $it")
        }
        
        appendLine("Cast:")
        movieDetail.getMainCast().forEach { castMember ->
            appendLine("  ${castMember.name} as ${castMember.character}")
        }
    }
}
