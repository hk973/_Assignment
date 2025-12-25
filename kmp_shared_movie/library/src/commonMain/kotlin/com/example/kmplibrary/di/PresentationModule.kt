package com.example.kmplibrary.di

import com.example.kmplibrary.presentation.viewmodel.FavoritesViewModel
import com.example.kmplibrary.presentation.viewmodel.MovieDetailViewModel
import com.example.kmplibrary.presentation.viewmodel.MoviesViewModel
import org.koin.dsl.module

/**
 * Koin module for presentation layer dependencies (ViewModels)
 */
val presentationModule = module {
    
    // ViewModels
    factory { MoviesViewModel(get(), get(), get()) }
    factory { MovieDetailViewModel(get(), get()) }
    factory { FavoritesViewModel(get(), get()) }
}