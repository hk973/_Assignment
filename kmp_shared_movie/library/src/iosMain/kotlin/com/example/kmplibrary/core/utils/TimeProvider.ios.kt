package com.example.kmplibrary.core.utils

/**
 * iOS implementation of TimeProvider
 * Simple implementation for compilation compatibility
 */
actual object TimeProvider {
    private var startTime = 1640995200000L // Jan 1, 2022 as base
    
    actual fun currentTimeMillis(): Long {
        // Simple implementation that provides incrementing timestamps
        // In a real iOS app, this would use proper NSDate APIs
        return startTime + (kotlin.random.Random.nextLong(0, 1000000))
    }
}