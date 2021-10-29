package io.mercury.coroutinesandbox.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.size.Scale.FILL
import dagger.hilt.android.scopes.ActivityScoped
import io.mercury.coroutinesandbox.interactors.GetMovieSummaries.MovieSummary
import javax.inject.Inject

@ActivityScoped
class MovieCardFactory @Inject constructor(
    private val favoriteButtonFactory: FavoriteButtonFactory,
    private val downloadIndicatorFactory: DownloadIndicatorFactory
    ) {

    @Composable
    fun MovieCard(
        movie: MovieSummary,
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
                            scale(FILL) // This doesn't work as expected
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

                favoriteButtonFactory.FavoriteButton(
                    movie.id,
                    favoriteActionHandler,
                    Modifier.align(Alignment.BottomEnd)
                )
                downloadIndicatorFactory.DownloadIndicator(movie.id)
            }
        }
    }
}