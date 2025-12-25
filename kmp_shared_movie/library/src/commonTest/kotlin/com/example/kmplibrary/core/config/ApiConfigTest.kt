package com.example.kmplibrary.core.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApiConfigTest {
    
    @Test
    fun testApiConfigConstants() {
        assertEquals("https://api.themoviedb.org/3/", ApiConfig.TMDB_BASE_URL)
        assertEquals("https://image.tmdb.org/t/p/", ApiConfig.TMDB_IMAGE_BASE_URL)
        assertEquals("w500", ApiConfig.TMDB_POSTER_SIZE)
        assertEquals("w780", ApiConfig.TMDB_BACKDROP_SIZE)
    }
    
    @Test
    fun testApiKeyProviderExists() {
        val apiKey = ApiKeyProvider.getTmdbApiKey()
        assertNotNull(apiKey)
    }
}