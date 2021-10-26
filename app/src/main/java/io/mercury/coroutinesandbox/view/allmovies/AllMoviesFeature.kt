package io.mercury.coroutinesandbox.view.allmovies

import io.mercury.coroutinesandbox.interactors.FavorMovie
import io.mercury.coroutinesandbox.models.FavoriteableMovie
import io.mercury.coroutinesandbox.interactors.GetFavoritableMovies
import io.mercury.coroutinesandbox.interactors.UnfavorMovie
import io.mercury.coroutinesandbox.view.allmovies.AllMoviesFeature.Action.Favor
import io.mercury.coroutinesandbox.view.allmovies.AllMoviesFeature.Action.Load
import io.mercury.coroutinesandbox.view.allmovies.AllMoviesFeature.Action.Unfavor
import io.mercury.coroutinesandbox.view.allmovies.AllMoviesFeature.State.Loaded
import io.mercury.coroutinesandbox.view.allmovies.AllMoviesFeature.State.Loading
import io.mercury.coroutinesandbox.view.allmovies.AllMoviesFeature.State.Uninitialized
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllMoviesFeature(
    private val scope: CoroutineScope,
    private val getMovies: GetFavoritableMovies,
    private val favorMovie: FavorMovie,
    private val unfavorMovie: UnfavorMovie
) {

    private var job: Job? = null

    private val statePublisher = MutableStateFlow<State>(Uninitialized)
    val state get() = statePublisher.asStateFlow()

    fun onAction(action: Action) {
        when (action) {
            is Load -> {
                handleLoad()
            }
            is Favor -> {
                // Doesn't modify the state directly
                scope.launch(Dispatchers.IO) { favorMovie(action.movieId) }
            }
            is Unfavor -> {
                // Doesn't modify the state directly
                scope.launch(Dispatchers.IO) { unfavorMovie(action.movieId) }
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
                        getMovies()
                            .collect { movies ->
                                updateState(Loaded(movies))
                            }
                    }
                } catch (e: Exception) {
                    updateState(State.Error(e))
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
        data class Error(val exception: Exception) : State()
    }

    sealed class Action {
        object Load : Action()
        data class Favor(val movieId: String) : Action()
        data class Unfavor(val movieId: String) : Action()
    }
}