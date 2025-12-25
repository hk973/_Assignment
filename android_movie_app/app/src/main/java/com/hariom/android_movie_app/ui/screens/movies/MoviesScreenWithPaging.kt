package com.hariom.android_movie_app.ui.screens.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * Movies screen with Paging 3 support + inline favorites toggle
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreenWithPaging(
    onMovieClick: (Movie) -> Unit,
    viewModel: MoviesViewModel = koinViewModel(),
    repository: MoviesRepository = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    var showFavoritesOnly by rememberSaveable { mutableStateOf(false) }

    /* ---------------- PAGER ---------------- */
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

    /* ---------------- FAVORITES LIST ---------------- */
    val favoriteMovies = remember(uiState.movies, uiState.favorites) {
        uiState.movies.filter { uiState.favorites.contains(it.id) }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        /* ---------------- BACKGROUND GRADIENTS ---------------- */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .background(
                    Brush.linearGradient(
                        colors = listOf(GradientTopLeftDark, GradientTopRightLight)
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            GradientBottomDark,
                            GradientBottomLight,
                            GradientBottomLight,
                            GradientBottomDark
                        )
                    )
                )
        )

        /* ---------------- UI ---------------- */
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (showFavoritesOnly) "Favorites" else "Movies",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { showFavoritesOnly = !showFavoritesOnly }
                        ) {
                            Icon(
                                imageVector = if (showFavoritesOnly)
                                    Icons.Default.Favorite
                                else
                                    Icons.Default.FavoriteBorder,
                                contentDescription = "Toggle favorites",
                                tint = if (showFavoritesOnly)
                                    IconFavorite
                                else
                                    IconFavoriteBorder
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                /* ---------------- SEARCH ---------------- */
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.searchMovies(it) },
                    placeholder = "Search movies..."
                )

                Spacer(Modifier.height(8.dp))

                /* ---------------- CONTENT ---------------- */
                Box(modifier = Modifier.fillMaxSize()) {

                    when {

                        /* ---------- FAVORITES MODE ---------- */
                        showFavoritesOnly -> {
                            if (favoriteMovies.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No favorite movies yet",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextPrimary
                                    )
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(MovieDimensions.movieCardWidth),
                                    contentPadding = PaddingValues(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(
                                        count = favoriteMovies.size,
                                        key = { index -> favoriteMovies[index].id }
                                    ) { index ->
                                        val movie = favoriteMovies[index]
                                        MovieCard(
                                            movie = movie,
                                            isFavorite = true,
                                            onMovieClick = { onMovieClick(movie) },
                                            onFavoriteClick = { viewModel.toggleFavorite(movie) },
                                            onLongPress = { }
                                        )
                                    }
                                }
                            }
                        }

                        /* ---------- SEARCH MODE ---------- */
                        uiState.isSearching -> {
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

                        /* ---------- PAGING MODE ---------- */
                        else -> {
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
                                        MovieCard(
                                            movie = movie,
                                            isFavorite = uiState.favorites.contains(movie.id),
                                            onMovieClick = { onMovieClick(movie) },
                                            onFavoriteClick = { viewModel.toggleFavorite(movie) },
                                            onLongPress = { }
                                        )
                                    }
                                }

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
                        }
                    }
                }
            }
        }
    }
}
