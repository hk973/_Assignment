package com.example.kmplibrary.di

import com.example.kmplibrary.data.local.FavoritesStorage
import com.example.kmplibrary.data.local.FavoritesStorageImpl
import com.example.kmplibrary.data.repository.FavoritesRepository
import com.example.kmplibrary.data.repository.FavoritesRepositoryImpl
import com.example.kmplibrary.data.repository.MoviesRepository
import com.example.kmplibrary.data.repository.MoviesRepositoryImpl
import org.koin.dsl.module

/**
 * Koin module for data layer dependencies
 */
val dataModule = module {
    
    // Local Storage
    single<FavoritesStorage> { FavoritesStorageImpl(get()) }
    
    // Repositories
    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }
    single<MoviesRepository> { MoviesRepositoryImpl(get(), get()) }
}