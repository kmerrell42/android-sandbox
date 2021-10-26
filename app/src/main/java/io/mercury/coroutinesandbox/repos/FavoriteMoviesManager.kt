package io.mercury.coroutinesandbox.repos

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteMoviesManager @Inject constructor(private val favoriteMovieIdsStore: FavoriteMovieIdsStore) {
    private val publisher = MutableSharedFlow<Set<String>>(replay = 1)
    val collection: Flow<Set<String>>
        get() = publisher.asSharedFlow()

    init {
        // Initialize with whatever we have in the store
        publisher.tryEmit(favoriteMovieIdsStore.getFavoriteMovies())
    }

    suspend fun add(id: String) {
        favoriteMovieIdsStore.addFavoriteMovie(id)
        publisher.emit(favoriteMovieIdsStore.getFavoriteMovies())
    }

    suspend fun remove(id: String) {
        favoriteMovieIdsStore.removeFavoriteMovie(id)
        publisher.emit(favoriteMovieIdsStore.getFavoriteMovies())
    }
}