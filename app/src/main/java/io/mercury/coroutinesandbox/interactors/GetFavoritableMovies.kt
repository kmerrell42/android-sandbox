package io.mercury.coroutinesandbox.interactors

import io.mercury.coroutinesandbox.models.FavoriteableMovie
import io.mercury.coroutinesandbox.repos.FavoriteMoviesManager
import io.mercury.domain.interactors.GetMovies
import io.mercury.domain.models.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetFavoritableMovies @Inject constructor(
    private val getMovies: GetMovies,
    private val favoritesStore: FavoriteMoviesManager
) {
    operator fun invoke(): Flow<List<FavoriteableMovie>> {
        val moviesFlow = flowOf(getMovies())
        return favoritesStore.collection
            .combine(moviesFlow) { favorites, movies ->
                movies.mapToFavoritables(favorites)
            }
    }
}

private fun List<Movie>.mapToFavoritables(favorites: Set<String>): List<FavoriteableMovie> {
    return map { movie ->
        FavoriteableMovie(movie.id, movie.title, movie.posterUrl, favorites.contains(movie.id))
    }
}