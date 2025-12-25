package com.example.kmplibrary.core.utils

/**
 * Android implementation of TimeProvider
 */
actual object TimeProvider {
    actual fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}