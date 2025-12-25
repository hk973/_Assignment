package com.example.kmplibrary.di

import com.example.kmplibrary.data.local.PreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific Koin module
 */
actual val platformModule = module {
    
    // PreferencesManager - requires Android Context
    single { PreferencesManager(androidContext()) }
}