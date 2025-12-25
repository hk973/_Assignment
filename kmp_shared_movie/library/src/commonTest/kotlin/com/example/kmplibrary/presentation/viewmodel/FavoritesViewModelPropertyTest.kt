package com.example.kmplibrary.presentation.viewmodel

import com.example.kmplibrary.data.local.TestFavoritesStorage
import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.data.repository.FavoritesRepository
import com.example.kmplibrary.data.repository.FavoritesRepositoryImpl
import com.example.kmplibrary.domain.usecase.ToggleFavoriteUseCase
import com.example.kmplibrary.domain.usecase.ToggleFavoriteUseCaseImpl
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

/**
 * **Feature: movie-showcase-app, Property 7: Favorites State Synchronization**
 * **Feature: movie-showcase-app, Property 8: Favorites Screen Accuracy**
 * Property-based tests for favorites state synchronization across ViewModels
 */
class FavoritesViewModelPropertyTest : StringSpec({
    
    "Favorites state should synchronize across all screens immediately" {
        checkAll(100, movieArb()) { movie ->
            val favoritesStorage = TestFavoritesStorage()
            val favoritesRepository = FavoritesRepositoryImpl(favoritesStorage)
            val toggleFavoriteUseCase = ToggleFavoriteUseCaseImpl(favoritesRepository)
            
            val favoritesViewModel = FavoritesViewModel(favoritesRepository, toggleFavoriteUseCase)
            
            // Initially no favorites
            delay(100)
            val initialState = favoritesViewModel.uiState.first()
            initialState.favorites.isEmpty() shouldBe true
            
            // Add movie to favorites through use case (simulating action from another screen)
            toggleFavoriteUseCase.addToFavorites(movie)
            delay(100)
            
            // Favorites screen should reflect the change immediately
            val updatedState = favoritesViewModel.uiState.first()
            updatedState.favorites.any { it.id == movie.id } shouldBe true
        }
    }
    
    "Favorites screen should display exactly the favorited movies and no others" {
        checkAll(100, Arb.list(movieArb(), 1..10)) { movies ->
            val favoritesStorage = TestFavoritesStorage()
            val favoritesRepository = FavoritesRepositoryImpl(favoritesStorage)
            val toggleFavoriteUseCase = ToggleFavoriteUseCaseImpl(favoritesRepository)
            
            val favoritesViewModel = FavoritesViewModel(favoritesRepository, toggleFavoriteUseCase)
            
            // Add some movies to favorites
            val moviesToFavorite = movies.take(movies.size / 2)
            moviesToFavorite.forEach { movie ->
                toggleFavoriteUseCase.addToFavorites(movie)
            }
            
            delay(100)
            val state = favoritesViewModel.uiState.first()
            
            // Favorites screen should show exactly the favorited movies
            state.favorites.size shouldBe moviesToFavorite.size
            moviesToFavorite.forEach { movie ->
                state.favorites.any { it.id == movie.id } shouldBe true
            }
            
            // Should not show non-favorited movies
            val nonFavoritedMovies = movies.drop(movies.size / 2)
            nonFavoritedMovies.forEach { movie ->
                state.favorites.any { it.id == movie.id } shouldBe false
            }
        }
    }
    
    "Removing favorite from favorites screen should update state immediately" {
        checkAll(100, movieArb()) { movie ->
            val favoritesStorage = TestFavoritesStorage()
            val favoritesRepository = FavoritesRepositoryImpl(favoritesStorage)
            val toggleFavoriteUseCase = ToggleFavoriteUseCaseImpl(favoritesRepository)
            
            val favoritesViewModel = FavoritesViewModel(favoritesRepository, toggleFavoriteUseCase)
            
            // Add movie to favorites first
            toggleFavoriteUseCase.addToFavorites(movie)
            delay(100)
            
            val stateWithFavorite = favoritesViewModel.uiState.first()
            stateWithFavorite.favorites.any { it.id == movie.id } shouldBe true
            
            // Remove from favorites through the ViewModel
            favoritesViewModel.removeFavorite(movie)
            delay(100)
            
            val stateWithoutFavorite = favoritesViewModel.uiState.first()
            stateWithoutFavorite.favorites.any { it.id == movie.id } shouldBe false
        }
    }
    
    "Toggle favorite should update favorites screen state" {
        checkAll(100, movieArb()) { movie ->
            val favoritesStorage = TestFavoritesStorage()
            val favoritesRepository = FavoritesRepositoryImpl(favoritesStorage)
            val toggleFavoriteUseCase = ToggleFavoriteUseCaseImpl(favoritesRepository)
            
            val favoritesViewModel = FavoritesViewModel(favoritesRepository, toggleFavoriteUseCase)
            
            // Initially no favorites
            delay(100)
            val initialState = favoritesViewModel.uiState.first()
            initialState.favorites.isEmpty() shouldBe true
            
            // Toggle favorite (should add)
            favoritesViewModel.toggleFavorite(movie)
            delay(100)
            
            val stateAfterAdd = favoritesViewModel.uiState.first()
            stateAfterAdd.favorites.any { it.id == movie.id } shouldBe true
            
            // Toggle again (should remove)
            favoritesViewModel.toggleFavorite(movie)
            delay(100)
            
            val stateAfterRemove = favoritesViewModel.uiState.first()
            stateAfterRemove.favorites.any { it.id == movie.id } shouldBe false
        }
    }
    
    "Clear all favorites should empty the favorites list" {
        checkAll(100, Arb.list(movieArb(), 1..5)) { movies ->
            val favoritesStorage = TestFavoritesStorage()
            val favoritesRepository = FavoritesRepositoryImpl(favoritesStorage)
            val toggleFavoriteUseCase = ToggleFavoriteUseCaseImpl(favoritesRepository)
            
            val favoritesViewModel = FavoritesViewModel(favoritesRepository, toggleFavoriteUseCase)
            
            // Add movies to favorites
            movies.forEach { movie ->
                toggleFavoriteUseCase.addToFavorites(movie)
            }
            delay(100)
            
            val stateWithFavorites = favoritesViewModel.uiState.first()
            stateWithFavorites.favorites.isNotEmpty() shouldBe true
            
            // Clear all favorites
            favoritesViewModel.clearAllFavorites()
            delay(100)
            
            val stateAfterClear = favoritesViewModel.uiState.first()
            stateAfterClear.favorites.isEmpty() shouldBe true
        }
    }
    
    "Favorites count should match actual favorites list size" {
        checkAll(100, Arb.list(movieArb(), 0..10)) { movies ->
            val favoritesStorage = TestFavoritesStorage()
            val favoritesRepository = FavoritesRepositoryImpl(favoritesStorage)
            val toggleFavoriteUseCase = ToggleFavoriteUseCaseImpl(favoritesRepository)
            
            val favoritesViewModel = FavoritesViewModel(favoritesRepository, toggleFavoriteUseCase)
            
            // Add movies to favorites
            movies.forEach { movie ->
                toggleFavoriteUseCase.addToFavorites(movie)
            }
            delay(100)
            
            val state = favoritesViewModel.uiState.first()
            val expectedCount = movies.distinctBy { it.id }.size // Account for potential duplicates
            
            favoritesViewModel.getFavoriteCount() shouldBe expectedCount
            state.favorites.size shouldBe expectedCount
        }
    }
})

// Arbitrary generator for Movie domain model
private fun movieArb() = arbitrary {
    Movie(
        id = Arb.int(1..1000000).bind(),
        title = Arb.string(1..100).bind(),
        overview = Arb.string(0..500).bind(),
        posterPath = Arb.string(1..50).orNull().bind(),
        backdropPath = Arb.string(1..50).orNull().bind(),
        releaseDate = Arb.string(10..10).bind(),
        voteAverage = Arb.double(0.0..10.0).bind(),
        genreIds = Arb.list(Arb.int(1..100), 0..5).bind(),
        isFavorite = Arb.boolean().bind()
    )
}