package io.mercury.coroutinesandbox.view.list

import io.mercury.coroutinesandbox.model.Movie
import io.mercury.coroutinesandbox.usecases.GetMovies
import io.mercury.coroutinesandbox.view.list.ListFeature.Action.Load
import io.mercury.coroutinesandbox.view.list.ListFeature.State.Loaded
import io.mercury.coroutinesandbox.view.list.ListFeature.State.Loading
import io.mercury.coroutinesandbox.view.list.ListFeature.State.Uninitialized
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class ListFeature(private val scope: CoroutineScope, private val getMovies: GetMovies) {

    private val statePublisher = MutableStateFlow<State>(Uninitialized)
    val state get() = statePublisher.asStateFlow()

    fun onAction(action: Action) {
        when (action) {
            is Load -> {
                handleLoad()
            }
        }
    }

    private fun handleLoad() {
        if (state.value is Uninitialized) {
            scope.launch {
                updateState(Loading)
                try {
                    val movies = withContext(kotlinx.coroutines.Dispatchers.IO) {
                        getMovies()
                    }
                    updateState(Loaded(movies))

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
        data class Loaded(val movies: List<Movie>) : State()
        data class Error(val exception: Exception) : State()
    }

    sealed class Action {
        object Load : Action()
    }
}