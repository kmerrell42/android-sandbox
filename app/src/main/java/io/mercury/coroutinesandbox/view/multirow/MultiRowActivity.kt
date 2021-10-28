package io.mercury.coroutinesandbox.view.multirow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import io.mercury.coroutinesandbox.view.component.MovieCardFactory
import io.mercury.coroutinesandbox.view.component.MoviesRow
import io.mercury.coroutinesandbox.view.component.RowHeader
import io.mercury.coroutinesandbox.view.multirow.MultiRowFeature.MovieCollection.FavoriteCollection
import io.mercury.coroutinesandbox.view.multirow.MultiRowFeature.MovieCollection.StandardCollection
import io.mercury.coroutinesandbox.view.multirow.MultiRowFeature.State
import io.mercury.coroutinesandbox.view.multirow.MultiRowFeature.State.Error
import io.mercury.coroutinesandbox.view.multirow.MultiRowFeature.State.Loaded
import io.mercury.coroutinesandbox.view.multirow.MultiRowFeature.State.Loading
import io.mercury.coroutinesandbox.view.multirow.MultiRowFeature.State.Uninitialized
import io.mercury.coroutinesandbox.view.theme.ThemedMaterial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MultiRowActivity : ComponentActivity() {

    @Inject
    lateinit var favoriteMoviesManager: FavoriteMoviesManager

    @Inject
    lateinit var movieCaFactory: MovieCardFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model: MultiRowViewModel by viewModels()

        setContent {
            val state by model.feature.state.collectAsState(lifecycleScope.coroutineContext)
            Root(state)
        }

        model.feature.load()
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
                    when (state) {
                        is Uninitialized -> {
                            Text("Uninitialized...")
                        }
                        is Loading -> {
                            Text("Loading...")
                        }
                        is Error -> {
                            Text("ERROR")
                        }
                        is Loaded -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp)) {

                                items(state.movieCollections) { item ->
                                    when (item) {
                                        is StandardCollection -> {
                                            MoviesRow(
                                                "${item.title} (${item.movies.size})",
                                                movies = item.movies,
                                                movieCardFactory = movieCaFactory,
                                                favoriteActionHandler = ::handleFavoriteAction
                                            )
                                        }
                                        is FavoriteCollection -> {
                                            val rowTitle = "${item.title} (${item.movies.size})"
                                            if (item.movies.isEmpty()) {
                                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                                    RowHeader(rowTitle)
                                                    Text(
                                                        "Please favorite some movies",
                                                        color = MaterialTheme.colors.primary
                                                    )
                                                }
                                            } else {
                                                MoviesRow(
                                                    rowTitle,
                                                    movies = item.movies,
                                                    movieCardFactory = movieCaFactory,
                                                    favoriteActionHandler = ::handleFavoriteAction
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}