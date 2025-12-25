package com.example.kmplibrary.data.repository

import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.data.models.domain.MovieDetail
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing movies data
 */
interface MoviesRepository {
    /**
     * Get popular movies with favorite status
     */
    suspend fun getPopularMovies(page: Int = 1): Flow<List<Movie>>
    
    /**
     * Search movies by query with favorite status
     */
    suspend fun searchMovies(query: String, page: Int = 1): Flow<List<Movie>>
    
    /**
     * Get movie details with favorite status
     */
    suspend fun getMovieDetail(movieId: Int): Result<MovieDetail>
    
    /**
     * Get now playing movies with favorite status
     */
    suspend fun getNowPlayingMovies(page: Int = 1): Flow<List<Movie>>
    
    /**
     * Get top rated movies with favorite status
     */
    suspend fun getTopRatedMovies(page: Int = 1): Flow<List<Movie>>
    
    /**
     * Get upcoming movies with favorite status
     */
    suspend fun getUpcomingMovies(page: Int = 1): Flow<List<Movie>>
    
    /**
     * Refresh cached data
     */
    suspend fun refreshMovies(): Result<Unit>
    
    /**
     * Clear all cached data
     */
    suspend fun clearCache(): Result<Unit>
}