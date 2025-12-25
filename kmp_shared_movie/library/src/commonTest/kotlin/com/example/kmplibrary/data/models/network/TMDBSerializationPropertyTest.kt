package com.example.kmplibrary.data.models.network

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * **Feature: movie-showcase-app, Property 11: API Data Serialization Round-trip**
 * Property-based tests for TMDB API model serialization
 */
class TMDBSerializationPropertyTest : StringSpec({
    
    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    "TMDBMovie serialization round-trip should preserve data" {
        checkAll(100, tmdbMovieArb()) { movie ->
            val serialized = json.encodeToString(movie)
            val deserialized = json.decodeFromString<TMDBMovie>(serialized)
            deserialized shouldBe movie
        }
    }
    
    "TMDBMoviesResponse serialization round-trip should preserve data" {
        checkAll(100, tmdbMoviesResponseArb()) { response ->
            val serialized = json.encodeToString(response)
            val deserialized = json.decodeFromString<TMDBMoviesResponse>(serialized)
            deserialized shouldBe response
        }
    }
    
    "TMDBMovieDetail serialization round-trip should preserve data" {
        checkAll(100, tmdbMovieDetailArb()) { movieDetail ->
            val serialized = json.encodeToString(movieDetail)
            val deserialized = json.decodeFromString<TMDBMovieDetail>(serialized)
            deserialized shouldBe movieDetail
        }
    }
    
    "TMDBGenre serialization round-trip should preserve data" {
        checkAll(100, tmdbGenreArb()) { genre ->
            val serialized = json.encodeToString(genre)
            val deserialized = json.decodeFromString<TMDBGenre>(serialized)
            deserialized shouldBe genre
        }
    }
    
    "TMDBCastMember serialization round-trip should preserve data" {
        checkAll(100, tmdbCastMemberArb()) { castMember ->
            val serialized = json.encodeToString(castMember)
            val deserialized = json.decodeFromString<TMDBCastMember>(serialized)
            deserialized shouldBe castMember
        }
    }
    
    "TMDBCreditsResponse serialization round-trip should preserve data" {
        checkAll(100, tmdbCreditsResponseArb()) { credits ->
            val serialized = json.encodeToString(credits)
            val deserialized = json.decodeFromString<TMDBCreditsResponse>(serialized)
            deserialized shouldBe credits
        }
    }
})

// Arbitrary generators for TMDB models
private fun tmdbMovieArb() = arbitrary {
    TMDBMovie(
        id = Arb.int(1..1000000).bind(),
        title = Arb.string(1..100).bind(),
        overview = Arb.string(0..1000).bind(),
        posterPath = Arb.string(1..50).orNull().bind(),
        backdropPath = Arb.string(1..50).orNull().bind(),
        releaseDate = Arb.string(10..10).bind(), // YYYY-MM-DD format
        voteAverage = Arb.double(0.0..10.0).bind(),
        voteCount = Arb.int(0..100000).bind(),
        genreIds = Arb.list(Arb.int(1..100), 0..5).bind(),
        adult = Arb.boolean().bind(),
        originalLanguage = Arb.string(2..5).bind(),
        originalTitle = Arb.string(1..100).bind(),
        popularity = Arb.double(0.0..1000.0).bind(),
        video = Arb.boolean().bind()
    )
}

private fun tmdbMoviesResponseArb() = arbitrary {
    TMDBMoviesResponse(
        page = Arb.int(1..1000).bind(),
        results = Arb.list(tmdbMovieArb(), 0..20).bind(),
        totalPages = Arb.int(1..1000).bind(),
        totalResults = Arb.int(0..20000).bind()
    )
}

private fun tmdbGenreArb() = arbitrary {
    TMDBGenre(
        id = Arb.int(1..100).bind(),
        name = Arb.string(1..50).bind()
    )
}

private fun tmdbProductionCompanyArb() = arbitrary {
    TMDBProductionCompany(
        id = Arb.int(1..10000).bind(),
        name = Arb.string(1..100).bind(),
        logoPath = Arb.string(1..50).orNull().bind(),
        originCountry = Arb.string(2..2).bind()
    )
}

private fun tmdbProductionCountryArb() = arbitrary {
    TMDBProductionCountry(
        iso31661 = Arb.string(2..2).bind(),
        name = Arb.string(1..50).bind()
    )
}

private fun tmdbSpokenLanguageArb() = arbitrary {
    TMDBSpokenLanguage(
        englishName = Arb.string(1..50).bind(),
        iso6391 = Arb.string(2..2).bind(),
        name = Arb.string(1..50).bind()
    )
}

private fun tmdbMovieDetailArb() = arbitrary {
    TMDBMovieDetail(
        id = Arb.int(1..1000000).bind(),
        title = Arb.string(1..100).bind(),
        overview = Arb.string(0..1000).bind(),
        posterPath = Arb.string(1..50).orNull().bind(),
        backdropPath = Arb.string(1..50).orNull().bind(),
        releaseDate = Arb.string(10..10).bind(),
        voteAverage = Arb.double(0.0..10.0).bind(),
        voteCount = Arb.int(0..100000).bind(),
        runtime = Arb.int(60..300).orNull().bind(),
        genres = Arb.list(tmdbGenreArb(), 0..5).bind(),
        productionCompanies = Arb.list(tmdbProductionCompanyArb(), 0..5).bind(),
        productionCountries = Arb.list(tmdbProductionCountryArb(), 0..5).bind(),
        spokenLanguages = Arb.list(tmdbSpokenLanguageArb(), 0..5).bind(),
        adult = Arb.boolean().bind(),
        budget = Arb.long(0L..1000000000L).bind(),
        homepage = Arb.string(1..100).orNull().bind(),
        imdbId = Arb.string(9..9).orNull().bind(),
        originalLanguage = Arb.string(2..5).bind(),
        originalTitle = Arb.string(1..100).bind(),
        popularity = Arb.double(0.0..1000.0).bind(),
        revenue = Arb.long(0L..2000000000L).bind(),
        status = Arb.string(1..20).bind(),
        tagline = Arb.string(1..200).orNull().bind(),
        video = Arb.boolean().bind()
    )
}

private fun tmdbCastMemberArb() = arbitrary {
    TMDBCastMember(
        id = Arb.int(1..1000000).bind(),
        name = Arb.string(1..100).bind(),
        character = Arb.string(1..100).bind(),
        profilePath = Arb.string(1..50).orNull().bind(),
        castId = Arb.int(1..1000).bind(),
        creditId = Arb.string(1..50).bind(),
        gender = Arb.int(0..2).orNull().bind(),
        knownForDepartment = Arb.string(1..50).bind(),
        order = Arb.int(0..100).bind(),
        originalName = Arb.string(1..100).bind(),
        popularity = Arb.double(0.0..100.0).bind()
    )
}

private fun tmdbCrewMemberArb() = arbitrary {
    TMDBCrewMember(
        id = Arb.int(1..1000000).bind(),
        name = Arb.string(1..100).bind(),
        job = Arb.string(1..50).bind(),
        department = Arb.string(1..50).bind(),
        profilePath = Arb.string(1..50).orNull().bind(),
        creditId = Arb.string(1..50).bind(),
        gender = Arb.int(0..2).orNull().bind(),
        knownForDepartment = Arb.string(1..50).bind(),
        originalName = Arb.string(1..100).bind(),
        popularity = Arb.double(0.0..100.0).bind()
    )
}

private fun tmdbCreditsResponseArb() = arbitrary {
    TMDBCreditsResponse(
        id = Arb.int(1..1000000).bind(),
        cast = Arb.list(tmdbCastMemberArb(), 0..20).bind(),
        crew = Arb.list(tmdbCrewMemberArb(), 0..50).bind()
    )
}