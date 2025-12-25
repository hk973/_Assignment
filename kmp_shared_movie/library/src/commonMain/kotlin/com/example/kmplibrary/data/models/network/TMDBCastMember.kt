package com.example.kmplibrary.data.models.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TMDB API cast member model
 */
@Serializable
data class TMDBCastMember(
    @SerialName("id")
    val id: Int,
    
    @SerialName("name")
    val name: String,
    
    @SerialName("character")
    val character: String,
    
    @SerialName("profile_path")
    val profilePath: String? = null,
    
    @SerialName("cast_id")
    val castId: Int,
    
    @SerialName("credit_id")
    val creditId: String,
    
    @SerialName("gender")
    val gender: Int? = null,
    
    @SerialName("known_for_department")
    val knownForDepartment: String,
    
    @SerialName("order")
    val order: Int,
    
    @SerialName("original_name")
    val originalName: String,
    
    @SerialName("popularity")
    val popularity: Double
)