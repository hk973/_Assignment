package com.example.kmplibrary.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.data.repository.MoviesRepository
import kotlinx.coroutines.flow.first

/**
 * PagingSource for loading movies with pagination
 */
class MoviesPagingSource(
    private val repository: MoviesRepository,
    private val movieType: MovieType
) : PagingSource<Int, Movie>() {
    
    enum class MovieType {
        POPULAR, NOW_PLAYING, TOP_RATED, UPCOMING
    }
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1
            
            val moviesFlow = when (movieType) {
                MovieType.POPULAR -> repository.getPopularMovies(page)
                MovieType.NOW_PLAYING -> repository.getNowPlayingMovies(page)
                MovieType.TOP_RATED -> repository.getTopRatedMovies(page)
                MovieType.UPCOMING -> repository.getUpcomingMovies(page)
            }
            
            val movies = moviesFlow.first()
            
            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (movies.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
