package com.example.kmplibrary.data.network

import com.example.kmplibrary.data.models.network.*

/**
 * Interface for TMDB API client
 */
interface TMDBApiClient {
    /**
     * Get popular movies
     */
    suspend fun getPopularMovies(page: Int = 1): Result<TMDBMoviesResponse>
    
    /**
     * Search movies by query
     */
    suspend fun searchMovies(query: String, page: Int = 1): Result<TMDBMoviesResponse>
    
    /**
     * Get movie details by ID
     */
    suspend fun getMovieDetail(movieId: Int): Result<TMDBMovieDetail>
    
    /**
     * Get movie credits (cast and crew) by ID
     */
    suspend fun getMovieCredits(movieId: Int): Result<TMDBCreditsResponse>
    
    /**
     * Get now playing movies
     */
    suspend fun getNowPlayingMovies(page: Int = 1): Result<TMDBMoviesResponse>
    
    /**
     * Get top rated movies
     */
    suspend fun getTopRatedMovies(page: Int = 1): Result<TMDBMoviesResponse>
    
    /**
     * Get upcoming movies
     */
    suspend fun getUpcomingMovies(page: Int = 1): Result<TMDBMoviesResponse>
}