package com.example.kmplibrary.data.local

import com.example.kmplibrary.data.models.domain.Movie
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.coroutines.flow.first

/**
 * **Feature: movie-showcase-app, Property 9: Favorites Persistence**
 * Property-based tests for favorites storage persistence
 */
class FavoritesStoragePropertyTest : StringSpec({
    
    "Adding a favorite should persist and be retrievable" {
        checkAll(100, movieArb()) { movie ->
            val storage = TestFavoritesStorage()
            
            // Add favorite
            val addResult = storage.addFavorite(movie)
            addResult.isSuccess shouldBe true
            
            // Verify it's persisted
            val favorites = storage.getFavorites().first()
            favorites.any { it.id == movie.id } shouldBe true
            
            // Verify it's marked as favorite
            storage.isFavorite(movie.id) shouldBe true
        }
    }
    
    "Removing a favorite should persist the removal" {
        checkAll(100, movieArb()) { movie ->
            val storage = TestFavoritesStorage()
            
            // Add favorite first
            storage.addFavorite(movie)
            storage.isFavorite(movie.id) shouldBe true
            
            // Remove favorite
            val removeResult = storage.removeFavorite(movie.id)
            removeResult.isSuccess shouldBe true
            
            // Verify it's no longer in favorites
            val favorites = storage.getFavorites().first()
            favorites.any { it.id == movie.id } shouldBe false
            
            // Verify it's not marked as favorite
            storage.isFavorite(movie.id) shouldBe false
        }
    }
    
    "Adding multiple favorites should persist all of them" {
        checkAll(100, Arb.list(movieArb(), 1..10)) { movies ->
            val storage = TestFavoritesStorage()
            
            // Add all movies to favorites
            movies.forEach { movie ->
                storage.addFavorite(movie)
            }
            
            // Verify all are persisted
            val favorites = storage.getFavorites().first()
            val uniqueMovies = movies.distinctBy { it.id }
            favorites.size shouldBe uniqueMovies.size
            
            uniqueMovies.forEach { movie ->
                storage.isFavorite(movie.id) shouldBe true
            }
        }
    }
    
    "Clearing favorites should remove all persisted data" {
        checkAll(100, Arb.list(movieArb(), 1..5)) { movies ->
            val storage = TestFavoritesStorage()
            
            // Add movies to favorites
            movies.forEach { movie ->
                storage.addFavorite(movie)
            }
            
            // Verify they exist
            val favoritesBeforeClear = storage.getFavorites().first()
            favoritesBeforeClear.isNotEmpty() shouldBe true
            
            // Clear favorites
            val clearResult = storage.clearFavorites()
            clearResult.isSuccess shouldBe true
            
            // Verify all are removed
            val favoritesAfterClear = storage.getFavorites().first()
            favoritesAfterClear.isEmpty() shouldBe true
            
            movies.forEach { movie ->
                storage.isFavorite(movie.id) shouldBe false
            }
        }
    }
    
    "Getting favorite IDs should return correct set" {
        checkAll(100, Arb.list(movieArb(), 1..5)) { movies ->
            val storage = TestFavoritesStorage()
            
            // Add movies to favorites
            movies.forEach { movie ->
                storage.addFavorite(movie)
            }
            
            // Get favorite IDs
            val favoriteIds = storage.getFavoriteIds()
            val expectedIds = movies.map { it.id }.toSet()
            
            favoriteIds shouldBe expectedIds
        }
    }
    
    "Adding duplicate favorites should not create duplicates" {
        checkAll(100, movieArb()) { movie ->
            val storage = TestFavoritesStorage()
            
            // Add same movie multiple times
            storage.addFavorite(movie)
            storage.addFavorite(movie)
            storage.addFavorite(movie)
            
            // Verify only one instance exists
            val favorites = storage.getFavorites().first()
            favorites.count { it.id == movie.id } shouldBe 1
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
        releaseDate = Arb.string(10..10).bind(), // YYYY-MM-DD format
        voteAverage = Arb.double(0.0..10.0).bind(),
        genreIds = Arb.list(Arb.int(1..100), 0..5).bind(),
        isFavorite = Arb.boolean().bind()
    )
}