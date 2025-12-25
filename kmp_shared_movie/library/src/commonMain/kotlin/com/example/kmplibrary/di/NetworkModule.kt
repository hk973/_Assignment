package com.example.kmplibrary.di

import com.example.kmplibrary.data.network.HttpClientFactory
import com.example.kmplibrary.data.network.TMDBApiClient
import com.example.kmplibrary.data.network.TMDBApiClientImpl
import org.koin.dsl.module

/**
 * Koin module for network layer dependencies
 */
val networkModule = module {
    
    // HTTP Client
    single { HttpClientFactory.create() }
    
    // TMDB API Client
    single<TMDBApiClient> { TMDBApiClientImpl(get()) }
}