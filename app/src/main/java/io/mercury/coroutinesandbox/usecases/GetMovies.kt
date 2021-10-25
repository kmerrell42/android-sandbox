package io.mercury.coroutinesandbox.usecases

import io.mercury.coroutinesandbox.api.MoviesService
import io.mercury.coroutinesandbox.model.Movie
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMovies @Inject constructor(private val moviesService: MoviesService) {
    operator fun invoke(): List<Movie> {
        return moviesService.getMovies().execute().body()!!
    }
}