package com.example.kmplibrary.data.models.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TMDB API production country model
 */
@Serializable
data class TMDBProductionCountry(
    @SerialName("iso_3166_1")
    val iso31661: String,
    
    @SerialName("name")
    val name: String
)