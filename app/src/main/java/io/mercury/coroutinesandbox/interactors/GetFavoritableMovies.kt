package io.mercury.coroutinesandbox.interactors

import io.mercury.coroutinesandbox.models.FavoriteableMovie
import io.mercury.coroutinesandbox.repos.FavoriteMoviesManager
import io.mercury.domain.interactors.GetMovies
import io.mercury.domain.models.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetFavoritableMovies @Inject constructor(
    private val getMovies: GetMovies,
    private val favoritesStore: FavoriteMoviesManager
) {
    operator fun invoke(): Flow<List<FavoriteableMovie>> {
        return favoritesStore.collection
            .map { favorites ->
                getMovies().mapToFavoritables(favorites)
            }
    }
}

private fun List<Movie>.mapToFavoritables(favorites: Set<String>): List<FavoriteableMovie> {
    return map { movie ->
        FavoriteableMovie(movie.id, movie.title, movie.posterUrl, favorites.contains(movie.id))
    }
}