package com.hariom.android_movie_app.ui.screens.moviedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.kmplibrary.presentation.viewmodel.MovieDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.input.pointer.pointerInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreenExact(
    movieId: Int,
    onBackClick: () -> Unit,
    viewModel: MovieDetailViewModel = koinViewModel { parametersOf(movieId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(movieId) {
        viewModel.loadMovieDetail(movieId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0B1E))
    ) {

        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }

            uiState.error != null -> {
                Text(
                    text = uiState.error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.movieDetail != null -> {
                val movie = uiState.movieDetail!!

                /* ---------------- BACKGROUND IMAGE (NON-CLICKABLE) ---------------- */
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(movie.getBackdropUrl() ?: movie.getPosterUrl())
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.25f)
                        .pointerInput(Unit) {} // IMPORTANT: allow clicks above
                )

                /* ---------------- TOP BAR ---------------- */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {

                    // LEFT: Back + Rating
                    Column {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF1F1B3A)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    String.format("%.1f", movie.voteAverage),
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // RIGHT: Favorite
                    IconButton(
                        onClick = { viewModel.toggleFavorite() },
                        enabled = !uiState.isTogglingFavorite
                    ) {
                        Icon(
                            imageVector = if (uiState.isFavorite)
                                Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (uiState.isFavorite) Color.Red else Color.White
                        )
                    }
                }

                /* ---------------- SCROLL CONTENT ---------------- */
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(top = 280.dp, bottom = 32.dp)
                ) {

                    /* ---------------- FLOATING MOVIE CARD ---------------- */
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFF1A1638),
                        shadowElevation = 12.dp
                    ) {
                        Column(Modifier.padding(20.dp)) {

                            Text(
                                movie.title,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {

                                movie.getReleaseYear()?.let {
                                    Text(it, color = Color.Gray, fontSize = 13.sp)
                                }

                                Spacer(Modifier.width(8.dp))
                                Text("•", color = Color.Gray)
                                Spacer(Modifier.width(8.dp))

                                movie.getFormattedRuntime()?.let {
                                    Text(it, color = Color.Gray, fontSize = 13.sp)
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // GENRE CHIPS
                            GenreChips(
                                genres = movie.genres.map { it.name }
                            )

                            Spacer(Modifier.height(16.dp))

                            /* ---------------- PLAY BUTTON (NO ACTION) ---------------- */
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(30.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFF9C27FF),
                                                Color(0xFFFF2E93)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayArrow, null, tint = Color.White)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Play Now",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    SectionCard("Synopsis", movie.overview)

                    movie.director?.let {
                        SectionCard("Director", it)
                    }

                    if (movie.cast.isNotEmpty()) {
                        SectionCard(
                            "Cast",
                            movie.getMainCast().joinToString(", ") { it.name }
                        )
                    }
                }
            }
        }
    }
}

/* ---------------- GENRE CHIPS ---------------- */

@Composable
private fun GenreChips(genres: List<String>) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        genres.forEach { genre ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2A2459)
            ) {
                Text(
                    text = genre,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    color = Color(0xFFBB86FC)
                )
            }
        }
    }
}

/* ---------------- SECTION CARD ---------------- */

@Composable
private fun SectionCard(
    title: String,
    content: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF241E48)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Text(content, fontSize = 14.sp, color = Color(0xFFD0D0E0))
        }
    }
}
