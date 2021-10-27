package io.mercury.coroutinesandbox.interactors

import io.mercury.coroutinesandbox.models.FavoriteableMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetFavoritedMovies @Inject constructor(private val getAllMovies: GetFavoritableMovies) {
    operator fun invoke(): Flow<List<FavoriteableMovie>> {
        return getAllMovies()
            .map { movies ->
                movies.filter { it.isFavorite }
            }
    }
}