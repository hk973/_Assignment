package com.example.kmplibrary.core.config

/**
 * Configuration object for API settings
 */
object ApiConfig {
    const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    const val TMDB_POSTER_SIZE = "w500"
    const val TMDB_BACKDROP_SIZE = "w780"
    const val TMDB_API_KEY = "90094606fe03827e47b600913394b0bc" // Replace with your actual TMDB API key
}

/**
 * Platform-specific API key provider
 */
expect object ApiKeyProvider {
    fun getTmdbApiKey(): String
}