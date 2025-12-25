package com.example.kmplibrary.data.mappers

import com.example.kmplibrary.data.models.domain.*
import com.example.kmplibrary.data.models.network.*

/**
 * Mapper functions to convert TMDB network models to domain models
 */

/**
 * Convert TMDBMovie to domain Movie
 */
fun TMDBMovie.toDomain(isFavorite: Boolean = false): Movie {
    return Movie(
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

/**
 * Convert TMDBMovieDetail to domain MovieDetail
 */
fun TMDBMovieDetail.toDomain(
    director: String? = null,
    cast: List<CastMember> = emptyList(),
    isFavorite: Boolean = false
): MovieDetail {
    return MovieDetail(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        runtime = runtime,
        voteAverage = voteAverage,
        genres = genres.map { it.toDomain() },
        director = director,
        cast = cast,
        isFavorite = isFavorite
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
 * Convert TMDBCreditsResponse to list of CastMembers and find director
 */
fun TMDBCreditsResponse.toDomainCast(): List<CastMember> {
    return cast.map { it.toDomain() }
}

/**
 * Find director from TMDBCreditsResponse
 */
fun TMDBCreditsResponse.findDirector(): String? {
    return crew.find { it.job.equals("Director", ignoreCase = true) }?.name
}

/**
 * Convert list of TMDBMovies to domain Movies with favorite status
 */
fun List<TMDBMovie>.toDomain(favoriteIds: Set<Int> = emptySet()): List<Movie> {
    return map { it.toDomain(isFavorite = favoriteIds.contains(it.id)) }
}

/**
 * Convert TMDBMoviesResponse to list of domain Movies
 */
fun TMDBMoviesResponse.toDomain(favoriteIds: Set<Int> = emptySet()): List<Movie> {
    return results.toDomain(favoriteIds)
}