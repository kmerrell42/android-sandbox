package io.mercury.coroutinesandbox.view.multirow

import io.mercury.coroutinesandbox.interactors.GetFavoritedMovies
import io.mercury.coroutinesandbox.interactors.GetMovieSummaries
import io.mercury.coroutinesandbox.interactors.GetMovieSummaries.MovieSummary
import io.mercury.coroutinesandbox.view.multirow.MultiRowFeature.MovieCollection.FavoriteCollection
import io.mercury.coroutinesandbox.view.multirow.MultiRowFeature.MovieCollection.StandardCollection
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
    private val getMovieSummaries: GetMovieSummaries,
    private val getFavoritedMovies: GetFavoritedMovies
) {
    private val statePublisher = MutableStateFlow<State>(Uninitialized)
    val state get() = statePublisher.asStateFlow()

    fun load() {
        if (state.value is Uninitialized) {
            handleLoad()
        }
    }

    private fun handleLoad() {
        scope.launch {
            statePublisher.emit(Loading)
            withContext(Dispatchers.IO) {
                getMovieSummaries().combine(getFavoritedMovies()) { all, favorites ->
                    arrayListOf(
                        StandardCollection(all, "All Movies"),
                        FavoriteCollection(favorites, "Your Favorite Movies"),
                        StandardCollection(all.sortedBy { it.title }, "A-Z Movies"),
                        StandardCollection(all.sortedByDescending { it.title }, "Z-A Movies"),
                        StandardCollection(all.sortedBy { it.id }, "More Movies")
                    ).let { collectionList ->
                        State.Loaded(collectionList)
                    }
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
            val movieCollections: List<MovieCollection>
        ) : State()

        data class Error(val e: Exception) : State()
    }

    sealed class MovieCollection {
        abstract val movies: List<MovieSummary>
        abstract val title: String

        data class StandardCollection(
            override val movies: List<MovieSummary>,
            override val title: String
        ) : MovieCollection()

        data class FavoriteCollection(
            override val movies: List<MovieSummary>,
            override val title: String
        ) : MovieCollection()
    }

}