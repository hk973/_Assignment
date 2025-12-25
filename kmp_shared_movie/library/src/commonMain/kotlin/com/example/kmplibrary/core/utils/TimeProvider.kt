package com.example.kmplibrary.core.utils

/**
 * Platform-specific time provider
 */
expect object TimeProvider {
    /**
     * Get current time in milliseconds
     */
    fun currentTimeMillis(): Long
}