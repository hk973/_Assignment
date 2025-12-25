package com.example.kmplibrary.data.models.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TMDB API crew member model
 */
@Serializable
data class TMDBCrewMember(
    @SerialName("id")
    val id: Int,
    
    @SerialName("name")
    val name: String,
    
    @SerialName("job")
    val job: String,
    
    @SerialName("department")
    val department: String,
    
    @SerialName("profile_path")
    val profilePath: String? = null,
    
    @SerialName("credit_id")
    val creditId: String,
    
    @SerialName("gender")
    val gender: Int? = null,
    
    @SerialName("known_for_department")
    val knownForDepartment: String,
    
    @SerialName("original_name")
    val originalName: String,
    
    @SerialName("popularity")
    val popularity: Double
)