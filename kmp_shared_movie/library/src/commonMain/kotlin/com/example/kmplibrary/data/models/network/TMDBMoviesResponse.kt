package com.example.kmplibrary.data.models.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TMDB API response for movies list endpoints (popular, search, etc.)
 */
@Serializable
data class TMDBMoviesResponse(
    @SerialName("page")
    val page: Int,
    
    @SerialName("results")
    val results: List<TMDBMovie>,
    
    @SerialName("total_pages")
    val totalPages: Int,
    
    @SerialName("total_results")
    val totalResults: Int
)