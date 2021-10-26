package io.mercury.domain.repos

import io.mercury.domain.models.Movie

interface MoviesStore {
    fun getMovies(): List<Movie>
}