package com.example.kmplibrary.core.config

/**
 * iOS-specific implementation of API key provider
 */
actual object ApiKeyProvider {
    actual fun getTmdbApiKey(): String {
        return ApiConfig.TMDB_API_KEY
    }
}