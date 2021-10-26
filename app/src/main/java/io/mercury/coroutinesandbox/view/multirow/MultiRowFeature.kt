package io.mercury.coroutinesandbox.view.multirow

import io.mercury.coroutinesandbox.interactors.GetAllMovies
import io.mercury.coroutinesandbox.interactors.GetFavoritedMovies
import io.mercury.coroutinesandbox.models.FavoriteableMovie
import io.mercury.coroutinesandbox.view.multirow.MultiRowFeature.State.Loading
import io.mercury.coroutinesandbox.view.multirow.MultiRowFeature.State.Uninitialized
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MultiRowFeature(
    private val scope: CoroutineScope,
    private val getAllMovies: GetAllMovies,
    private val getFavoritedMovies: GetFavoritedMovies
) {
    private val statePublisher = MutableStateFlow<State>(Uninitialized)
    val state get() = statePublisher.asStateFlow()

    fun load() {
        when (state.value) {
            is Uninitialized -> {
                handleLoad()
            }
            else -> {
                // no-op
            }
        }
    }

    private fun handleLoad() {
        scope.launch {
            statePublisher.emit(Loading)
            withContext(Dispatchers.IO) {
                getAllMovies().combine(getFavoritedMovies()) { all, favorites ->
                    State.Loaded(all, favorites)

                }.collect { loadedState ->
                    statePublisher.emit(loadedState)
                }
            }
        }
    }

    sealed class State {
        object Uninitialized : State()
        object Loading : State()
        data class Loaded(
            val allMovies: List<FavoriteableMovie>,
            val favoriteMovies: List<FavoriteableMovie>
        ) : State()

        data class Error(val e: Exception) : State()
    }
}