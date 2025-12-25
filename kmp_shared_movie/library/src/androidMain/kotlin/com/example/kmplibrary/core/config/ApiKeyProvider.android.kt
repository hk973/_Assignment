package com.example.kmplibrary.core.config

/**
 * Android-specific implementation of API key provider
 * Returns the TMDB API key
 */
actual object ApiKeyProvider {
    actual fun getTmdbApiKey(): String {
        return ApiConfig.TMDB_API_KEY
    }
}