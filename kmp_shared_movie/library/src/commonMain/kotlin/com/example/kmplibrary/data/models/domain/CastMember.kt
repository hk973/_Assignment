package com.example.kmplibrary.data.models.domain

/**
 * Domain model for a cast member
 */
data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String?
) {
    /**
     * Get the full profile image URL
     */
    fun getProfileUrl(baseUrl: String = "https://image.tmdb.org/t/p/w185"): String? {
        return profilePath?.let { "$baseUrl$it" }
    }
}