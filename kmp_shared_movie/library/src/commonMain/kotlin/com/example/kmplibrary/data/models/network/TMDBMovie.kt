package com.example.kmplibrary.data.models.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TMDB API movie model for basic movie information
 */
@Serializable
data class TMDBMovie(
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
    
    @SerialName("genre_ids")
    val genreIds: List<Int>,
    
    @SerialName("adult")
    val adult: Boolean = false,
    
    @SerialName("original_language")
    val originalLanguage: String,
    
    @SerialName("original_title")
    val originalTitle: String,
    
    @SerialName("popularity")
    val popularity: Double,
    
    @SerialName("video")
    val video: Boolean = false
)