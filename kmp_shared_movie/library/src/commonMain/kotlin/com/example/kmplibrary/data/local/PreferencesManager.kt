package com.example.kmplibrary.data.local

/**
 * Platform-specific preferences manager interface
 */
expect class PreferencesManager {
    /**
     * Store a string value
     */
    fun putString(key: String, value: String)
    
    /**
     * Retrieve a string value
     */
    fun getString(key: String, defaultValue: String): String
    
    /**
     * Store an integer value
     */
    fun putInt(key: String, value: Int)
    
    /**
     * Retrieve an integer value
     */
    fun getInt(key: String, defaultValue: Int): Int
    
    /**
     * Store a boolean value
     */
    fun putBoolean(key: String, value: Boolean)
    
    /**
     * Retrieve a boolean value
     */
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    
    /**
     * Remove a key-value pair
     */
    fun remove(key: String)
    
    /**
     * Clear all preferences
     */
    fun clear()
    
    /**
     * Check if a key exists
     */
    fun contains(key: String): Boolean
}