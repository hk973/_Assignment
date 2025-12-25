package com.example.kmplibrary.di

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE

/**
 * Koin initializer for the KMP library
 */
object KoinInitializer {
    
    /**
     * Initialize Koin with all required modules
     */
    fun init(enableLogging: Boolean = false) {
        startKoin {
            if (enableLogging) {
                logger(KoinLogger(Level.DEBUG))
            }
            modules(KoinModules.getAllModules())
        }
    }
    
    /**
     * Stop Koin (useful for testing)
     */
    fun stop() {
        stopKoin()
    }
}

/**
 * Custom Koin logger for better debugging
 */
class KoinLogger(level: Level = Level.INFO) : Logger(level) {
    override fun display(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> println("🔧 KOIN [DEBUG]: $msg")
            Level.INFO -> println("ℹ️ KOIN [INFO]: $msg")
            Level.WARNING -> println("⚠️ KOIN [WARNING]: $msg")
            Level.ERROR -> println("❌ KOIN [ERROR]: $msg")
            Level.NONE -> { /* No logging */ }
        }
    }
}