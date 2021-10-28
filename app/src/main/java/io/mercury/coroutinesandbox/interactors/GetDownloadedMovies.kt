package io.mercury.coroutinesandbox.interactors

import io.mercury.coroutinesandbox.repos.MovieDownloadManager
import io.mercury.domain.interactors.GetMovies
import io.mercury.domain.models.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDownloadedMovies @Inject constructor(
    private val downloadManager: MovieDownloadManager,
    private val getMovies: GetMovies
) {
    operator fun invoke(): Flow<List<Movie>> {
        return flow { emit(getMovies()) }.combine(downloadManager.downloadMovies) { allMovies, downloadedMovieIds ->
            allMovies.filter { downloadedMovieIds.contains(it.id) }
        }

    }
}