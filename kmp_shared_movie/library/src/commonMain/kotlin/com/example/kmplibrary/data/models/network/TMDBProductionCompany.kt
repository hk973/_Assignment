package com.example.kmplibrary.data.models.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TMDB API production company model
 */
@Serializable
data class TMDBProductionCompany(
    @SerialName("id")
    val id: Int,
    
    @SerialName("name")
    val name: String,
    
    @SerialName("logo_path")
    val logoPath: String? = null,
    
    @SerialName("origin_country")
    val originCountry: String
)