package io.mercury.coroutinesandbox.interactors

import io.mercury.domain.interactors.GetMovies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMovieSummaries @Inject constructor(
    private val getMovies: GetMovies
) {
    operator fun invoke(): Flow<List<MovieSummary>> {
        return flow {
            emit(getMovies().map { movie ->
                MovieSummary(
                    movie.id,
                    movie.title,
                    movie.posterUrl
                )
            })
        }
    }

    data class MovieSummary(
        val id: String,
        val title: String,
        val posterUrl: String
    )
}