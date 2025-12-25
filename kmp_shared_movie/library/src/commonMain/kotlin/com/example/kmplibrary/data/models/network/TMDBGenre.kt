package com.example.kmplibrary.data.models.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TMDB API genre model
 */
@Serializable
data class TMDBGenre(
    @SerialName("id")
    val id: Int,
    
    @SerialName("name")
    val name: String
)