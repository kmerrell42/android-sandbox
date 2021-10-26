package io.mercury.coroutinesandbox.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.mercury.coroutinesandbox.R
import io.mercury.coroutinesandbox.models.FavoriteableMovie

@Composable
fun MoviesList(movies: List<FavoriteableMovie>, favoriteActionHandler: (String, Boolean) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        itemsIndexed(movies) { i, movie ->
            MovieItem(
                movie,
                if (i.isEven()) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
                if (i.isEven()) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSecondary,
                favoriteActionHandler
            )
        }
    }
}

@Composable
private fun MovieItem(
    movie: FavoriteableMovie,
    backgroundColor: Color,
    textColor: Color,
    favoriteActionHandler: (String, Boolean) -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp, 4.dp)
    ) {
        Text(
            text = movie.title,
            color = textColor

        )
        FavoriteButton(movie, favoriteActionHandler, Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun FavoriteButton(
    favoriteableMovie: FavoriteableMovie,
    actionHandler: (String, Boolean) -> Unit,
    modifier: Modifier
) {
    Button(onClick = {
        actionHandler(favoriteableMovie.id, !favoriteableMovie.isFavorite)
    }, modifier = modifier) {
        FavoriteIcon(isFavorite = favoriteableMovie.isFavorite)
    }
}

@Composable
private fun FavoriteIcon(isFavorite: Boolean, modifier: Modifier = Modifier) {
    if (isFavorite) {
        R.drawable.ic_favorite_on
    } else {
        R.drawable.ic_favorite_off
    }.let { drawableResource ->
        // TODO: Add conditional contentDescription based on isFavorite
        // TODO: Tint the image
        Image(
            painter = painterResource(id = drawableResource),
            contentDescription = "",
            modifier = modifier
        )
    }
}

private fun Int.isEven(): Boolean {
    return this % 2 == 0
}