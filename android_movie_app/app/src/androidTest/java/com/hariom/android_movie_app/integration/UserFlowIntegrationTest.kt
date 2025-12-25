package com.hariom.android_movie_app.integration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hariom.android_movie_app.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for complete user flows
 * Tests end-to-end scenarios like search, favorite, and detail navigation
 */
@RunWith(AndroidJUnit4::class)
class UserFlowIntegrationTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    /**
     * Test: User can navigate from movies list to movie detail
     */
    @Test
    fun userCanNavigateToMovieDetail() {
        // Wait for movies to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("movie_card").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Click on first movie card
        composeTestRule.onAllNodesWithTag("movie_card").onFirst().performClick()
        
        // Verify movie detail screen is displayed
        composeTestRule.onNodeWithTag("movie_detail_screen").assertExists()
    }
    
    /**
     * Test: User can search for movies
     */
    @Test
    fun userCanSearchMovies() {
        // Wait for search bar to appear
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("search_bar").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Type in search bar
        composeTestRule.onNodeWithTag("search_bar").performTextInput("Batman")
        
        // Wait for search results
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("movie_card").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Verify search results are displayed
        composeTestRule.onAllNodesWithTag("movie_card").assertCountEquals(1)
    }
    
    /**
     * Test: User can add and remove favorites
     */
    @Test
    fun userCanToggleFavorites() {
        // Wait for movies to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("movie_card").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Click favorite button on first movie
        composeTestRule.onAllNodesWithTag("favorite_button").onFirst().performClick()
        
        // Navigate to favorites screen
        composeTestRule.onNodeWithContentDescription("Favorites").performClick()
        
        // Verify favorites screen shows the movie
        composeTestRule.onNodeWithTag("favorites_screen").assertExists()
        composeTestRule.onAllNodesWithTag("movie_card").assertCountEquals(1)
        
        // Remove from favorites
        composeTestRule.onAllNodesWithTag("favorite_button").onFirst().performClick()
        
        // Verify empty state is shown
        composeTestRule.onNodeWithText("No favorite movies yet").assertExists()
    }
    
    /**
     * Test: User can navigate to favorites and back
     */
    @Test
    fun userCanNavigateToFavoritesAndBack() {
        // Wait for movies screen to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("movies_screen").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Navigate to favorites
        composeTestRule.onNodeWithContentDescription("Favorites").performClick()
        
        // Verify favorites screen is displayed
        composeTestRule.onNodeWithTag("favorites_screen").assertExists()
        
        // Navigate back
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        
        // Verify movies screen is displayed again
        composeTestRule.onNodeWithTag("movies_screen").assertExists()
    }
    
    /**
     * Test: Complete user flow - search, view detail, add to favorites
     */
    @Test
    fun completeUserFlow_SearchViewDetailAddFavorite() {
        // Wait for movies to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("movie_card").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Search for a movie
        composeTestRule.onNodeWithTag("search_bar").performTextInput("Inception")
        
        // Wait for search results
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("movie_card").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Click on movie to view details
        composeTestRule.onAllNodesWithTag("movie_card").onFirst().performClick()
        
        // Verify detail screen is shown
        composeTestRule.onNodeWithTag("movie_detail_screen").assertExists()
        
        // Add to favorites
        composeTestRule.onNodeWithContentDescription("Add to favorites").performClick()
        
        // Navigate back
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        
        // Clear search
        composeTestRule.onNodeWithContentDescription("Clear search").performClick()
        
        // Navigate to favorites
        composeTestRule.onNodeWithContentDescription("Favorites").performClick()
        
        // Verify movie is in favorites
        composeTestRule.onNodeWithTag("favorites_screen").assertExists()
        composeTestRule.onAllNodesWithTag("movie_card").assertCountEquals(1)
    }
    
    /**
     * Test: Favorites sync across screens
     */
    @Test
    fun favoritesSyncAcrossScreens() {
        // Wait for movies to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("movie_card").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Add first movie to favorites
        composeTestRule.onAllNodesWithTag("favorite_button").onFirst().performClick()
        
        // Click on the same movie to view details
        composeTestRule.onAllNodesWithTag("movie_card").onFirst().performClick()
        
        // Verify favorite status is reflected in detail screen
        composeTestRule.onNodeWithContentDescription("Remove from favorites").assertExists()
        
        // Navigate back
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        
        // Navigate to favorites screen
        composeTestRule.onNodeWithContentDescription("Favorites").performClick()
        
        // Verify movie appears in favorites
        composeTestRule.onAllNodesWithTag("movie_card").assertCountEquals(1)
        
        // Remove from favorites in favorites screen
        composeTestRule.onAllNodesWithTag("favorite_button").onFirst().performClick()
        
        // Navigate back to movies
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        
        // Verify favorite status is updated in movies screen
        composeTestRule.onAllNodesWithTag("favorite_button").onFirst()
            .assertContentDescriptionContains("Add to favorites")
    }
}
