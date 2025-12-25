package com.example.kmplibrary.data.models.ui

/**
 * Generic UI state wrapper for handling loading, success, and error states
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
    
    /**
     * Check if the state is loading
     */
    fun isLoading(): Boolean = this is Loading
    
    /**
     * Check if the state is success
     */
    fun isSuccess(): Boolean = this is Success
    
    /**
     * Check if the state is error
     */
    fun isError(): Boolean = this is Error
    
    /**
     * Get data if success, null otherwise
     */
    fun getDataOrNull(): T? = if (this is Success) data else null
    
    /**
     * Get error message if error, null otherwise
     */
    fun getErrorOrNull(): String? = if (this is Error) message else null
}

/**
 * Extension function to map success data
 */
inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> {
    return when (this) {
        is UiState.Loading -> UiState.Loading
        is UiState.Success -> UiState.Success(transform(data))
        is UiState.Error -> this
    }
}

/**
 * Extension function to handle different states
 */
inline fun <T> UiState<T>.fold(
    onLoading: () -> Unit = {},
    onSuccess: (T) -> Unit = {},
    onError: (String, Throwable?) -> Unit = { _, _ -> }
) {
    when (this) {
        is UiState.Loading -> onLoading()
        is UiState.Success -> onSuccess(data)
        is UiState.Error -> onError(message, throwable)
    }
}