package com.hariom.android_movie_app.ui.screens.moviedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.kmplibrary.presentation.viewmodel.MovieDetailViewModel
import com.hariom.android_movie_app.ui.theme.IconFavorite
import com.hariom.android_movie_app.ui.theme.IconFavoriteBorder
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.graphics.vector.ImageVector

/* ---------------- COLORS ---------------- */

private val CardBackground = Color(0xFF1C1C1E)
private val SectionBackground = Color(0xFF222226)
private val ChipBackground = Color(0xFF2A2A2E)

private val TextPrimary = Color(0xFFF2F2F2)
private val TextSecondary = Color(0xFFB8B8B8)
private val AccentPurple = Color(0xFFB58CFF)

/* ✅ REAL FULL-SCREEN PURPLE GRADIENT */
private val ScreenGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1B1236),
        Color(0xFF2A1459),
        Color(0xFF3B166F),
        Color(0xFF0E0E0E)
    )
)

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

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val heroHeight = screenHeight * 0.72f
    val overlapOffset = 72.dp // controls spacing precisely

    LaunchedEffect(movieId) {
        viewModel.loadMovieDetail(movieId)
    }

    if (uiState.movieDetail == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    val movie = uiState.movieDetail!!

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenGradient) // ✅ gradient visible
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            /* ---------------- HERO IMAGE ---------------- */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heroHeight)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(movie.getBackdropUrl() ?: movie.getPosterUrl())
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        CircleIconButton(
                            onClick = onBackClick,
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Color.White
                        )

                        Spacer(Modifier.height(8.dp))
                        RatingChip(movie.voteAverage)
                    }

                    CircleIconButton(
                        onClick = { viewModel.toggleFavorite() },
                        icon = if (uiState.isFavorite)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,
                        tint = if (uiState.isFavorite)
                            IconFavorite
                        else
                            Color.White
                    )
                }
            }

            /* ---------------- FLOATING CARD ---------------- */
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = -overlapOffset), // ✅ SAFE overlap
                shape = RoundedCornerShape(24.dp),
                color = CardBackground,
                shadowElevation = 12.dp
            ) {
                Column(Modifier.padding(20.dp)) {

                    Text(
                        movie.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(Modifier.height(8.dp))

                    Row {
                        movie.getReleaseYear()?.let {
                            Text(it, color = TextSecondary, fontSize = 13.sp)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("•", color = TextSecondary)
                        Spacer(Modifier.width(8.dp))
                        movie.getFormattedRuntime()?.let {
                            Text(it, color = TextSecondary, fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    GenreChips(movie.genres.map { it.name })
                    Spacer(Modifier.height(16.dp))
                    PlayButton()
                }
            }

            /* 🔥 THIS FIXES GAP: pull content UP */
            Spacer(Modifier.height((-overlapOffset + 16.dp).coerceAtLeast(0.dp)))

            SectionCard("Synopsis", movie.overview)
            movie.director?.let { SectionCard("Director", it) }

            Spacer(Modifier.height(32.dp))
        }
    }
}

/* ---------------- SMALL COMPONENTS ---------------- */

@Composable
private fun RatingChip(rating: Double) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.Black.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(String.format("%.1f", rating), color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
private fun CircleIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    tint: Color
) {
    Surface(shape = CircleShape, color = Color.Black.copy(alpha = 0.55f)) {
        IconButton(onClick = onClick) {
            Icon(icon, null, tint = tint)
        }
    }
}

@Composable
private fun PlayButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF9C27FF), Color(0xFFFF2E93))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PlayArrow, null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Play Now", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GenreChips(genres: List<String>) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        genres.forEach {
            Surface(shape = RoundedCornerShape(12.dp), color = ChipBackground) {
                Text(it, modifier = Modifier.padding(12.dp, 6.dp), fontSize = 12.sp, color = AccentPurple)
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = SectionBackground
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text(content, fontSize = 14.sp, color = TextSecondary)
        }
    }
}
