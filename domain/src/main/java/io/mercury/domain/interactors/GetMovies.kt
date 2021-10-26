package io.mercury.domain.interactors

import io.mercury.domain.models.Movie
import io.mercury.domain.repos.MoviesStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMovies @Inject constructor(private val moviesStore: MoviesStore) {
    operator fun invoke(): List<Movie> {
        return moviesStore.getMovies()
    }
}