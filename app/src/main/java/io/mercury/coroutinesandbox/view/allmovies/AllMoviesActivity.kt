package io.mercury.coroutinesandbox.view.allmovies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.mercury.coroutinesandbox.R
import io.mercury.coroutinesandbox.models.FavoriteableMovie
import io.mercury.coroutinesandbox.view.allmovies.AllMoviesFeature.Action
import io.mercury.coroutinesandbox.view.allmovies.AllMoviesFeature.Action.Favor
import io.mercury.coroutinesandbox.view.allmovies.AllMoviesFeature.Action.Load
import io.mercury.coroutinesandbox.view.allmovies.AllMoviesFeature.Action.Unfavor
import io.mercury.coroutinesandbox.view.allmovies.AllMoviesFeature.State
import io.mercury.coroutinesandbox.view.allmovies.AllMoviesFeature.State.Loaded
import io.mercury.coroutinesandbox.view.allmovies.AllMoviesFeature.State.Loading
import io.mercury.coroutinesandbox.view.allmovies.AllMoviesFeature.State.Uninitialized
import io.mercury.coroutinesandbox.view.theme.ThemedMaterial
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllMoviesActivity : ComponentActivity() {

    private val actionPublisher = MutableSharedFlow<Action>(replay = 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model: AllMoviesViewModel by viewModels()

        setContent {
            val state by model.feature.state.collectAsState(lifecycleScope.coroutineContext)
            Root(state)
        }

        lifecycleScope.launch { actionPublisher.collect(model.feature::onAction) }
        lifecycleScope.launchWhenCreated { actionPublisher.emit(Load) }
    }

    @Composable
    fun Root(state: State) {
        ThemedMaterial {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .padding(24.dp)
            ) {

                state.run {
                    when (this) {
                        is Uninitialized,
                        is Loading -> {
                            Text(text = "Loading...")
                        }
                        is Loaded -> {
                            MoviesList(movies = this.movies)
                        }
                        is State.Error -> {
                            Text(text = "ERROR")
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MoviesList(movies: List<FavoriteableMovie>) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            itemsIndexed(movies) { i, movie ->
                MovieItem(
                    movie,
                    if (i.isEven()) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
                    if (i.isEven()) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSecondary,
                )
            }
        }
    }

    @Composable
    private fun MovieItem(
        movie: FavoriteableMovie,
        backgroundColor: Color,
        textColor: Color
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
            FavoriteButton(movie, Modifier.align(Alignment.CenterEnd))
        }
    }

    @Composable
    private fun FavoriteButton(favoriteableMovie: FavoriteableMovie, modifier: Modifier) {
        if (favoriteableMovie.isFavorite) {
            Unfavor(favoriteableMovie.id)
        } else {
            Favor(favoriteableMovie.id)
        }.also { action ->
            Button(onClick = {
                lifecycleScope.launch { actionPublisher.emit(action) }
            }, modifier = modifier) {
                FavoriteIcon(isFavorite = favoriteableMovie.isFavorite)
            }
        }


        if (favoriteableMovie.isFavorite) {
            R.drawable.ic_favorite_on
        } else {
            R.drawable.ic_favorite_off
        }.let { drawableResource ->
            // TODO: Add conditional contentDescription based on isFavorite
            // TODO: Tint the image

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
}