package com.example.kmplibrary.data.repository

import com.example.kmplibrary.core.utils.TimeProvider
import com.example.kmplibrary.data.local.FavoritesStorage
import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.data.models.domain.MovieDetail
import com.example.kmplibrary.data.models.mappers.*
import com.example.kmplibrary.data.network.TMDBApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implementation of MoviesRepository that combines API data with local favorites
 */
class MoviesRepositoryImpl(
    private val apiClient: TMDBApiClient,
    private val favoritesStorage: FavoritesStorage
) : MoviesRepository {
    
    private val cacheMutex = Mutex()
    private val movieCache = mutableMapOf<String, List<Movie>>()
    private val movieDetailCache = mutableMapOf<Int, MovieDetail>()
    
    companion object {
        private const val POPULAR_MOVIES_KEY = "popular_movies"
        private const val NOW_PLAYING_KEY = "now_playing"
        private const val TOP_RATED_KEY = "top_rated"
        private const val UPCOMING_KEY = "upcoming"
        private const val CACHE_EXPIRY_MS = 5 * 60 * 1000L // 5 minutes
    }
    
    private var lastCacheTime = 0L
    
    override suspend fun getPopularMovies(page: Int): Flow<List<Movie>> {
        return getMoviesWithFavorites(POPULAR_MOVIES_KEY) {
            apiClient.getPopularMovies(page)
        }
    }
    
    override suspend fun searchMovies(query: String, page: Int): Flow<List<Movie>> {
        return flow {
            val result = apiClient.searchMovies(query, page)
            result.fold(
                onSuccess = { response ->
                    val movies = response.results.toMovieDomain()
                    val moviesWithFavorites = applyFavoriteStatus(movies)
                    emit(moviesWithFavorites)
                },
                onFailure = {
                    emit(emptyList<Movie>())
                }
            )
        }
    }
    
    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> {
        return try {
            // Check cache first
            cacheMutex.withLock {
                movieDetailCache[movieId]?.let { cachedDetail ->
                    val isFavorite = favoritesStorage.isFavorite(movieId)
                    return Result.success(cachedDetail.copy(isFavorite = isFavorite))
                }
            }
            
            // Fetch from API
            val movieDetailResult = apiClient.getMovieDetail(movieId)
            val creditsResult = apiClient.getMovieCredits(movieId)
            
            movieDetailResult.fold(
                onSuccess = { tmdbMovieDetail ->
                    creditsResult.fold(
                        onSuccess = { credits ->
                            val director = credits.getDirector()
                            val cast = credits.getCast()
                            val isFavorite = favoritesStorage.isFavorite(movieId)
                            
                            val movieDetail = tmdbMovieDetail.toDomain(director, cast)
                                .copy(isFavorite = isFavorite)
                            
                            // Cache the result
                            cacheMutex.withLock {
                                movieDetailCache[movieId] = movieDetail
                            }
                            
                            Result.success(movieDetail)
                        },
                        onFailure = { creditsError ->
                            // Still return movie detail without cast/crew if credits fail
                            val isFavorite = favoritesStorage.isFavorite(movieId)
                            val movieDetail = tmdbMovieDetail.toDomain()
                                .copy(isFavorite = isFavorite)
                            
                            Result.success(movieDetail)
                        }
                    )
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getNowPlayingMovies(page: Int): Flow<List<Movie>> {
        return getMoviesWithFavorites(NOW_PLAYING_KEY) {
            apiClient.getNowPlayingMovies(page)
        }
    }
    
    override suspend fun getTopRatedMovies(page: Int): Flow<List<Movie>> {
        return getMoviesWithFavorites(TOP_RATED_KEY) {
            apiClient.getTopRatedMovies(page)
        }
    }
    
    override suspend fun getUpcomingMovies(page: Int): Flow<List<Movie>> {
        return getMoviesWithFavorites(UPCOMING_KEY) {
            apiClient.getUpcomingMovies(page)
        }
    }
    
    override suspend fun refreshMovies(): Result<Unit> {
        return try {
            cacheMutex.withLock {
                movieCache.clear()
                movieDetailCache.clear()
                lastCacheTime = 0L
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun clearCache(): Result<Unit> {
        return refreshMovies()
    }
    
    /**
     * Generic method to get movies with favorites status applied
     */
    private suspend fun getMoviesWithFavorites(
        cacheKey: String,
        apiCall: suspend () -> Result<com.example.kmplibrary.data.models.network.TMDBMoviesResponse>
    ): Flow<List<Movie>> {
        return flow {
            // Check cache first
            val currentTime = TimeProvider.currentTimeMillis()
            val cachedMovies = cacheMutex.withLock {
                if (currentTime - lastCacheTime < CACHE_EXPIRY_MS) {
                    movieCache[cacheKey]
                } else null
            }
            
            if (cachedMovies != null) {
                // Return cached data with updated favorite status
                val moviesWithFavorites = applyFavoriteStatus(cachedMovies)
                emit(moviesWithFavorites)
            } else {
                // Fetch from API
                val result = apiCall()
                result.fold(
                    onSuccess = { response ->
                        val movies = response.results.toMovieDomain()
                        
                        // Cache the movies
                        cacheMutex.withLock {
                            movieCache[cacheKey] = movies
                            lastCacheTime = currentTime
                        }
                        
                        // Apply favorite status and emit
                        val moviesWithFavorites = applyFavoriteStatus(movies)
                        emit(moviesWithFavorites)
                    },
                    onFailure = {
                        // Return cached data if available, otherwise empty list
                        val fallbackMovies = cacheMutex.withLock { movieCache[cacheKey] }
                        if (fallbackMovies != null) {
                            val moviesWithFavorites = applyFavoriteStatus(fallbackMovies)
                            emit(moviesWithFavorites)
                        } else {
                            emit(emptyList<Movie>())
                        }
                    }
                )
            }
        }.combine(favoritesStorage.getFavorites()) { movies, _ ->
            // Reapply favorite status when favorites change
            applyFavoriteStatus(movies)
        }
    }
    
    /**
     * Apply favorite status to a list of movies
     */
    private suspend fun applyFavoriteStatus(movies: List<Movie>): List<Movie> {
        val favoriteIds = favoritesStorage.getFavoriteIds()
        return movies.map { movie ->
            movie.copy(isFavorite = favoriteIds.contains(movie.id))
        }
    }
}