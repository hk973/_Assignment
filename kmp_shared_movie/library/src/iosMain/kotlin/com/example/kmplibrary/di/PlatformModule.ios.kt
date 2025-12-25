package com.example.kmplibrary.di

import com.example.kmplibrary.data.local.PreferencesManager
import org.koin.dsl.module

/**
 * iOS-specific Koin module
 */
actual val platformModule = module {
    
    // PreferencesManager - no additional dependencies needed for iOS
    single { PreferencesManager() }
}