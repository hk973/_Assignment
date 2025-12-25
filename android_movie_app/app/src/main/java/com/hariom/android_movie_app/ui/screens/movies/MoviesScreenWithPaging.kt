package com.hariom.android_movie_app.ui.screens.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.kmplibrary.data.models.domain.Movie
import com.example.kmplibrary.data.paging.MoviesPagingSource
import com.example.kmplibrary.data.repository.MoviesRepository
import com.example.kmplibrary.presentation.viewmodel.MoviesViewModel
import com.hariom.android_movie_app.ui.components.MovieCard
import com.hariom.android_movie_app.ui.components.NoSearchResultsEmptyState
import com.hariom.android_movie_app.ui.components.SearchBar
import com.hariom.android_movie_app.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * Movies screen with Paging 3 support and purple gradient background
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreenWithPaging(
    onMovieClick: (Movie) -> Unit,
    onFavoritesClick: () -> Unit,
    viewModel: MoviesViewModel = koinViewModel(),
    repository: MoviesRepository = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    
    // Paging flow for normal browsing with optimized config
    val moviesPager = remember {
        Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 10,
                enablePlaceholders = false,
                initialLoadSize = 20,
                maxSize = 200
            ),
            pagingSourceFactory = {
                MoviesPagingSource(repository, MoviesPagingSource.MovieType.POPULAR)
            }
        ).flow.cachedIn(coroutineScope)
    }
    
    val lazyPagingItems = moviesPager.collectAsLazyPagingItems()
    
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val separatorPosition = screenHeight * 0.35f // 35% from top
    
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Upper gradient - diagonal (dark top-left to light bottom-right)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f) // Top 35% of screen
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            GradientTopLeftDark,
                            GradientTopRightLight
                        ),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f), // Top-left
                        end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY) // Bottom-right
                    )
                )
        )
        
        // Lower gradient - diagonal light band with dark edges
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            GradientBottomDark, // Dark at top-left
                            GradientBottomLight, // Light in middle diagonal
                            GradientBottomLight, // Light in middle diagonal
                            GradientBottomDark // Dark at bottom-right
                        ),
                        start = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, 0f), // Top-right
                        end = androidx.compose.ui.geometry.Offset(0f, Float.POSITIVE_INFINITY) // Bottom-left
                    )
                )
        )
        
        // Visible separator line at gradient transition
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.TopCenter)
                .offset(y = separatorPosition)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            SeparatorLine.copy(alpha = 0.6f),
                            SeparatorLine,
                            SeparatorLine.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = "Movies",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        ) 
                    },
                    actions = {
                        IconButton(onClick = onFavoritesClick) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorites",
                                tint = IconFavoriteBorder
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = TextPrimary
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Search bar
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.searchMovies(it) },
                    placeholder = "Search movies..."
                )
                
                // Separator line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(SeparatorLine)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Content area
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        uiState.isSearching -> {
                            // Show search results (non-paged)
                            if (uiState.isLoading && uiState.movies.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = PurpleAccent)
                                }
                            } else if (uiState.movies.isEmpty()) {
                                NoSearchResultsEmptyState()
                            } else {
                                // Search results grid (non-paged)
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(MovieDimensions.movieCardWidth),
                                    contentPadding = PaddingValues(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(
                                        count = uiState.movies.size,
                                        key = { index -> uiState.movies[index].id }
                                    ) { index ->
                                        val movie = uiState.movies[index]
                                        MovieCard(
                                            movie = movie,
                                            isFavorite = uiState.favorites.contains(movie.id),
                                            onMovieClick = { onMovieClick(movie) },
                                            onFavoriteClick = { viewModel.toggleFavorite(movie) },
                                            onLongPress = { }
                                        )
                                    }
                                }
                            }
                        }
                        
                        else -> {
                            // Paged movies grid
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(MovieDimensions.movieCardWidth),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(
                                    count = lazyPagingItems.itemCount,
                                    key = lazyPagingItems.itemKey { it.id }
                                ) { index ->
                                    val movie = lazyPagingItems[index]
                                    if (movie != null) {
                                        key(movie.id) {
                                            MovieCard(
                                                movie = movie,
                                                isFavorite = uiState.favorites.contains(movie.id),
                                                onMovieClick = { onMovieClick(movie) },
                                                onFavoriteClick = { viewModel.toggleFavorite(movie) },
                                                onLongPress = { }
                                            )
                                        }
                                    }
                                }
                                
                                // Loading indicator
                                when (lazyPagingItems.loadState.append) {
                                    is LoadState.Loading -> {
                                        item(span = { GridItemSpan(maxLineSpan) }) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(color = PurpleAccent)
                                            }
                                        }
                                    }
                                    is LoadState.Error -> {
                                        item(span = { GridItemSpan(maxLineSpan) }) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                TextButton(onClick = { lazyPagingItems.retry() }) {
                                                    Text("Retry", color = PurpleAccent)
                                                }
                                            }
                                        }
                                    }
                                    else -> {}
                                }
                            }
                            
                            // Initial loading
                            if (lazyPagingItems.loadState.refresh is LoadState.Loading) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = PurpleAccent)
                                }
                            }
                            
                            // Error state
                            if (lazyPagingItems.loadState.refresh is LoadState.Error) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "Failed to load movies",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = TextPrimary
                                        )
                                        Button(
                                            onClick = { lazyPagingItems.retry() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = PurpleAccent
                                            )
                                        ) {
                                            Text("Retry")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
