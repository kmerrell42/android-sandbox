package io.mercury.coroutinesandbox.view.list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.mercury.coroutinesandbox.model.Movie
import io.mercury.coroutinesandbox.view.list.ListFeature.Action
import io.mercury.coroutinesandbox.view.list.ListFeature.Action.Load
import io.mercury.coroutinesandbox.view.list.ListFeature.State
import io.mercury.coroutinesandbox.view.list.ListFeature.State.Loaded
import io.mercury.coroutinesandbox.view.list.ListFeature.State.Loading
import io.mercury.coroutinesandbox.view.list.ListFeature.State.Uninitialized
import io.mercury.coroutinesandbox.view.theme.ThemedMaterial
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListActivity : ComponentActivity() {

    private val actionPublisher = MutableSharedFlow<Action>(replay = 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model: ListViewModel by viewModels()

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
                    }
                }
            }
        }
    }

    @Composable
    private fun MoviesList(movies: List<Movie>) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            itemsIndexed(movies) { i, movie ->
                MovieItem(
                    movie.name,
                    if (i.isEven()) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
                    if (i.isEven()) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSecondary,
                )
            }
        }
    }

    @Composable
    private fun MovieItem(label: String, backgroundColor: Color, textColor: Color) {
        Box(
            Modifier
                .fillMaxWidth(.5f)
                .background(backgroundColor)
                .padding(16.dp, 4.dp)
        ) {
            Text(
                text = label,
                color = textColor

            )
        }
    }

    private fun Int.isEven(): Boolean {
        return this % 2 == 0
    }
}