package com.example.kmplibrary.data.models.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TMDB API movie detail model for detailed movie information
 */
@Serializable
data class TMDBMovieDetail(
    @SerialName("id")
    val id: Int,
    
    @SerialName("title")
    val title: String,
    
    @SerialName("overview")
    val overview: String,
    
    @SerialName("poster_path")
    val posterPath: String? = null,
    
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    
    @SerialName("release_date")
    val releaseDate: String,
    
    @SerialName("vote_average")
    val voteAverage: Double,
    
    @SerialName("vote_count")
    val voteCount: Int,
    
    @SerialName("runtime")
    val runtime: Int? = null,
    
    @SerialName("genres")
    val genres: List<TMDBGenre>,
    
    @SerialName("production_companies")
    val productionCompanies: List<TMDBProductionCompany>,
    
    @SerialName("production_countries")
    val productionCountries: List<TMDBProductionCountry>,
    
    @SerialName("spoken_languages")
    val spokenLanguages: List<TMDBSpokenLanguage>,
    
    @SerialName("adult")
    val adult: Boolean = false,
    
    @SerialName("budget")
    val budget: Long = 0,
    
    @SerialName("homepage")
    val homepage: String? = null,
    
    @SerialName("imdb_id")
    val imdbId: String? = null,
    
    @SerialName("original_language")
    val originalLanguage: String,
    
    @SerialName("original_title")
    val originalTitle: String,
    
    @SerialName("popularity")
    val popularity: Double,
    
    @SerialName("revenue")
    val revenue: Long = 0,
    
    @SerialName("status")
    val status: String,
    
    @SerialName("tagline")
    val tagline: String? = null,
    
    @SerialName("video")
    val video: Boolean = false
)