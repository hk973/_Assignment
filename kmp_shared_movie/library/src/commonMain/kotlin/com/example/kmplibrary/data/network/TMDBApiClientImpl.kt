package com.example.kmplibrary.data.network

import com.example.kmplibrary.core.config.ApiConfig
import com.example.kmplibrary.core.config.ApiKeyProvider
import com.example.kmplibrary.data.models.network.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.SerializationException

/**
 * Implementation of TMDB API client using Ktor
 */
class TMDBApiClientImpl(
    private val httpClient: HttpClient
) : TMDBApiClient {
    
    private val apiKey = ApiKeyProvider.getTmdbApiKey()
    
    override suspend fun getPopularMovies(page: Int): Result<TMDBMoviesResponse> {
        return safeApiCall {
            httpClient.get("${ApiConfig.TMDB_BASE_URL}movie/popular") {
                parameter("api_key", apiKey)
                parameter("page", page)
            }.body()
        }
    }
    
    override suspend fun searchMovies(query: String, page: Int): Result<TMDBMoviesResponse> {
        return safeApiCall {
            httpClient.get("${ApiConfig.TMDB_BASE_URL}search/movie") {
                parameter("api_key", apiKey)
                parameter("query", query)
                parameter("page", page)
            }.body()
        }
    }
    
    override suspend fun getMovieDetail(movieId: Int): Result<TMDBMovieDetail> {
        return safeApiCall {
            httpClient.get("${ApiConfig.TMDB_BASE_URL}movie/$movieId") {
                parameter("api_key", apiKey)
            }.body()
        }
    }
    
    override suspend fun getMovieCredits(movieId: Int): Result<TMDBCreditsResponse> {
        return safeApiCall {
            httpClient.get("${ApiConfig.TMDB_BASE_URL}movie/$movieId/credits") {
                parameter("api_key", apiKey)
            }.body()
        }
    }
    
    override suspend fun getNowPlayingMovies(page: Int): Result<TMDBMoviesResponse> {
        return safeApiCall {
            httpClient.get("${ApiConfig.TMDB_BASE_URL}movie/now_playing") {
                parameter("api_key", apiKey)
                parameter("page", page)
            }.body()
        }
    }
    
    override suspend fun getTopRatedMovies(page: Int): Result<TMDBMoviesResponse> {
        return safeApiCall {
            httpClient.get("${ApiConfig.TMDB_BASE_URL}movie/top_rated") {
                parameter("api_key", apiKey)
                parameter("page", page)
            }.body()
        }
    }
    
    override suspend fun getUpcomingMovies(page: Int): Result<TMDBMoviesResponse> {
        return safeApiCall {
            httpClient.get("${ApiConfig.TMDB_BASE_URL}movie/upcoming") {
                parameter("api_key", apiKey)
                parameter("page", page)
            }.body()
        }
    }
    
    /**
     * Safe API call wrapper that handles exceptions and converts them to Result
     */
    private suspend inline fun <T> safeApiCall(crossinline apiCall: suspend () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: TimeoutCancellationException) {
            Result.failure(NetworkException.Timeout("Request timed out", e))
        } catch (e: SerializationException) {
            Result.failure(NetworkException.Serialization("Failed to parse response", e))
        } catch (e: Exception) {
            when (e.message?.contains("HTTP", ignoreCase = true)) {
                true -> Result.failure(NetworkException.Http("HTTP error occurred", e))
                else -> Result.failure(NetworkException.Unknown("Unknown network error", e))
            }
        }
    }
}

/**
 * Network exception types for better error handling
 */
sealed class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class Timeout(message: String, cause: Throwable? = null) : NetworkException(message, cause)
    class Http(message: String, cause: Throwable? = null) : NetworkException(message, cause)
    class Serialization(message: String, cause: Throwable? = null) : NetworkException(message, cause)
    class Unknown(message: String, cause: Throwable? = null) : NetworkException(message, cause)
}