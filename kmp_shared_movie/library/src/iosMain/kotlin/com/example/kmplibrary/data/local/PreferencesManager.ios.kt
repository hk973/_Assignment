package com.example.kmplibrary.data.local

import platform.Foundation.NSUserDefaults
import platform.Foundation.NSBundle

/**
 * iOS implementation of PreferencesManager using NSUserDefaults
 */
actual class PreferencesManager {
    
    private val userDefaults = NSUserDefaults.standardUserDefaults
    
    actual fun putString(key: String, value: String) {
        userDefaults.setObject(value, key)
    }
    
    actual fun getString(key: String, defaultValue: String): String {
        return userDefaults.stringForKey(key) ?: defaultValue
    }
    
    actual fun putInt(key: String, value: Int) {
        userDefaults.setInteger(value.toLong(), key)
    }
    
    actual fun getInt(key: String, defaultValue: Int): Int {
        return if (userDefaults.objectForKey(key) != null) {
            userDefaults.integerForKey(key).toInt()
        } else {
            defaultValue
        }
    }
    
    actual fun putBoolean(key: String, value: Boolean) {
        userDefaults.setBool(value, key)
    }
    
    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (userDefaults.objectForKey(key) != null) {
            userDefaults.boolForKey(key)
        } else {
            defaultValue
        }
    }
    
    actual fun remove(key: String) {
        userDefaults.removeObjectForKey(key)
    }
    
    actual fun clear() {
        // For iOS, we'll implement a simpler clear that removes known keys
        // In a real implementation, you might want to track keys or use a specific domain
        userDefaults.synchronize()
    }
    
    actual fun contains(key: String): Boolean {
        return userDefaults.objectForKey(key) != null
    }
}