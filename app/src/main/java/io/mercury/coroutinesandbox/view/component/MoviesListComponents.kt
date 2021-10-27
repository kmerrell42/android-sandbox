package io.mercury.coroutinesandbox.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.size.Scale
import io.mercury.coroutinesandbox.R
import io.mercury.coroutinesandbox.models.FavoriteableMovie

@Composable
fun RowHeader(text: String) {
    Text(text, style = MaterialTheme.typography.h5, color = MaterialTheme.colors.onBackground)
}

@Composable
fun MoviesRow(headerText: String, movies: List<FavoriteableMovie>, favoriteActionHandler: (String, Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        RowHeader(headerText)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(movies, { it.id }) { movie ->
                MovieCard(
                    movie, favoriteActionHandler
                )
            }
        }
    }
}

@Composable
private fun MovieCard(
    movie: FavoriteableMovie,
    favoriteActionHandler: (String, Boolean) -> Unit
) {
    Card {
        Box(
            Modifier
                .width(140.dp)
                .height(240.dp)
        ) {
            val imageLoaded = remember { mutableStateOf(false) }
            Image(rememberImagePainter(data = movie.posterUrl,
                builder = {
                    scale(Scale.FILL) // This doesn't work as expected
                        .listener(onSuccess =  { _,_ -> imageLoaded.value = true })
                }), movie.title)

            // Only show the text title if we aren't showing the poster
            if (!imageLoaded.value) {
                Text(
                    text = movie.title,
                    color = MaterialTheme.colors.primary
                )
            }

            FavoriteButton(movie, favoriteActionHandler, Modifier.align(Alignment.BottomEnd))
        }
    }
}

@Composable
private fun FavoriteButton(
    favoriteableMovie: FavoriteableMovie,
    actionHandler: (String, Boolean) -> Unit,
    modifier: Modifier
) {
    FavoriteIcon(
        isFavorite = favoriteableMovie.isFavorite,
        modifier.clickable { actionHandler(favoriteableMovie.id, !favoriteableMovie.isFavorite) })
}

@Composable
private fun FavoriteIcon(isFavorite: Boolean, modifier: Modifier = Modifier) {
    if (isFavorite) {
        R.drawable.ic_favorite_on
    } else {
        R.drawable.ic_favorite_off
    }.let { drawableResource ->
        Image(
            painter = painterResource(id = drawableResource),
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
            modifier = modifier
        )
    }
}