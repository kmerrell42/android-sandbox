package io.mercury.coroutinesandbox.interactors

import io.mercury.coroutinesandbox.interactors.GetMovieSummaries.MovieSummary
import io.mercury.coroutinesandbox.repos.FavoriteMoviesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetFavoritedMovies @Inject constructor(
    private val getAllMovies: GetMovieSummaries,
    private val favoriteMoviesManager: FavoriteMoviesManager
) {
    operator fun invoke(): Flow<List<MovieSummary>> {
        return getAllMovies().combine(favoriteMoviesManager.collection) { movies, favorites ->
            movies.filter { favorites.contains(it.id) }
        }
    }
}