package com.example.kmplibrary.di

import org.koin.core.module.Module

/**
 * All Koin modules for the KMP library
 */
object KoinModules {
    
    /**
     * Get all modules needed for the KMP library
     */
    fun getAllModules(): List<Module> = listOf(
        platformModule,
        networkModule,
        dataModule,
        domainModule,
        presentationModule
    )
    
    /**
     * Get core modules (without platform-specific dependencies)
     * Useful for testing
     */
    fun getCoreModules(): List<Module> = listOf(
        networkModule,
        dataModule,
        domainModule,
        presentationModule
    )
}