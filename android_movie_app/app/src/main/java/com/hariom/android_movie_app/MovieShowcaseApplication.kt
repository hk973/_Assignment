package com.hariom.android_movie_app

import android.app.Application
import com.example.kmplibrary.di.KoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Application class for Movie Showcase App
 * Initializes Koin dependency injection with KMP library modules
 */
class MovieShowcaseApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin with KMP library modules
        startKoin {
            androidContext(this@MovieShowcaseApplication)
            modules(KoinModules.getAllModules())
        }
    }
}