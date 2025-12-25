package com.example.kmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Android implementation of PreferencesManager using SharedPreferences
 */
actual class PreferencesManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )
    
    companion object {
        private const val PREFS_NAME = "movie_showcase_prefs"
    }
    
    actual fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).commit()
    }
    
    actual fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
    
    actual fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).commit()
    }
    
    actual fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }
    
    actual fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).commit()
    }
    
    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
    
    actual fun remove(key: String) {
        sharedPreferences.edit().remove(key).commit()
    }
    
    actual fun clear() {
        sharedPreferences.edit().clear().commit()
    }
    
    actual fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }
}