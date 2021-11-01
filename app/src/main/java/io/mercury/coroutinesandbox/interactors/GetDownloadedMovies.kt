package io.mercury.coroutinesandbox.interactors

import io.mercury.coroutinesandbox.repos.MovieDownloadManager
import io.mercury.domain.interactors.GetMovies
import io.mercury.domain.models.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDownloadedMovies @Inject constructor(
    private val downloadManager: MovieDownloadManager,
    private val getMovies: GetMovies
) {
    operator fun invoke(): Flow<List<Movie>> {
        val allMovies = getMovies()
        return downloadManager.downloadMovies.map { downloadedMovieIds ->
            allMovies.filter { downloadedMovieIds.contains(it.id) }
        }
    }
}