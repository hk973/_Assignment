package com.example.kmplibrary.domain.usecase

import com.example.kmplibrary.data.local.TestFavoritesStorage
import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.data.repository.FavoritesRepository
import com.example.kmplibrary.data.repository.FavoritesRepositoryImpl
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest

/**
 * **Feature: movie-showcase-app, Property 6: Favorite Toggle Consistency**
 * Property-based tests for favorite toggle functionality
 */
class ToggleFavoriteUseCasePropertyTest : StringSpec({
    
    "Toggling favorite status should immediately update the movie's favorite state" {
        checkAll(100, movieArb()) { movie ->
            val favoritesStorage = TestFavoritesStorage()
            val favoritesRepository = FavoritesRepositoryImpl(favoritesStorage)
            val toggleUseCase = ToggleFavoriteUseCaseImpl(favoritesRepository)
            
            // Initially not favorite
            toggleUseCase.isFavorite(movie.id) shouldBe false
            
            // Toggle to favorite
            val toggleResult1 = toggleUseCase.toggleFavorite(movie)
            toggleResult1.isSuccess shouldBe true
            toggleResult1.getOrNull() shouldBe true
            toggleUseCase.isFavorite(movie.id) shouldBe true
            
            // Toggle back to not favorite
            val toggleResult2 = toggleUseCase.toggleFavorite(movie)
            toggleResult2.isSuccess shouldBe true
            toggleResult2.getOrNull() shouldBe false
            toggleUseCase.isFavorite(movie.id) shouldBe false
        }
    }
    
    "Adding to favorites should make movie favorite" {
        checkAll(100, movieArb()) { movie ->
            val favoritesStorage = TestFavoritesStorage()
            val favoritesRepository = FavoritesRepositoryImpl(favoritesStorage)
            val toggleUseCase = ToggleFavoriteUseCaseImpl(favoritesRepository)
            
            // Initially not favorite
            toggleUseCase.isFavorite(movie.id) shouldBe false
            
            // Add to favorites
            val addResult = toggleUseCase.addToFavorites(movie)
            addResult.isSuccess shouldBe true
            toggleUseCase.isFavorite(movie.id) shouldBe true
        }
    }
    
    "Removing from favorites should make movie not favorite" {
        checkAll(100, movieArb()) { movie ->
            val favoritesStorage = TestFavoritesStorage()
            val favoritesRepository = FavoritesRepositoryImpl(favoritesStorage)
            val toggleUseCase = ToggleFavoriteUseCaseImpl(favoritesRepository)
            
            // Add to favorites first
            toggleUseCase.addToFavorites(movie)
            toggleUseCase.isFavorite(movie.id) shouldBe true
            
            // Remove from favorites
            val removeResult = toggleUseCase.removeFromFavorites(movie.id)
            removeResult.isSuccess shouldBe true
            toggleUseCase.isFavorite(movie.id) shouldBe false
        }
    }
    
    "Multiple toggles should maintain consistency" {
        checkAll(100, movieArb(), Arb.int(1..10)) { movie, toggleCount ->
            val favoritesStorage = TestFavoritesStorage()
            val favoritesRepository = FavoritesRepositoryImpl(favoritesStorage)
            val toggleUseCase = ToggleFavoriteUseCaseImpl(favoritesRepository)
            
            var expectedFavorite = false
            
            repeat(toggleCount) {
                val toggleResult = toggleUseCase.toggleFavorite(movie)
                toggleResult.isSuccess shouldBe true
                
                expectedFavorite = !expectedFavorite
                toggleResult.getOrNull() shouldBe expectedFavorite
                toggleUseCase.isFavorite(movie.id) shouldBe expectedFavorite
            }
        }
    }
    
    "Toggle favorite should be idempotent for add/remove operations" {
        checkAll(100, movieArb()) { movie ->
            val favoritesStorage = TestFavoritesStorage()
            val favoritesRepository = FavoritesRepositoryImpl(favoritesStorage)
            val toggleUseCase = ToggleFavoriteUseCaseImpl(favoritesRepository)
            
            // Add multiple times should not cause issues
            toggleUseCase.addToFavorites(movie)
            toggleUseCase.addToFavorites(movie)
            toggleUseCase.addToFavorites(movie)
            toggleUseCase.isFavorite(movie.id) shouldBe true
            
            // Remove multiple times should not cause issues
            toggleUseCase.removeFromFavorites(movie.id)
            toggleUseCase.removeFromFavorites(movie.id)
            toggleUseCase.removeFromFavorites(movie.id)
            toggleUseCase.isFavorite(movie.id) shouldBe false
        }
    }
    
    "Favorite status should persist across different movie instances with same ID" {
        checkAll(100, Arb.int(1..1000)) { movieId ->
            val favoritesStorage = TestFavoritesStorage()
            val favoritesRepository = FavoritesRepositoryImpl(favoritesStorage)
            val toggleUseCase = ToggleFavoriteUseCaseImpl(favoritesRepository)
            
            // Create two different movie instances with same ID
            val movie1 = createMovieWithId(movieId, "Movie 1")
            val movie2 = createMovieWithId(movieId, "Movie 2")
            
            // Add first movie to favorites
            toggleUseCase.addToFavorites(movie1)
            
            // Both should be considered favorite since they have same ID
            toggleUseCase.isFavorite(movie1.id) shouldBe true
            toggleUseCase.isFavorite(movie2.id) shouldBe true
            
            // Toggle with second movie should affect both
            val toggleResult = toggleUseCase.toggleFavorite(movie2)
            toggleResult.getOrNull() shouldBe false
            toggleUseCase.isFavorite(movie1.id) shouldBe false
            toggleUseCase.isFavorite(movie2.id) shouldBe false
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

private fun createMovieWithId(id: Int, title: String) = Movie(
    id = id,
    title = title,
    overview = "Test overview",
    posterPath = "/test.jpg",
    backdropPath = "/backdrop.jpg",
    releaseDate = "2024-01-01",
    voteAverage = 8.0,
    genreIds = listOf(28, 12),
    isFavorite = false
)