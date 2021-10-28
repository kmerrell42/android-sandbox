package io.mercury.coroutinesandbox.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import io.mercury.coroutinesandbox.R.drawable
import io.mercury.coroutinesandbox.repos.FavoriteMoviesManager
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteButtonFactory @Inject constructor(private val favoriteMoviesManager: FavoriteMoviesManager) {

    @Composable
    fun FavoriteButton(
        id: String,
        actionHandler: (String, Boolean) -> Unit,
        modifier: Modifier
    ) {

        val state = favoriteMoviesManager.collection.map { it.contains(id) }.collectAsState(false)

        FavoriteIcon(state.value,
            modifier.clickable {
                actionHandler(
                    id,
                    !state.value
                )
            })
    }

    @Composable
    private fun FavoriteIcon(isFavorite: Boolean, modifier: Modifier = Modifier) {


        if (isFavorite) {
            drawable.ic_favorite_on
        } else {
            drawable.ic_favorite_off
        }.let { drawableResource ->
            Image(
                painter = painterResource(id = drawableResource),
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                modifier = modifier
            )
        }
    }
}

