package io.mercury.coroutinesandbox.api

import io.mercury.coroutinesandbox.model.Movie
import retrofit2.Call
import retrofit2.http.GET

interface MoviesService {
    @GET("top_movies.json")
    fun getMovies(): Call<List<Movie>>
}