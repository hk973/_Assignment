package com.example.kmplibrary.data.models.mappers

import com.example.kmplibrary.data.models.domain.*
import com.example.kmplibrary.data.models.network.*

/**
 * Mapper functions to convert TMDB API models to domain models
 */

/**
 * Convert TMDBMovie to domain Movie
 */
fun TMDBMovie.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        genreIds = genreIds
    )
}

/**
 * Convert list of TMDBMovie to list of domain Movie
 */
fun List<TMDBMovie>.toMovieDomain(): List<Movie> {
    return map { it.toDomain() }
}

/**
 * Convert TMDBMovieDetail to domain MovieDetail
 */
fun TMDBMovieDetail.toDomain(director: String? = null, cast: List<CastMember> = emptyList()): MovieDetail {
    return MovieDetail(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        runtime = runtime,
        voteAverage = voteAverage,
        genres = genres.toGenreDomain(),
        director = director,
        cast = cast
    )
}

/**
 * Convert TMDBGenre to domain Genre
 */
fun TMDBGenre.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

/**
 * Convert list of TMDBGenre to list of domain Genre
 */
fun List<TMDBGenre>.toGenreDomain(): List<Genre> {
    return map { it.toDomain() }
}

/**
 * Convert TMDBCastMember to domain CastMember
 */
fun TMDBCastMember.toDomain(): CastMember {
    return CastMember(
        id = id,
        name = name,
        character = character,
        profilePath = profilePath
    )
}

/**
 * Convert list of TMDBCastMember to list of domain CastMember
 */
fun List<TMDBCastMember>.toCastDomain(): List<CastMember> {
    return map { it.toDomain() }
}

/**
 * Extract director from TMDBCreditsResponse
 */
fun TMDBCreditsResponse.getDirector(): String? {
    return crew.firstOrNull { it.job == "Director" }?.name
}

/**
 * Convert TMDBCreditsResponse to domain cast list
 */
fun TMDBCreditsResponse.getCast(): List<CastMember> {
    return cast.toCastDomain()
}