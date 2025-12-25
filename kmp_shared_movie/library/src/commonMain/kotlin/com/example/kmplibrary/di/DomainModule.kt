package com.example.kmplibrary.di

import com.example.kmplibrary.domain.usecase.*
import org.koin.dsl.module

/**
 * Koin module for domain layer dependencies (use cases)
 */
val domainModule = module {
    
    // Use Cases
    single<GetMoviesUseCase> { GetMoviesUseCaseImpl(get()) }
    single<SearchMoviesUseCase> { SearchMoviesUseCaseImpl(get()) }
    single<ToggleFavoriteUseCase> { ToggleFavoriteUseCaseImpl(get()) }
    single<GetMovieDetailUseCase> { GetMovieDetailUseCaseImpl(get()) }
}