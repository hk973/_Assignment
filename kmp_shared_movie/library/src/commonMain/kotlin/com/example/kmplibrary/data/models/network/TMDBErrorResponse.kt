package com.example.kmplibrary.data.models.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TMDB API error response model
 */
@Serializable
data class TMDBErrorResponse(
    @SerialName("success")
    val success: Boolean = false,
    
    @SerialName("status_code")
    val statusCode: Int,
    
    @SerialName("status_message")
    val statusMessage: String
)