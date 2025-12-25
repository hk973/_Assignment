package com.example.kmplibrary.domain.usecase

import com.example.kmplibrary.data.models.domain.MovieDetail
import com.example.kmplibrary.data.repository.MoviesRepository

/**
 * Use case for getting movie details
 */
interface GetMovieDetailUseCase {
    /**
     * Get detailed information about a movie
     * @param movieId The ID of the movie to get details for
     * @return Result containing MovieDetail with favorite status
     */
    suspend fun getMovieDetail(movieId: Int): Result<MovieDetail>
    
    /**
     * Refresh movie detail data (clear cache)
     */
    suspend fun refreshMovieDetail(): Result<Unit>
}

/**
 * Implementation of GetMovieDetailUseCase
 */
class GetMovieDetailUseCaseImpl(
    private val moviesRepository: MoviesRepository
) : GetMovieDetailUseCase {
    
    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> {
        return try {
            moviesRepository.getMovieDetail(movieId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun refreshMovieDetail(): Result<Unit> {
        return moviesRepository.clearCache()
    }
}