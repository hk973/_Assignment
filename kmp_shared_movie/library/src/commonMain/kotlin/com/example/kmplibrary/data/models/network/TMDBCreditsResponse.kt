package com.example.kmplibrary.data.models.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TMDB API credits response for movie cast and crew
 */
@Serializable
data class TMDBCreditsResponse(
    @SerialName("id")
    val id: Int,
    
    @SerialName("cast")
    val cast: List<TMDBCastMember>,
    
    @SerialName("crew")
    val crew: List<TMDBCrewMember>
)