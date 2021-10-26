package io.mercury.coroutinesandbox.view.favoritemovies

import io.mercury.coroutinesandbox.interactors.GetFavoritedMovies
import io.mercury.coroutinesandbox.models.FavoriteableMovie
import io.mercury.coroutinesandbox.view.favoritemovies.FavoriteMoviesFeature.Action.Load
import io.mercury.coroutinesandbox.view.favoritemovies.FavoriteMoviesFeature.State.Error
import io.mercury.coroutinesandbox.view.favoritemovies.FavoriteMoviesFeature.State.Loaded
import io.mercury.coroutinesandbox.view.favoritemovies.FavoriteMoviesFeature.State.LoadedEmpty
import io.mercury.coroutinesandbox.view.favoritemovies.FavoriteMoviesFeature.State.Loading
import io.mercury.coroutinesandbox.view.favoritemovies.FavoriteMoviesFeature.State.Uninitialized
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteMoviesFeature(
    private val scope: CoroutineScope,
    private val getFavoritedMovies: GetFavoritedMovies
) {

    private val statePublisher = MutableStateFlow<State>(Uninitialized)
    val state get() = statePublisher.asStateFlow()

    private var job: Job? = null

    fun onAction(action: Action) {
        when (action) {
            is Load -> {
                handleLoad()
            }
        }
    }

    private fun handleLoad() {
        if (state.value is Uninitialized) {
            job?.cancel() // cancel previous job
            job = scope.launch {
                updateState(Loading)

                try {
                    withContext(Dispatchers.IO) {
                        getFavoritedMovies()
                            .collect { movies ->
                                if (movies.isEmpty()) {
                                    updateState(LoadedEmpty)
                                } else {
                                    updateState(Loaded(movies))
                                }
                            }
                    }
                } catch (e: Exception) {
                    updateState(Error(e))
                }
            }
        }
    }

    private suspend fun updateState(newState: State) {
        statePublisher.emit(newState)
    }

    sealed class State {
        object Uninitialized : State()
        object Loading : State()
        data class Loaded(val movies: List<FavoriteableMovie>) : State()
        object LoadedEmpty : State()
        data class Error(val exception: Exception) : State()
    }

    sealed class Action {
        object Load : Action()
    }
}