package com.example.kmplibrary.data.models.domain

/**
 * Domain model for detailed movie information
 * Used in movie detail screen
 */
data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val runtime: Int?,
    val voteAverage: Double,
    val genres: List<Genre>,
    val director: String?,
    val cast: List<CastMember>,
    val isFavorite: Boolean = false
) {
    /**
     * Get the full poster URL
     */
    fun getPosterUrl(baseUrl: String = "https://image.tmdb.org/t/p/w500"): String? {
        return posterPath?.let { "$baseUrl$it" }
    }
    
    /**
     * Get the full backdrop URL
     */
    fun getBackdropUrl(baseUrl: String = "https://image.tmdb.org/t/p/w780"): String? {
        return backdropPath?.let { "$baseUrl$it" }
    }
    
    /**
     * Get formatted rating (e.g., "8.5")
     */
    fun getFormattedRating(): String {
        return voteAverage.toString()
    }
    
    /**
     * Get release year from date
     */
    fun getReleaseYear(): String? {
        return if (releaseDate.length >= 4) {
            releaseDate.substring(0, 4)
        } else null
    }
    
    /**
     * Get formatted runtime (e.g., "2h 18m")
     */
    fun getFormattedRuntime(): String? {
        return runtime?.let { minutes ->
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            if (hours > 0) {
                "${hours}h ${remainingMinutes}m"
            } else {
                "${remainingMinutes}m"
            }
        }
    }
    
    /**
     * Get genres as comma-separated string
     */
    fun getGenresString(): String {
        return genres.joinToString(", ") { it.name }
    }
    
    /**
     * Get main cast (first 5 members)
     */
    fun getMainCast(): List<CastMember> {
        return cast.take(5)
    }
}