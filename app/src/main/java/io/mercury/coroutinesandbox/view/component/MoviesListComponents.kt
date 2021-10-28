package io.mercury.coroutinesandbox.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.size.Scale
import dagger.hilt.android.scopes.ActivityScoped
import io.mercury.coroutinesandbox.interactors.GetMovieSummaries.MovieSummary
import javax.inject.Inject

@Composable
fun RowHeader(text: String) {
    Text(text, style = MaterialTheme.typography.h5, color = MaterialTheme.colors.onBackground)
}

@Composable
fun MoviesRow(
    headerText: String,
    movies: List<MovieSummary>,
    movieCardFactory: MovieCardFactory,
    favoriteActionHandler: (String, Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        RowHeader(headerText)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(movies, { it.id }) { movie ->
                MovieCard(
                    movie, movieCardFactory, favoriteActionHandler
                )
            }
        }
    }
}

@ActivityScoped
data class MovieCardFactory @Inject constructor(
    val favoriteButtonfactory: FavoriteButtonFactory,
    val downloadIndicatorFactory: DownloadIndicatorFactory
)

@Composable
private fun MovieCard(
    movie: MovieSummary,
    movieCardFactory: MovieCardFactory,
    favoriteActionHandler: (String, Boolean) -> Unit
) {
    Card {
        Box(
            Modifier
                .width(140.dp)
                .height(240.dp)
        ) {
            val imageLoaded = remember { mutableStateOf(false) }
            Image(
                rememberImagePainter(data = movie.posterUrl,
                    builder = {
                        scale(Scale.FILL) // This doesn't work as expected
                            .listener(onSuccess = { _, _ -> imageLoaded.value = true })
                    }), movie.title
            )

            // Only show the text title if we aren't showing the poster
            if (!imageLoaded.value) {
                Text(
                    text = movie.title,
                    color = MaterialTheme.colors.primary
                )
            }

            movieCardFactory.favoriteButtonfactory.FavoriteButton(
                movie.id,
                favoriteActionHandler,
                Modifier.align(Alignment.BottomEnd)
            )
            movieCardFactory.downloadIndicatorFactory.DownloadIndicator(movie.id)
        }
    }
}



