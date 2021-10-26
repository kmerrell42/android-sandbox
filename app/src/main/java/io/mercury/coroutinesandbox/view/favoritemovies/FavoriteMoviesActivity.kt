package io.mercury.coroutinesandbox.view.favoritemovies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.mercury.coroutinesandbox.repos.FavoriteMoviesManager
import io.mercury.coroutinesandbox.view.component.MoviesList
import io.mercury.coroutinesandbox.view.favoritemovies.FavoriteMoviesFeature.Action
import io.mercury.coroutinesandbox.view.favoritemovies.FavoriteMoviesFeature.Action.Load
import io.mercury.coroutinesandbox.view.favoritemovies.FavoriteMoviesFeature.State
import io.mercury.coroutinesandbox.view.favoritemovies.FavoriteMoviesFeature.State.Loaded
import io.mercury.coroutinesandbox.view.favoritemovies.FavoriteMoviesFeature.State.LoadedEmpty
import io.mercury.coroutinesandbox.view.favoritemovies.FavoriteMoviesFeature.State.Loading
import io.mercury.coroutinesandbox.view.favoritemovies.FavoriteMoviesFeature.State.Uninitialized
import io.mercury.coroutinesandbox.view.theme.ThemedMaterial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteMoviesActivity : ComponentActivity() {

    @Inject
    lateinit var favoriteMoviesManager: FavoriteMoviesManager

    private val actionPublisher = MutableSharedFlow<Action>(replay = 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model: FavoriteMoviesViewModel by viewModels()

        setContent {
            val state by model.feature.state.collectAsState(lifecycleScope.coroutineContext)
            Root(state)
        }

        lifecycleScope.launch { actionPublisher.collect(model.feature::onAction) }
        lifecycleScope.launchWhenCreated { actionPublisher.emit(Load) }
    }

    private fun handleFavoriteAction(id: String, toFavorite: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (toFavorite) {
                favoriteMoviesManager.add(id)
            } else {
                favoriteMoviesManager.remove(id)
            }
        }
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
                            MoviesList(movies = this.movies, ::handleFavoriteAction)
                        }
                        is LoadedEmpty -> {
                            Text(text = "Please add some favorites")
                        }
                        is Error -> {
                            Text(text = "ERROR")
                        }
                    }
                }
            }
        }
    }
}