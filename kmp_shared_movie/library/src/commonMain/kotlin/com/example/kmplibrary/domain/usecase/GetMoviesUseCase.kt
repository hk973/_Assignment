package com.example.kmplibrary.domain.usecase

import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.data.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting movies with favorite status
 */
interface GetMoviesUseCase {
    /**
     * Get popular movies
     */
    suspend fun getPopularMovies(page: Int = 1): Flow<List<Movie>>
    
    /**
     * Get now playing movies
     */
    suspend fun getNowPlayingMovies(page: Int = 1): Flow<List<Movie>>
    
    /**
     * Get top rated movies
     */
    suspend fun getTopRatedMovies(page: Int = 1): Flow<List<Movie>>
    
    /**
     * Get upcoming movies
     */
    suspend fun getUpcomingMovies(page: Int = 1): Flow<List<Movie>>
    
    /**
     * Refresh movies data
     */
    suspend fun refreshMovies(): Result<Unit>
}

/**
 * Implementation of GetMoviesUseCase
 */
class GetMoviesUseCaseImpl(
    private val moviesRepository: MoviesRepository
) : GetMoviesUseCase {
    
    override suspend fun getPopularMovies(page: Int): Flow<List<Movie>> {
        return moviesRepository.getPopularMovies(page)
    }
    
    override suspend fun getNowPlayingMovies(page: Int): Flow<List<Movie>> {
        return moviesRepository.getNowPlayingMovies(page)
    }
    
    override suspend fun getTopRatedMovies(page: Int): Flow<List<Movie>> {
        return moviesRepository.getTopRatedMovies(page)
    }
    
    override suspend fun getUpcomingMovies(page: Int): Flow<List<Movie>> {
        return moviesRepository.getUpcomingMovies(page)
    }
    
    override suspend fun refreshMovies(): Result<Unit> {
        return moviesRepository.refreshMovies()
    }
}