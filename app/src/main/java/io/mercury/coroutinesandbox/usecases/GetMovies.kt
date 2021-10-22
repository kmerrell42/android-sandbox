package io.mercury.coroutinesandbox.usecases

import io.mercury.coroutinesandbox.model.Movie
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMovies @Inject constructor() {
    operator fun invoke(): List<Movie> {
        return arrayListOf(
            Movie("The Matrix"),
            Movie("Spider-man"),
            Movie("Shawshank Redemption"),
            Movie("Big"),
            Movie("Shrek"),
            Movie("Blazing Saddles"),
            Movie("Alice in Wonderland")
        )
    }
}