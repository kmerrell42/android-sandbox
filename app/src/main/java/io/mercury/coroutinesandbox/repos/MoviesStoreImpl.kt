package io.mercury.coroutinesandbox.repos

import io.mercury.coroutinesandbox.api.MoviesService
import io.mercury.domain.models.Movie
import io.mercury.domain.repos.MoviesStore

class MoviesStoreImpl(private val moviesService: MoviesService) : MoviesStore {
    override fun getMovies(): List<Movie> {
        return moviesService.getMovies().execute().body()!!
    }
}