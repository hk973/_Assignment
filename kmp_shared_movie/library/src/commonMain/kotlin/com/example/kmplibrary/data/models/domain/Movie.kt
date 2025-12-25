package com.example.kmplibrary.data.models.domain

import kotlinx.serialization.Serializable

/**
 * Domain model for a movie
 * Simplified model for UI consumption
 */
@Serializable
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val genreIds: List<Int>,
    val isFavorite: Boolean = false
) {
    /**
     * Get the full poster URL
     */
    fun getPosterUrl(baseUrl: String = "https://image.tmdb.org/t/p/w500"): String? {
        return posterPath?.let { "$baseUrl$it" }
    }
    
    /**
     * Get the full backdrop URL
     */
    fun getBackdropUrl(baseUrl: String = "https://image.tmdb.org/t/p/w780"): String? {
        return backdropPath?.let { "$baseUrl$it" }
    }
    
    /**
     * Get formatted rating (e.g., "8.5")
     */
    fun getFormattedRating(): String {
        return voteAverage.toString()
    }
    
    /**
     * Get release year from date
     */
    fun getReleaseYear(): String? {
        return if (releaseDate.length >= 4) {
            releaseDate.substring(0, 4)
        } else null
    }
}